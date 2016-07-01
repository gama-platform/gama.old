package ummisco.gama.modernOpenGL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import ummisco.gama.modernOpenGL.shader.ShaderProgram;
import ummisco.gama.opengl.ModernRenderer;
import ummisco.gama.opengl.vaoGenerator.TransformationMatrix;

public class ModernDrawer {
	
	private Matrix4f transformationMatrix;

	ShaderProgram shaderProgram;
	int[] vboHandles;
	ArrayList<DrawingEntity> entities = new ArrayList<DrawingEntity>();
	HashMap<String,ArrayList<DrawingEntity>> mapEntities = new HashMap<String,ArrayList<DrawingEntity>>();
	final ModernRenderer renderer;
	GL2 gl;
	
	ArrayList<Integer> listOfVAOUsed = new ArrayList<Integer>();

	static final int COLOR_IDX = 0;
	static final int VERTICES_IDX = 1;
	static final int IDX_BUFF_IDX = 2;
	static final int NORMAL_IDX = 3;
	static final int UVMAPPING_IDX = 4;
	
	public ModernDrawer(ModernRenderer renderer, GL2 gl, ShaderProgram shaderProgram) {
		this.renderer = renderer;
		this.gl = gl;
		this.shaderProgram = shaderProgram;
		
		vboHandles = new int[5];
		this.gl.glGenBuffers(5, vboHandles, 0);
		
		// init map
		mapEntities.put(DrawingEntity.Type.BORDER.toString(), null);
		mapEntities.put(DrawingEntity.Type.FILLED.toString(), null);
		mapEntities.put(DrawingEntity.Type.TEXTURED.toString(), null);
	}
	
	public void addDrawingEntities(DrawingEntity[] entities) {
		for (DrawingEntity entity : entities) {
			if (entity.type.equals(DrawingEntity.Type.BORDER)) {
				addBorderEntity(entity);
			}
			else if (entity.type.equals(DrawingEntity.Type.FILLED)) {
				addFilledEntity(entity);
			}
			else if (entity.type.equals(DrawingEntity.Type.TEXTURED)) {
				addTexturedEntity(entity);
			}
		}
	}
	
	public void addBorderEntity(DrawingEntity newEntity) {
		ArrayList<DrawingEntity> borderEntities = mapEntities.get(DrawingEntity.Type.BORDER.toString());
		ArrayList<DrawingEntity> listToAdd = new ArrayList<DrawingEntity>();
		if (borderEntities == null) {
			listToAdd.add(newEntity);
		}
		else {
			listToAdd = borderEntities;
			DrawingEntity entity = listToAdd.get(0); // only one element for "border"
			// we concatenate newEntity with the other entities
			listToAdd.add(entity.concatenateWith(newEntity));
			// we remove the old entity
			listToAdd.remove(0);
		}
		mapEntities.put(DrawingEntity.Type.BORDER.toString(), listToAdd);
	}
	
	public void addFilledEntity(DrawingEntity newEntity) {
		ArrayList<DrawingEntity> filledEntities = mapEntities.get(DrawingEntity.Type.FILLED.toString());
		ArrayList<DrawingEntity> listToAdd = new ArrayList<DrawingEntity>();
		if (filledEntities == null) {
			listToAdd.add(newEntity);
		}
		else {
			listToAdd = filledEntities;
			// add to the entities with the same material
			boolean entityAdded = false;
			for (int i = 0 ; i < filledEntities.size() ; i++) {
				DrawingEntity entity = filledEntities.get(i);
				if (entity.getMaterial().equalsTo(newEntity.getMaterial())) {
					// same material --> we concatenate newEntity with the other entities with the same material
					listToAdd.add(entity.concatenateWith(newEntity));
					// we remove the old entity
					listToAdd.remove(i);
					// we change the value of the flag
					entityAdded = true;
				}
			}
			if (!entityAdded) {
				// the material of newEntity has not been added yet. Create a new entity
				listToAdd.add(newEntity);
			}
		}
		mapEntities.put(DrawingEntity.Type.FILLED.toString(), listToAdd);
	}
	
	public void addTexturedEntity(DrawingEntity newEntity) {
		ArrayList<DrawingEntity> filledEntities = mapEntities.get(DrawingEntity.Type.TEXTURED.toString());
		ArrayList<DrawingEntity> listToAdd = new ArrayList<DrawingEntity>();
		if (filledEntities == null) {
			listToAdd.add(newEntity);
		}
		else {
			listToAdd = filledEntities;
			// add to the entities with the same material
			boolean entityAdded = false;
			for (int i = 0 ; i < filledEntities.size() ; i++) {
				DrawingEntity entity = filledEntities.get(i);
				if (entity.getMaterial().equalsTo(newEntity.getMaterial()) 
						&& entity.getTextureID() == newEntity.getTextureID()) {
					// same material --> we concatenate newEntity with the other entities with the same material
					listToAdd.add(entity.concatenateWith(newEntity));
					// we remove the old entity
					listToAdd.remove(i);
					// we change the value of the flag
					entityAdded = true;
				}
			}
			if (!entityAdded) {
				// the material of newEntity has not been added yet. Create a new entity
				listToAdd.add(newEntity);
			}
		}
		mapEntities.put(DrawingEntity.Type.TEXTURED.toString(), listToAdd);
	}
	
