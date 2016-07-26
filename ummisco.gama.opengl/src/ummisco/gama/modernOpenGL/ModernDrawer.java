package ummisco.gama.modernOpenGL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import msi.gama.outputs.LightPropertiesStructure;
import msi.gaml.operators.Maths;
import ummisco.gama.modernOpenGL.shader.ShaderProgram;
import ummisco.gama.opengl.ModernRenderer;
import ummisco.gama.opengl.scene.LayerObject;
import ummisco.gama.opengl.vaoGenerator.ModernLayerStructure;
import ummisco.gama.opengl.vaoGenerator.TransformationMatrix;

public class ModernDrawer {

	LayerObject currentLayer;
	HashMap<String,ArrayList<ArrayList<DrawingEntity>>> mapEntities;
	final ModernRenderer renderer;
	GL2 gl;
	
	public boolean isDrawing = true;
	
	ArrayList<Integer> listOfVAOUsed = new ArrayList<Integer>();
	ArrayList<ShaderProgram> shaderLoaded = new ArrayList<ShaderProgram>();
	HashMap<ShaderProgram,int[]> typeOfDrawingMap = new HashMap<ShaderProgram,int[]>();
	
	HashMap<LayerObject,ModernLayerStructure> layerStructureMap = new HashMap<LayerObject,ModernLayerStructure>();
	
	int numberOfShaderInTheCurrentLayer = 0;
	int currentShaderNumber = 0;
	
	static final int COLOR_IDX = 0;
	static final int VERTICES_IDX = 1;
	static final int IDX_BUFF_IDX = 2;
	static final int NORMAL_IDX = 3;
	static final int UVMAPPING_IDX = 4;
	
	public ModernDrawer(ModernRenderer renderer, GL2 gl) {
		this.renderer = renderer;
		this.gl = gl;		
	}
	
	public void prepareMapForLayer(LayerObject layer) {
		// init map
		mapEntities = new HashMap<String,ArrayList<ArrayList<DrawingEntity>>>();
		mapEntities.put(DrawingEntity.Type.LINE.toString(), null);
		mapEntities.put(DrawingEntity.Type.FACE.toString(), null);
		mapEntities.put(DrawingEntity.Type.TEXTURED.toString(), null);
		currentLayer = layer;
		numberOfShaderInTheCurrentLayer=0;
		currentShaderNumber = 0;
	}
	
	public void addDrawingEntities(DrawingEntity[] entities) {
		for (DrawingEntity entity : entities) {
			if (entity.type.equals(DrawingEntity.Type.LINE)) {
				addLineEntity(entity);
			}
			else if (entity.type.equals(DrawingEntity.Type.FACE)) {
				addFilledEntity(entity);
			}
			else if (entity.type.equals(DrawingEntity.Type.TEXTURED)) {
				addTexturedEntity(entity);
			}
			else if (entity.type.equals(DrawingEntity.Type.POINT)) {
				addPointEntity(entity);
			}
		}
	}
	
	public void addLineEntity(DrawingEntity newEntity) {
		// all the line entities are using the same shader. We have to put them all together
		ArrayList<ArrayList<DrawingEntity>> lineEntities = mapEntities.get(DrawingEntity.Type.LINE.toString());
		ArrayList<ArrayList<DrawingEntity>> listToAdd = new ArrayList<ArrayList<DrawingEntity>>();
		if (lineEntities == null) {
			ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
			// we create a new shader and we set it to the entity
			newEntity.setShader(new ShaderProgram(gl));
			numberOfShaderInTheCurrentLayer++;
			entityList.add(newEntity);
			listToAdd.add(entityList);
		}
		else {
			listToAdd = lineEntities;
			// we link the new entity to the shader used for the other entities of the list.
			newEntity.setShader(listToAdd.get(0).get(0).getShader());
			listToAdd.get(0).add(newEntity);
		}
		mapEntities.put(DrawingEntity.Type.LINE.toString(), listToAdd);
	}
	
