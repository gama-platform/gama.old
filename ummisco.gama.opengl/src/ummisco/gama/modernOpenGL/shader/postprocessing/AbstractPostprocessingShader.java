/*********************************************************************************************
 *
 * 'AbstractPostprocessingShader.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.shader.postprocessing;

import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;

import ummisco.gama.modernOpenGL.shader.AbstractShader;

public abstract class AbstractPostprocessingShader extends AbstractShader {

	protected final static String containingFolder = "postprocessing";

	private int location_texture;

	private int textureIDStored = -1;

	protected AbstractPostprocessingShader(final GL2 gl, final String vertexFile, final String fragmentFile) {
		super(gl, vertexFile, fragmentFile);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords");
	}

	public void loadTexture(final int textureId) {
		super.loadInt(location_texture, textureId);
	}

	@Override
	public boolean useTexture() {
		return true;
	}

	@Override
	public boolean useNormal() {
		return false;
	}

	public void storeTextureID(final int textureID) {
		textureIDStored = textureID;
	}

	@Override
	public int getTextureID() {
		return textureIDStored;
	}

	@Override
	public Vector3f getTranslation() {
		return new Vector3f(0, 0, 0);
	}

}
