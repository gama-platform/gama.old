/*******************************************************************************************************
 *
 * ummisco.gama.opengl.renderer.caches.ITextureCache.java, in plugin ummisco.gama.opengl, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.caches;

import java.awt.image.BufferedImage;
import java.io.File;

import com.jogamp.opengl.util.texture.Texture;

public interface ITextureCache {

	void initialize();

	void deleteVolatileTextures();

	void dispose();

	void processs(File file);

	void processUnloaded();

	Texture getTexture(BufferedImage img);

	Texture getTexture(File file, boolean isAnimated, boolean useCache);

}