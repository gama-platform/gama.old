package ummisco.gama.modernOpenGL.shader;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;

import ummisco.gama.opengl.camera.ICamera;
import ummisco.gama.opengl.vaoGenerator.TransformationMatrix;

public class TextShaderProgram extends AbstractShader {
	
	private static String VERTEX_FILE = "textVertexShader";		
	private static String FRAGMENT_FILE = "textFragmentShader";
	
	private int location_texture;
	private int location_fontWidth; // only for string entities
	private int location_fontEdge; // only for string entities
	
	private int textureIDStored = -1;
	
	public TextShaderProgram(GL2 gl) {
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
	public Vector3f getTranslation() {
		return new Vector3f(0,0,0);
	}

	@Override
	public boolean useNormal() {
		return false;
	}

	@Override
	public boolean useTexture() {
		return true;
	}
}
