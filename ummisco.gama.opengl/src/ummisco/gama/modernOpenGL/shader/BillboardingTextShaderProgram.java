/*********************************************************************************************
 *
 * 'BillboardingTextShaderProgram.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.shader;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;


public class BillboardingTextShaderProgram extends AbstractShader {
	
	private static String VERTEX_FILE = "billboardingTextVertexShader";		
	private static String FRAGMENT_FILE = "billboardingTextFragmentShader";
	
	private int location_texture;
	private int location_fontWidth; // only for string entities
	private int location_fontEdge; // only for string entities
	private int location_modelMatrix; // only for string entities
	
	private Vector3f translation;
	
	private int textureIDStored = -1;
	
	public BillboardingTextShaderProgram(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(COLOR_ATTRIBUTE_IDX, "attribute_Color");
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_texture = getUniformLocation("textureSampler");
		location_fontWidth = getUniformLocation("fontWidth");
		location_fontEdge = getUniformLocation("fontEdge");
		location_modelMatrix = getUniformLocation("modelMatrix");		
	}
	
	public void loadModelMatrix(Matrix4f matrix) {
		super.loadMatrix(location_modelMatrix, matrix);
	}
	
	public void loadTexture(int textureId) {
		super.loadInt(location_texture,textureId);
	}

	public void storeTextureID(int textureID) {
		textureIDStored = textureID;
	}
	
	public int getTextureID() {
		return textureIDStored;
	}

	public void loadFontWidth(float fontWidth) {
		super.loadFloat(location_fontWidth, fontWidth);
	}

	public void loadFontEdge(float fontEdge) {
		super.loadFloat(location_fontEdge, fontEdge);
	}
	
	@Override
	public boolean useNormal() {
		return false;
	}
	
	@Override
	public boolean useTexture() {
		return true;
	}
	
	public void setTranslation(Vector3f value) {
		translation = value;
	}
	
	@Override
	public Vector3f getTranslation() {
		return translation;
	}
}