	public void addPointEntity(DrawingEntity newEntity) {
		// all the point entities are using the same shader. We have to put them all together
		ArrayList<ArrayList<DrawingEntity>> pointEntities = mapEntities.get(DrawingEntity.Type.POINT.toString());
		ArrayList<ArrayList<DrawingEntity>> listToAdd = new ArrayList<ArrayList<DrawingEntity>>();
		if (pointEntities == null) {
			ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
			// we create a new shader and we set it to the entity
			newEntity.setShader(new ShaderProgram(gl));
			numberOfShaderInTheCurrentLayer++;
			entityList.add(newEntity);
			listToAdd.add(entityList);
		}
		else {
			listToAdd = pointEntities;
			// we link the new entity to the shader used for the other entities of the list.
			newEntity.setShader(listToAdd.get(0).get(0).getShader());
			listToAdd.get(0).add(newEntity);
		}
		mapEntities.put(DrawingEntity.Type.POINT.toString(), listToAdd);
	}
	
	public void addFilledEntity(DrawingEntity newEntity) {
		ArrayList<ArrayList<DrawingEntity>> filledEntities = mapEntities.get(DrawingEntity.Type.FACE.toString());
		ArrayList<ArrayList<DrawingEntity>> listToAdd = new ArrayList<ArrayList<DrawingEntity>>();
		if (filledEntities == null) {
			ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
			// we create a new shader and we set it to the entity
			newEntity.setShader(new ShaderProgram(gl));
			numberOfShaderInTheCurrentLayer++;
			entityList.add(newEntity);
			listToAdd.add(entityList);
		}
		else {
			listToAdd = filledEntities;
			// add to the entities with the same material
			boolean entityAdded = false;
			for (int i = 0 ; i < filledEntities.size() ; i++) {
				DrawingEntity entity = filledEntities.get(i).get(0);
				if (entity.getMaterial().equalsTo(newEntity.getMaterial())) {
					// same material --> we add newEntity to the list which use the same shader, and we link the shader used for the other entities of the list to this entity
					newEntity.setShader(listToAdd.get(i).get(0).getShader());
					listToAdd.get(i).add(newEntity);
					// we change the value of the flag
					entityAdded = true;
				}
			}
			if (!entityAdded) {
				// the material of newEntity has not been added yet. Create a new entity
				ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
				// we create a new shader and we set it to the entity
				newEntity.setShader(new ShaderProgram(gl));
				numberOfShaderInTheCurrentLayer++;
				entityList.add(newEntity);
				listToAdd.add(entityList);
			}
		}
		mapEntities.put(DrawingEntity.Type.FACE.toString(), listToAdd);
	}
	
	public void addTexturedEntity(DrawingEntity newEntity) {
		ArrayList<ArrayList<DrawingEntity>> texturedEntities = mapEntities.get(DrawingEntity.Type.TEXTURED.toString());
		ArrayList<ArrayList<DrawingEntity>> listToAdd = new ArrayList<ArrayList<DrawingEntity>>();
		if (texturedEntities == null) {
			ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
			newEntity.setShader(new ShaderProgram(gl));
			numberOfShaderInTheCurrentLayer++;
			entityList.add(newEntity);
			listToAdd.add(entityList);
		}
		else {
			listToAdd = texturedEntities;
			// add to the entities with the same material
			boolean entityAdded = false;
			for (int i = 0 ; i < texturedEntities.size() ; i++) {
				DrawingEntity entity = texturedEntities.get(i).get(0);
				if (entity.getMaterial().equalsTo(newEntity.getMaterial()) 
						&& entity.getTextureID() == newEntity.getTextureID()) {
					// same material, same texture --> we add newEntity to the list which use the same shader, and we link the shader used for the other entities of the list to this entity
					newEntity.setShader(listToAdd.get(i).get(0).getShader());
					listToAdd.get(i).add(newEntity);
					// we change the value of the flag
					entityAdded = true;
				}
			}
			if (!entityAdded) {
				// the material of newEntity has not been added yet. Create a new entity
				ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
				// we create a new shader and we set it to the entity
				newEntity.setShader(new ShaderProgram(gl));
				numberOfShaderInTheCurrentLayer++;
				entityList.add(newEntity);
				listToAdd.add(entityList);
			}
		}
		mapEntities.put(DrawingEntity.Type.TEXTURED.toString(), listToAdd);
	}
	
	public void clearVBO() {
		for (Integer vao : listOfVAOUsed) {
			gl.glDisableVertexAttribArray(vao);
		}
		listOfVAOUsed.clear();
		shaderLoaded.clear();
		typeOfDrawingMap.clear();
		layerStructureMap.clear();
	}
	
