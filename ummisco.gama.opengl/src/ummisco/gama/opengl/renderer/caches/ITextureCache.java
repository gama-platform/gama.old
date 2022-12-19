/*******************************************************************************************************
 *
 * ITextureCache.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.caches;

import java.awt.image.BufferedImage;
import java.io.File;

import com.jogamp.opengl.util.texture.Texture;

/**
 * The Interface ITextureCache.
 */
public interface ITextureCache {

	/**
	 * Initialize.
	 */
	void initialize();

	/**
	 * Delete volatile textures.
	 */
	void deleteVolatileTextures();

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Processs.
	 *
	 * @param file the file
	 */
	void processs(File file);

	/**
	 * Process unloaded.
	 */
	void processUnloaded();

	/**
	 * Gets the texture.
	 *
	 * @param img the img
	 * @return the texture
	 */
	Texture getTexture(BufferedImage img);

	/**
	 * Gets the texture.
	 *
	 * @param file the file
	 * @param isAnimated the is animated
	 * @param useCache the use cache
	 * @return the texture
	 */
	Texture getTexture(File file, boolean isAnimated, boolean useCache);

}