	public void clearEntityList() {
		entities.clear();
	}
	
	public void draw() {
		
		// Clear screen
//		gl.glClearColor(1, 0, 1, 0.5f);  // Purple
//		gl.glClear(GL2.GL_STENCIL_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT   );
//		
//		gl.glClearDepth(1.0f);
//		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
//		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do

		shaderProgram.start();
			
		transformationMatrix = TransformationMatrix.createTransformationMatrix(new Vector3f(0,0,0), 0, 0, 0, 1);
		shaderProgram.loadTransformationMatrix(transformationMatrix);
		
		Light light = new Light(new Vector3f(50,50,100),new Vector3f(1,1,1));
		shaderProgram.loadLight(light);
		
		for (String key : mapEntities.keySet()) {
			ArrayList<DrawingEntity> listOfEntities = mapEntities.get(key);
			if (listOfEntities != null) {
				for (DrawingEntity entity : listOfEntities) {
					genericDrawMethod(entity,key);
				}
			}
		}
		
		shaderProgram.stop();
		
		mapEntities.clear();
	}
	
	private void genericDrawMethod(DrawingEntity entity, String drawingType) {
		boolean useNormals = (drawingType.equals(DrawingEntity.Type.BORDER.toString())) ? false : true;
		if (useNormals) {
			shaderProgram.enableNormal();
			float shineDamper = entity.getMaterial().getShineDamper();
			float reflectivity = entity.getMaterial().getReflectivity();
			shaderProgram.loadShineVariables(shineDamper,reflectivity);
		}
		else {
			shaderProgram.disableNormal();
		}
		
		float[] vertices = entity.getVertices();
		float[] colors = entity.getColors();
		float[] idxBuffer = entity.getIndices();
		float[] normals = entity.getNormals();
		float[] uvMapping = entity.getUvMapping();


		// VERTICES POSITIONS BUFFER
		storeDataInAttributeList(ShaderProgram.POSITION_ATTRIBUTE_IDX,VERTICES_IDX,vertices);
		
		// COLORS BUFFER (If no texture is defined)
		if (uvMapping == null) {
			storeDataInAttributeList(ShaderProgram.COLOR_ATTRIBUTE_IDX,COLOR_IDX,colors);
		}
		
		// UV MAPPING (If a texture is defined)
		else {
			shaderProgram.loadTexture(0);
			storeDataInAttributeList(ShaderProgram.UVMAPPING_ATTRIBUTE_IDX,UVMAPPING_IDX,uvMapping);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, entity.getTextureID());
		}
		
		// NORMAL BUFFER
		if (useNormals)
			storeDataInAttributeList(ShaderProgram.NORMAL_ATTRIBUTE_IDX,NORMAL_IDX,normals);
		
		// INDEX BUFFER
		int[] intIdxBuff = new int[idxBuffer.length];
		for (int i = 0; i < idxBuffer.length ; i++) {
			intIdxBuff[i] = (int) idxBuffer[i];
		}
		IntBuffer ibIdxBuff = Buffers.newDirectIntBuffer(intIdxBuff);
		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, vboHandles[IDX_BUFF_IDX]);
		int numBytes = idxBuffer.length * 4;
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, numBytes, ibIdxBuff, GL2.GL_STATIC_DRAW);
		ibIdxBuff.rewind();

		if (drawingType.equals(DrawingEntity.Type.BORDER.toString())) {
			// draw border (lines)
			gl.glDrawElements(GL2.GL_LINES, idxBuffer.length, GL2.GL_UNSIGNED_INT, 0);
		}
		else {
			// draw triangles
			gl.glDrawElements(GL2.GL_TRIANGLES, idxBuffer.length, GL2.GL_UNSIGNED_INT, 0);
		}

		releaseVAOMemory();
	}
	
	private void storeDataInAttributeList(int shaderAttributeNumber, int bufferAttributeNumber, float[] data) {
		int coordinateSize = 0;
		switch (shaderAttributeNumber) {
		// recognize the type of VAO to determine the size of the coordinates
			case ShaderProgram.COLOR_ATTRIBUTE_IDX : coordinateSize = 4; break; // r, g, b, a
			case ShaderProgram.POSITION_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case ShaderProgram.NORMAL_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case ShaderProgram.UVMAPPING_ATTRIBUTE_IDX : coordinateSize = 2; break; // u, v
		}
		FloatBuffer fbData = Buffers.newDirectFloatBuffer(data);
		// Select the VBO, GPU memory data, to use for data
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboHandles[bufferAttributeNumber]);
		int numBytes = data.length * 4;
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, fbData, GL2.GL_STATIC_DRAW);
		fbData.rewind(); // It is OK to release CPU after transfer to GPU
		// Associate Vertex attribute 1 with the last bound VBO
		gl.glVertexAttribPointer(shaderAttributeNumber, coordinateSize,
		                    GL2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
		                    0 /* The bound VBO data offset */);
		gl.glEnableVertexAttribArray(shaderAttributeNumber);
		listOfVAOUsed.add(shaderAttributeNumber);
	}
	
	private void releaseVAOMemory() {
		for (Integer vao : listOfVAOUsed) {
			gl.glDisableVertexAttribArray(vao);
		}
		listOfVAOUsed.clear();
	}

}