	public void redraw() {
		
		int[] vboHandles = new int[numberOfShaderInTheCurrentLayer*5];
		this.gl.glGenBuffers(numberOfShaderInTheCurrentLayer*5, vboHandles, 0);
		ModernLayerStructure layerStructure = new ModernLayerStructure();
		layerStructure.vboHandles = vboHandles;
		layerStructureMap.put(currentLayer, layerStructure);
		
		for (String key : mapEntities.keySet()) {
			ArrayList<ArrayList<DrawingEntity>> listOfListOfEntities = mapEntities.get(key);
			if (listOfListOfEntities != null) {
				
				for (ArrayList<DrawingEntity> listOfEntities : listOfListOfEntities) {
					// all those entities are using the same shader
					ShaderProgram shaderProgram = listOfEntities.get(0).getShader();
					shaderLoaded.add(shaderProgram);
					shaderProgram.start();
					
					prepareShader(listOfEntities.get(0),key,shaderProgram);
					
					drawLights(renderer.data.getDiffuseLights());
					loadVBO(listOfEntities,key,currentShaderNumber);
					drawVBO(typeOfDrawingMap.get(shaderProgram),currentShaderNumber);
					
					shaderProgram.stop();
					currentShaderNumber++;
				}
			}
		}
		
		layerStructure = layerStructureMap.get(currentLayer);
		layerStructure.shaderList = (ArrayList<ShaderProgram>) shaderLoaded.clone();
		layerStructureMap.put(currentLayer, layerStructure);
		
		shaderLoaded.clear();
		mapEntities.clear();
		
	}
	
