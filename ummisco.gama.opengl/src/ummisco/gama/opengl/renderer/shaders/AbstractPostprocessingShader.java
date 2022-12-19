/*******************************************************************************************************
 *
 * AbstractPostprocessingShader.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.shaders;

import com.jogamp.opengl.GL2;

/**
 * The Class AbstractPostprocessingShader.
 */
public abstract class AbstractPostprocessingShader extends AbstractShader {

	/** The location texture. */
	private int location_texture;

	/** The texture ID stored. */
	private int textureIDStored = -1;

	/**
	 * Instantiates a new abstract postprocessing shader.
	 *
	 * @param gl the gl
	 * @param vertexFile the vertex file
	 * @param fragmentFile the fragment file
	 */
	protected AbstractPostprocessingShader(final GL2 gl, final String vertexFile, final String fragmentFile) {
		super(gl, vertexFile, fragmentFile);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords");
	}

	/**
	 * Load texture.
	 *
	 * @param textureId the texture id
	 */
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

	/**
	 * Store texture ID.
	 *
	 * @param textureID the texture ID
	 */
	public void storeTextureID(final int textureID) {
		textureIDStored = textureID;
	}

	@Override
	public int getTextureID() {
		return textureIDStored;
	}

}
