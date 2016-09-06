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

import ummisco.gama.modernOpenGL.shader.AbstractShader;
import ummisco.gama.modernOpenGL.shader.BillboardingTextShaderProgram;
import ummisco.gama.modernOpenGL.shader.ShaderProgram;
import ummisco.gama.modernOpenGL.shader.TextShaderProgram;
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
	ArrayList<AbstractShader> shaderLoaded = new ArrayList<AbstractShader>();
	HashMap<AbstractShader,int[]> typeOfDrawingMap = new HashMap<AbstractShader,int[]>();
	
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
		mapEntities.put(DrawingEntity.Type.BILLBOARDING.toString(), null);
		mapEntities.put(DrawingEntity.Type.POINT.toString(), null);
		currentLayer = layer;
		numberOfShaderInTheCurrentLayer=0;
		currentShaderNumber = 0;
	}
	
	public void addDrawingEntities(DrawingEntity[] entities) {
		for (DrawingEntity entity : entities) {
			addDrawingEntities(entity,entity.type);
		}
	}
	
	private void addDrawingEntities(DrawingEntity newEntity, DrawingEntity.Type type) {
		ArrayList<ArrayList<DrawingEntity>> entities = mapEntities.get(type.toString());
		ArrayList<ArrayList<DrawingEntity>> listToAdd = new ArrayList<ArrayList<DrawingEntity>>();
		if (entities == null) {
			ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
			// we create a new shader and we set it to the entity
			setShaderToEntity(newEntity,type);
			numberOfShaderInTheCurrentLayer++;
			entityList.add(newEntity);
			listToAdd.add(entityList);
		}
		else {
			listToAdd = entities;
			boolean entityAdded = false;
			for (int i = 0 ; i < entities.size() ; i++) {
				DrawingEntity entity = entities.get(i).get(0);
				if (addEntityToList(entity,newEntity,type)) {
					// if the entity can be mixed to the list of entities, we add newEntity to the list which use the same shader, and we link the shader used for the other entities of the list to this entity
					newEntity.setShader(listToAdd.get(i).get(0).getShader());
					listToAdd.get(i).add(newEntity);
					// we change the value of the flag
					entityAdded = true;
				}
			}
			if (!entityAdded) {
				// the entity to add cannot be mixed with other entities. We create a new entity list to the map.
				ArrayList<DrawingEntity> entityList = new ArrayList<DrawingEntity>();
				// we create a new shader and we set it to the entity
				setShaderToEntity(newEntity,type);
				numberOfShaderInTheCurrentLayer++;
				entityList.add(newEntity);
				listToAdd.add(entityList);
			}
		}
		mapEntities.put(type.toString(), listToAdd);
	}
	
	private void setShaderToEntity(DrawingEntity newEntity, DrawingEntity.Type type) {
		if (type.equals(DrawingEntity.Type.BILLBOARDING)) {
			newEntity.setShader(new BillboardingTextShaderProgram(gl));
		}
		else if (type.equals(DrawingEntity.Type.STRING)) {
			newEntity.setShader(new TextShaderProgram(gl));
		}
		else {
			newEntity.setShader(new ShaderProgram(gl));
		}
	}
	
	private boolean addEntityToList(DrawingEntity entity, DrawingEntity newEntity, DrawingEntity.Type type) {
		if (type.equals(DrawingEntity.Type.LINE)) {
			return true;
		}
		else if (type.equals(DrawingEntity.Type.FACE)) {
			return entity.getMaterial().equalsTo(newEntity.getMaterial());
		}
		else if (type.equals(DrawingEntity.Type.TEXTURED) || type.equals(DrawingEntity.Type.STRING)) {
			return (entity.getMaterial().equalsTo(newEntity.getMaterial()) 
					&& entity.getTextureID() == newEntity.getTextureID());
		}
		else if (type.equals(DrawingEntity.Type.POINT)) {
			return true;
		}
		return false;
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
		
		if (numberOfShaderInTheCurrentLayer == 0) {
			return; // if nothing is to draw for this layer, do nothing.
		}
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
					AbstractShader shaderProgram = listOfEntities.get(0).getShader();
					shaderLoaded.add(shaderProgram);
					shaderProgram.start();
					
					if (shaderProgram instanceof BillboardingTextShaderProgram) {
						((BillboardingTextShaderProgram)shaderProgram).setTranslation(listOfEntities.get(0).getTranslation()); // FIXME : need refactoring
					}
					updateTransformationMatrix(shaderProgram);
					prepareShader(listOfEntities.get(0), shaderProgram);
					
					loadVBO(listOfEntities,key,currentShaderNumber,shaderProgram);
					drawVBO(typeOfDrawingMap.get(shaderProgram),currentShaderNumber);
					
					shaderProgram.stop();
					currentShaderNumber++;
				}
			}
		}
		
		layerStructure = layerStructureMap.get(currentLayer);
		layerStructure.shaderList = (ArrayList<AbstractShader>) shaderLoaded.clone();
		layerStructureMap.put(currentLayer, layerStructure);
		
		shaderLoaded.clear();
		mapEntities.clear();
		
	}
	
	public void refresh(LayerObject layer) {
		currentLayer = layer;
		if (layerStructureMap.get(currentLayer) == null) {
			return; // if nothing is to draw for this layer, do nothing.
		}
		ArrayList<AbstractShader> shaderList = layerStructureMap.get(currentLayer).shaderList;
		for (AbstractShader shader : shaderList) {
			// set the current layer drawn
			
			shader.start();
			
			updateTransformationMatrix(shader);
			int[] typeOfDrawing = typeOfDrawingMap.get(shader);
			
			///////////////////////////////////////:
			// VERTICES POSITIONS BUFFER
			bindBuffer(AbstractShader.POSITION_ATTRIBUTE_IDX,VERTICES_IDX,typeOfDrawing[2]);
			
			// COLORS BUFFER
			bindBuffer(AbstractShader.COLOR_ATTRIBUTE_IDX,COLOR_IDX,typeOfDrawing[2]);
			
			// UV MAPPING (If a texture is defined)
			if (shader.useTexture())
			{
				bindBuffer(AbstractShader.UVMAPPING_ATTRIBUTE_IDX,UVMAPPING_IDX,typeOfDrawing[2]);
				gl.glActiveTexture(GL.GL_TEXTURE0);
				gl.glBindTexture(GL.GL_TEXTURE_2D, shader.getTextureID());
			}
			
			// NORMAL BUFFER
			if (shader.useNormal())
				bindBuffer(AbstractShader.NORMAL_ATTRIBUTE_IDX,NORMAL_IDX,typeOfDrawing[2]);
			
			// INDEX BUFFER
			// Select the VBO, GPU memory data, to use for colors
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, layerStructureMap.get(currentLayer).vboHandles[typeOfDrawing[2]*5+IDX_BUFF_IDX]);
			//////////////////////////////////
			
			drawVBO(typeOfDrawing,typeOfDrawing[2]);
			
			shader.stop();
		}
	}
	
	private void drawVBO(int[] typeOfDrawing, int shaderNumber) {
		gl.glDrawElements(typeOfDrawing[0], typeOfDrawing[1], GL2.GL_UNSIGNED_INT, 0);
	}
	
	private void updateTransformationMatrix(AbstractShader shaderProgram) {
		if (shaderProgram instanceof BillboardingTextShaderProgram) {
			updateTransformationMatrix((BillboardingTextShaderProgram)shaderProgram);
		}
		else {
			shaderProgram.loadTransformationMatrix(getTransformationMatrix());
			shaderProgram.loadViewMatrix(renderer.camera);
			shaderProgram.loadProjectionMatrix(renderer.getProjectionMatrix());
		}
	}
	
	private void updateTransformationMatrix(BillboardingTextShaderProgram shaderProgram) {
		shaderProgram.loadProjectionMatrix(renderer.getProjectionMatrix());
		
		Matrix4f viewMatrix = TransformationMatrix.createViewMatrix(renderer.camera);
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
		// set the translation
		Vector3f entityTranslation = shaderProgram.getTranslation();
		modelMatrix.m30 = entityTranslation.x;
		modelMatrix.m31 = entityTranslation.y;
		modelMatrix.m32 = entityTranslation.z;
		// reset the rotation
		modelMatrix.m00 = viewMatrix.m00;
		modelMatrix.m01 = viewMatrix.m10;
		modelMatrix.m02 = viewMatrix.m20;
		modelMatrix.m10 = viewMatrix.m01;
		modelMatrix.m11 = viewMatrix.m11;
		modelMatrix.m12 = viewMatrix.m21;
		modelMatrix.m20 = viewMatrix.m02;
		modelMatrix.m21 = viewMatrix.m12;
		modelMatrix.m22 = viewMatrix.m22;
		// compute modelViewMatrix
		Matrix4f modelViewMatrix = modelMatrix;
		modelViewMatrix.mul(viewMatrix);
		// inverse y axis
		modelViewMatrix.m11 = -1;
		
		shaderProgram.loadModelViewMatrix(modelViewMatrix);
	}
	
	private void prepareShader(DrawingEntity entity, AbstractShader shaderProgram) {
		if (shaderProgram instanceof ShaderProgram) {
			prepareShader(entity, (ShaderProgram)shaderProgram);
		}
		else if (shaderProgram instanceof BillboardingTextShaderProgram) {
			prepareShader(entity, (BillboardingTextShaderProgram)shaderProgram);
		}
		else if (shaderProgram instanceof TextShaderProgram) {
			prepareShader(entity, (TextShaderProgram)shaderProgram);
		}
	}
	
	private void prepareShader(DrawingEntity entity, ShaderProgram shaderProgram) {
		shaderProgram.loadAmbientLight(new Vector3f(
				(float) renderer.data.getAmbientLightColor().getRed() / 255f,
				(float) renderer.data.getAmbientLightColor().getGreen() / 255f,
				(float) renderer.data.getAmbientLightColor().getBlue() / 255f));
		shaderProgram.loadLights(renderer.data.getDiffuseLights());
		boolean useNormals = entity.getMaterial().useLight;
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
			shaderProgram.storeTextureID(entity.getTextureID());
		}
		if (entity.type == DrawingEntity.Type.STRING) {
			shaderProgram.enableString();
			shaderProgram.loadFontWidth(entity.getFontWidth());
			shaderProgram.loadFontEdge(entity.getFontEdge());
		}
		else {
			shaderProgram.disableString();
		}
	}
	
	private void prepareShader(DrawingEntity entity, TextShaderProgram shaderProgram) {		
		shaderProgram.loadTexture(0);
		shaderProgram.storeTextureID(entity.getTextureID());
		shaderProgram.loadFontWidth(entity.getFontWidth());
		shaderProgram.loadFontEdge(entity.getFontEdge());
	}
	
	private void prepareShader(DrawingEntity entity, BillboardingTextShaderProgram shaderProgram) {
		// TODO
		shaderProgram.loadTexture(0);
		shaderProgram.storeTextureID(entity.getTextureID());
		shaderProgram.loadFontWidth(entity.getFontWidth());
		shaderProgram.loadFontEdge(entity.getFontEdge());
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
	
	private void loadVBO(ArrayList<DrawingEntity> listEntities, String drawingType, int shaderNumber, AbstractShader shader) {
		
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
		storeDataInAttributeList(AbstractShader.POSITION_ATTRIBUTE_IDX,VERTICES_IDX,listVertices,shaderNumber);
		
		// COLORS BUFFER
		storeDataInAttributeList(AbstractShader.COLOR_ATTRIBUTE_IDX,COLOR_IDX,listColors,shaderNumber);
		
		// UV MAPPING (If a texture is defined)
		if (listUvMapping.size() != 0) {
			storeDataInAttributeList(AbstractShader.UVMAPPING_ATTRIBUTE_IDX,UVMAPPING_IDX,listUvMapping,shaderNumber);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, shader.getTextureID());
		}
		
		// NORMAL BUFFER
		if (shader.useNormal())
			storeDataInAttributeList(AbstractShader.NORMAL_ATTRIBUTE_IDX,NORMAL_IDX,listNormals,shaderNumber);
		
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
		bindBuffer(shaderAttributeNumber,bufferAttributeNumber,shaderNumber);
		
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
		
		gl.glEnableVertexAttribArray(shaderAttributeNumber);
		if (!listOfVAOUsed.contains(shaderAttributeNumber))
		{
			listOfVAOUsed.add(shaderAttributeNumber);
		}
	}
	
	private void bindBuffer(int shaderAttributeNumber, int bufferAttributeNumber, int shaderNumber) {
		int coordinateSize = 0;
		switch (shaderAttributeNumber) {
		// recognize the type of VAO to determine the size of the coordinates
			case AbstractShader.COLOR_ATTRIBUTE_IDX : coordinateSize = 4; break; // r, g, b, a
			case AbstractShader.POSITION_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case AbstractShader.NORMAL_ATTRIBUTE_IDX : coordinateSize = 3; break; // x, y, z
			case AbstractShader.UVMAPPING_ATTRIBUTE_IDX : coordinateSize = 2; break; // u, v
		}
		// Select the VBO, GPU memory data, to use for data
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, layerStructureMap.get(currentLayer).vboHandles[shaderNumber*5+bufferAttributeNumber]);
		
		// Associate Vertex attribute with the last bound VBO
		gl.glVertexAttribPointer(shaderAttributeNumber, coordinateSize,
		                    GL2.GL_FLOAT, false /* normalized? */, 0 /* stride */,
		                    0 /* The bound VBO data offset */);
	}

}