	public void refresh(LayerObject layer) {
		ArrayList<ShaderProgram> shaderList = layerStructureMap.get(currentLayer).shaderList;
		
		for (ShaderProgram shader : shaderList) {
			// set the current layer drawn
			currentLayer = layer;
			
			shader.start();
			
			shader.loadViewMatrix(renderer.camera);
			shader.loadProjectionMatrix(renderer.getProjectionMatrix());
			shader.loadTransformationMatrix(getTransformationMatrix());
			int[] typeOfDrawing = typeOfDrawingMap.get(shader);
			
			///////////////////////////////////////:
			// VERTICES POSITIONS BUFFER
			storeDataInAttributeListBis(ShaderProgram.POSITION_ATTRIBUTE_IDX,VERTICES_IDX,typeOfDrawing[2]);
			
			// COLORS BUFFER (If no texture is defined)
			if (!shader.useTexture())
			{
				storeDataInAttributeListBis(ShaderProgram.COLOR_ATTRIBUTE_IDX,COLOR_IDX,typeOfDrawing[2]);
			}
			else {
				storeDataInAttributeListBis(ShaderProgram.UVMAPPING_ATTRIBUTE_IDX,UVMAPPING_IDX,typeOfDrawing[2]);
				gl.glActiveTexture(GL.GL_TEXTURE0);
				//gl.glBindTexture(GL.GL_TEXTURE_2D, listEntities.get(0).getTextureID()); TODO
			}
			
			// NORMAL BUFFER
			if (shader.useNormal())
				storeDataInAttributeListBis(ShaderProgram.NORMAL_ATTRIBUTE_IDX,NORMAL_IDX,typeOfDrawing[2]);
			
			// INDEX BUFFER
			// Select the VBO, GPU memory data, to use for colors
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, layerStructureMap.get(currentLayer).vboHandles[typeOfDrawing[2]*5+IDX_BUFF_IDX]);
			//////////////////////////////////
			
			drawVBO(typeOfDrawing,typeOfDrawing[2]);
			
			shader.stop();
		}
	}
	
	private void drawLights(List<LightPropertiesStructure> lights) {
		for (LightPropertiesStructure light : lights) {
			if (light.isDrawLight()) {
				
			}
		}
	}
	
	private boolean useNormals(String drawingType) {
		if (drawingType.equals(DrawingEntity.Type.LINE.toString())
				|| drawingType.equals(DrawingEntity.Type.POINT.toString()))
			return false;
		return true;
	}
	
	private void drawVBO(int[] typeOfDrawing, int shaderNumber) {
		gl.glDrawElements(typeOfDrawing[0], typeOfDrawing[1], GL2.GL_UNSIGNED_INT, 0);
	}
	
	private void prepareShader(DrawingEntity entity, String drawingType, ShaderProgram shaderProgram) {
		shaderProgram.loadTransformationMatrix(getTransformationMatrix());
		shaderProgram.loadViewMatrix(renderer.camera);
		shaderProgram.loadProjectionMatrix(renderer.getProjectionMatrix());
		
		shaderProgram.loadAmbientLight(new Vector3f(
				(float) renderer.data.getAmbientLightColor().getRed() / 255f,
				(float) renderer.data.getAmbientLightColor().getGreen() / 255f,
				(float) renderer.data.getAmbientLightColor().getBlue() / 255f));
		shaderProgram.loadDiffuseLights(renderer.data.getDiffuseLights());
		boolean useNormals = useNormals(drawingType);
		if (useNormals) {
			shaderProgram.enableNormal();
			float shineDamper = (float) entity.getMaterial().getShineDamper();
			float reflectivity = (float) entity.getMaterial().getReflectivity();
			shaderProgram.loadShineVariables(shineDamper,reflectivity);
		}
		else {
			shaderProgram.disableNormal();
		}
		
		if (entity.getUvMapping() == null) {
			shaderProgram.disableTexture();
		}
		else {
			shaderProgram.enableTexture();
			shaderProgram.loadTexture(0);
		}
	}
	
	private Matrix4f getTransformationMatrix() {
		
		Vector3f layerTranslation = new Vector3f( (float)currentLayer.getOffset().x,
				(float)currentLayer.getOffset().y,
				(float)currentLayer.getOffset().z);
		float[] quat = new float[]{0,0,1,(float) Math.toRadians(renderer.getZRotation())};
		float scale = (float)currentLayer.getScale().x;
		
		final float env_width = (float) renderer.data.getEnvWidth();
		final float env_height = (float) renderer.data.getEnvHeight();
		return TransformationMatrix.createTransformationMatrix(layerTranslation, quat, scale, env_width, env_height);
	}
	
	private void loadVBO(ArrayList<DrawingEntity> listEntities, String drawingType, int shaderNumber) {
		
		ArrayList<float[]> listVertices = new ArrayList<float[]>();
		ArrayList<float[]> listColors = new ArrayList<float[]>();
		ArrayList<float[]> listIdxBuffer = new ArrayList<float[]>();
		ArrayList<float[]> listNormals = new ArrayList<float[]>();
		ArrayList<float[]> listUvMapping = new ArrayList<float[]>();
		for (DrawingEntity entity : listEntities) {
			listVertices.add(entity.getVertices());
			listColors.add(entity.getColors());
			listIdxBuffer.add(entity.getIndices());
			listNormals.add(entity.getNormals());
			if (entity.getUvMapping() != null)
				listUvMapping.add(entity.getUvMapping());
		}


		// VERTICES POSITIONS BUFFER
		storeDataInAttributeList(ShaderProgram.POSITION_ATTRIBUTE_IDX,VERTICES_IDX,listVertices,shaderNumber);
		
		// COLORS BUFFER (If no texture is defined)
		if (listUvMapping.size() == 0) {
			storeDataInAttributeList(ShaderProgram.COLOR_ATTRIBUTE_IDX,COLOR_IDX,listColors,shaderNumber);
		}
		
		// UV MAPPING (If a texture is defined)
		else {
			storeDataInAttributeList(ShaderProgram.UVMAPPING_ATTRIBUTE_IDX,UVMAPPING_IDX,listUvMapping,shaderNumber);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, listEntities.get(0).getTextureID());
		}
		
		// NORMAL BUFFER
		if (useNormals(drawingType))
			storeDataInAttributeList(ShaderProgram.NORMAL_ATTRIBUTE_IDX,NORMAL_IDX,listNormals,shaderNumber);
		
		// INDEX BUFFER
		int sizeIdxBuffer = 0;
		for (float[] idxBuffer : listIdxBuffer) {
			sizeIdxBuffer += idxBuffer.length;
		}
		int[] intIdxBuffer = new int[sizeIdxBuffer];
		
		int cpt = 0;
		int offset = 0;
		for (int i = 0 ; i < listIdxBuffer.size() ; i++) {
			float[] idxBuffer = listIdxBuffer.get(i);
			int maxIdx = 0;
			for (int j = 0 ; j < idxBuffer.length ; j++) {
				if ((int)idxBuffer[j]>maxIdx) {maxIdx = (int) idxBuffer[j];}
				intIdxBuffer[offset+j] = (int) idxBuffer[j] + cpt ;
			}
			offset += idxBuffer.length;
			cpt += maxIdx+1;
		}
		IntBuffer ibIdxBuff = Buffers.newDirectIntBuffer(intIdxBuffer);
		// Select the VBO, GPU memory data, to use for colors
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, layerStructureMap.get(currentLayer).vboHandles[shaderNumber*5+IDX_BUFF_IDX]);
		int numBytes = intIdxBuffer.length * 4;
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, numBytes, ibIdxBuff, GL2.GL_STATIC_DRAW);
		//ibIdxBuff.rewind();

		int[] newElement = new int[3];
		if (drawingType.equals(DrawingEntity.Type.POINT.toString())) {
			// particular case : drawing just a point
			newElement[0] = GL2.GL_POINTS;
		}
		else if (drawingType.equals(DrawingEntity.Type.LINE.toString())) {
			// draw border (lines)
			newElement[0] = GL2.GL_LINES;
		}
		else {
			// draw triangles
			newElement[0] = GL2.GL_TRIANGLES;
		}
		newElement[1] = intIdxBuffer.length;
		newElement[2] = shaderNumber;
		typeOfDrawingMap.put(listEntities.get(0).getShader(),newElement);
	}
	
	private void storeDataInAttributeList(int shaderAttributeNumber, int bufferAttributeNumber, ArrayList<float[]> listData, int shaderNumber) {
		int coordinateSize = 0;
		switch (shaderAttributeNumber) {
		// recognize the type of VAO to determine the size of the coordinates
			case ShaderProgram.COLOR_ATTRIBUTE_IDX : coordinateSize = 4; break; // r, g, b, a
			case ShaderProgram.POSITION_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case ShaderProgram.NORMAL_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case ShaderProgram.UVMAPPING_ATTRIBUTE_IDX : coordinateSize = 2; break; // u, v
		}
		// Select the VBO, GPU memory data, to use for data
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, layerStructureMap.get(currentLayer).vboHandles[shaderNumber*5+bufferAttributeNumber]);
		// compute the total size of the buffer :
		int numBytes = 0;
		for (float[] data : listData) {
			numBytes += data.length * 4;
		}
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, numBytes, null, GL2.GL_STATIC_DRAW);
		
		int offset = 0;
		for (float[] data : listData)
		{
			FloatBuffer fbData = Buffers.newDirectFloatBuffer(data/*totalData,positionInBuffer*/);
			gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, offset, data.length*4, fbData);
			offset += data.length*4;
			//fbData.rewind(); // It is OK to release CPU after transfer to GPU
		}
		
		// Associate Vertex attribute with the last bound VBO
		gl.glVertexAttribPointer(shaderAttributeNumber, coordinateSize,
		                    GL2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
		                    0 /* The bound VBO data offset */);
		gl.glEnableVertexAttribArray(shaderAttributeNumber);
		if (!listOfVAOUsed.contains(shaderAttributeNumber))
		{
			listOfVAOUsed.add(shaderAttributeNumber);
		}
	}
	
	private void storeDataInAttributeListBis(int shaderAttributeNumber, int bufferAttributeNumber, int shaderNumber) {
		int coordinateSize = 0;
		switch (shaderAttributeNumber) {
		// recognize the type of VAO to determine the size of the coordinates
			case ShaderProgram.COLOR_ATTRIBUTE_IDX : coordinateSize = 4; break; // r, g, b, a
			case ShaderProgram.POSITION_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case ShaderProgram.NORMAL_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case ShaderProgram.UVMAPPING_ATTRIBUTE_IDX : coordinateSize = 2; break; // u, v
		}
		// Select the VBO, GPU memory data, to use for data
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, layerStructureMap.get(currentLayer).vboHandles[shaderNumber*5+bufferAttributeNumber]);
		
		// Associate Vertex attribute with the last bound VBO
		gl.glVertexAttribPointer(shaderAttributeNumber, coordinateSize,
		                    GL2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
		                    0 /* The bound VBO data offset */);
	}

}
