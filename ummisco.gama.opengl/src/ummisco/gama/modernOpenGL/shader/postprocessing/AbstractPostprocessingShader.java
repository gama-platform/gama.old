package ummisco.gama.modernOpenGL.shader.postprocessing;

import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;

import ummisco.gama.modernOpenGL.shader.AbstractShader;

public abstract class AbstractPostprocessingShader extends AbstractShader {
	
	protected final static String containingFolder = "postprocessing";
	
	private int location_texture;
	
	private boolean useNormal = false;
	private boolean useTexture = true;
	
	private int textureIDStored = -1;
	
	protected AbstractPostprocessingShader(GL2 gl, String vertexFile, String fragmentFile) {
		super(gl, vertexFile, fragmentFile);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords");
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
