package ummisco.gama.modernOpenGL.shader;

import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;

public class SimpleShaderProgram extends AbstractShader {
	
	private static String VERTEX_FILE = "simpleVertexShader";		
	private static String FRAGMENT_FILE = "simpleFragmentShader";
	
	private int location_texture;
	
	private boolean useNormal = false;
	private boolean useTexture = true;
	
	private int textureIDStored = -1;
	
	public SimpleShaderProgram(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	public SimpleShaderProgram(SimpleShaderProgram shader) {
		super(shader.gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(COLOR_ATTRIBUTE_IDX, "attribute_Color");
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords3D");
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_texture = getUniformLocation("textureSampler");
	}
	
	public void loadTexture(int textureId) {
		super.loadInt(location_texture,textureId);
	}
	
	public boolean useTexture() {
		return useTexture;
	}
	
	public boolean useNormal() {
		return useNormal;
	}

	public void storeTextureID(int textureID) {
		textureIDStored = textureID;
	}
	
	public int getTextureID() {
		return textureIDStored;
	}

	@Override
	public Vector3f getTranslation() {
		return new Vector3f(0,0,0);
	}
}
