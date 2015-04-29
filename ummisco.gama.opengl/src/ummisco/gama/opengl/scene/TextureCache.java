/*********************************************************************************************
 * 
 * 
 * 'MyTexture.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import ummisco.gama.opengl.JOGLRenderer;
import msi.gama.common.util.ImageUtils;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class TextureCache {

	private final static Map<BufferedImage, Texture> staticTextures = new IdentityHashMap();
	private final static Map<JOGLRenderer, Map<BufferedImage, Texture>> dynamicTextures = new IdentityHashMap();

	static Texture get(final JOGLRenderer renderer, final BufferedImage image, final boolean isDynamic) {
		if ( isDynamic ) {
			Map<BufferedImage, Texture> map = dynamicTextures.get(renderer);
			if ( map == null ) {
				return null;
			} else {
				return map.get(image);
			}
		} else {
			return staticTextures.get(image);
		}

	}

	static void put(final JOGLRenderer renderer, final BufferedImage image, final Texture texture,
		final boolean isDynamic) {
		if ( texture == null || image == null ) { return; }

		if ( isDynamic ) {
			Map<BufferedImage, Texture> map = dynamicTextures.get(renderer);
			if ( map == null ) {
				map = new IdentityHashMap();
				map.put(image, texture);
				dynamicTextures.put(renderer, map);
			}
		} else {
			staticTextures.put(image, texture);
		}
	}

	public static void clearDynamicTextures(final GL gl, final JOGLRenderer renderer) {
		Map<BufferedImage, Texture> map = dynamicTextures.get(renderer);
		if ( map != null ) {
			for ( Texture t : map.values() ) {
				t.destroy(gl);
			}
			map.clear();
		}

	}

	public static void clearCache(final JOGLRenderer renderer) {
		// clearDynamicTextures(gl, renderer);
		dynamicTextures.remove(renderer);
	}

	public static Texture create(final GL gl, final JOGLRenderer renderer, final BufferedImage image,
		final boolean isDynamic) {
		if ( image == null ) { return null; }
		Texture texture = get(renderer, image, isDynamic);

		if ( texture == null ) {
			BufferedImage corrected = image;
			if ( !IsPowerOfTwo(image.getWidth()) || !IsPowerOfTwo(image.getHeight()) ) {
				int width = getClosestPow(image.getWidth());
				int height = getClosestPow(image.getHeight());
				corrected = ImageUtils.createCompatibleImage(width, height);
				Graphics2D g2 = corrected.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(image, 0, 0, width, height, null);
				g2.dispose();
			}
			try {
				TextureData data =
					AWTTextureIO.newTextureData(gl.getGLProfile(), corrected, false /* true for mipmapping */);
				texture = new Texture(gl, data);
				// We use the original image to keep track of the texture
				TextureCache.put(renderer, image, texture, isDynamic);

			} catch (final GLException e) {
				e.printStackTrace();
				return null;
			}
		}
		// Apply antialas to the texture based on the current preferences
		texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, renderer.data.isAntialias() ? GL.GL_LINEAR
			: GL.GL_NEAREST);
		texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, renderer.data.isAntialias() ? GL.GL_LINEAR
			: GL.GL_NEAREST);
		return texture;
	}

	static boolean IsPowerOfTwo(final int x) {
		return (x & x - 1) == 0;
	}

	static int getClosestPow(final int value) {
		int power = 1;
		while (power < value) {
			power *= 2;
		}
		return power;
	}
}
