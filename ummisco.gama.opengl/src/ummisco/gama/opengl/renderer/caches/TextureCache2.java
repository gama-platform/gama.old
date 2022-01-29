/*******************************************************************************************************
 *
 * TextureCache2.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.caches;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.ImageUtils;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;

/**
 * The Class TextureCache2.
 */
public class TextureCache2 implements ITextureCache {

	static {
		DEBUG.OFF();
	}

	/** The volatile textures. */
	private final LoadingCache<BufferedImage, Texture> volatileTextures;
	
	/** The static textures. */
	private final Cache<String, Texture> staticTextures =
			CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS).build();
	
	/** The textures to process. */
	final List<String> texturesToProcess = new CopyOnWriteArrayList<>();
	
	/** The gl. */
	final OpenGL gl;
	
	/** The is non power of 2 textures available. */
	Boolean isNonPowerOf2TexturesAvailable;

	/**
	 * Instantiates a new texture cache 2.
	 *
	 * @param gl the gl
	 */
	public TextureCache2(final OpenGL gl) {
		this.gl = gl;
		volatileTextures = CacheBuilder.newBuilder().build(new CacheLoader<BufferedImage, Texture>() {

			@Override
			public Texture load(final BufferedImage key) throws Exception {
				return buildTexture(gl.getGL(), key);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#initialize()
	 */
	@Override
	public void initialize() {
		if (isNonPowerOf2TexturesAvailable == null) {
			isNonPowerOf2TexturesAvailable =
					!GamaPreferences.Displays.DISPLAY_POWER_OF_TWO.getValue() && gl.getGL().isNPOTTextureAvailable();
			GamaPreferences.Displays.DISPLAY_POWER_OF_TWO.onChange(newValue -> {
				isNonPowerOf2TexturesAvailable = !newValue && gl.getGL().isNPOTTextureAvailable();
				TextureIO.setTexRectEnabled(newValue);
			});
			TextureIO.setTexRectEnabled(GamaPreferences.Displays.DISPLAY_POWER_OF_TWO.getValue());
			// DEBUG.OUT("Non power-of-two textures available: " + isNonPowerOf2TexturesAvailable);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#deleteVolatileTextures()
	 */
	@Override
	public void deleteVolatileTextures() {
		final Collection<Texture> textures = volatileTextures.asMap().values();
		for (final Texture t : textures) { t.destroy(gl.getGL()); }
		volatileTextures.invalidateAll();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#dispose()
	 */
	@Override
	public void dispose() {
		deleteVolatileTextures();
		staticTextures.asMap().forEach((s, t) -> { t.destroy(gl.getGL()); });
		staticTextures.invalidateAll();
		staticTextures.cleanUp();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#processs(java.io.File)
	 */
	@Override
	public void processs(final File file) {
		if (!texturesToProcess.contains(file.getAbsolutePath())) { texturesToProcess.add(file.getAbsolutePath()); }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#processUnloaded()
	 */
	@Override
	public void processUnloaded() {
		for (final String path : texturesToProcess) { getTexture(new File(path), false, true); }
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#getTexture(java.awt.image.BufferedImage)
	 */
	@Override
	public Texture getTexture(final BufferedImage img) {
		return volatileTextures.getUnchecked(img);
	}

	@Override
	public Texture getTexture(final File file, final boolean isAnimated, final boolean useCache) {
		if (file == null) return null;
		Texture texture = null;
		if (isAnimated || !useCache) {
			final BufferedImage image = ImageUtils.getInstance().getImageFromFile(file, useCache, true, null
			/* new ProgressCounter(GAMA.getRuntimeScope(), file.getName()) */);
			texture = volatileTextures.getUnchecked(image);

		} else {
			try {
				texture = staticTextures.get(file.getAbsolutePath(), () -> buildTexture(gl.getGL(), file));
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		}
		return texture;
	}

	/**
	 * Builds the texture.
	 *
	 * @param gl the gl
	 * @param file the file
	 * @return the texture
	 */
	private Texture buildTexture(final GL gl, final File file) {

		return buildTexture(gl, ImageUtils.getInstance().getImageFromFile(file,
				GamaPreferences.Displays.OPENGL_USE_IMAGE_CACHE.getValue(), true, null
		/* new ProgressCounter(GAMA.getRuntimeScope(), file.getName()) */));

		// try {
		//
		// // final TextureData data = TextureIO.newTextureData(gl.getGLProfile(), file, true, null);
		// // final Texture texture = new Texture(gl, data);
		// // texture.setMustFlipVertically(false);
		// // data.flush();
		// // return texture;
		// Texture t = TextureIO.newTexture(file, true);
		// t.setMustFlipVertically(true);
		//
		// return t;
		// } catch (final GLException | IOException e) {
		// e.printStackTrace();
		// return null;
		// }
	}

	/**
	 * Builds the texture.
	 *
	 * @param gl the gl
	 * @param im the im
	 * @return the texture
	 */
	Texture buildTexture(final GL gl, final BufferedImage im) {
		try {
			final TextureData data = AWTTextureIO.newTextureData(gl.getGLProfile(), im, true);
			final Texture texture = new Texture(gl, data);
			data.flush();
			return texture;
		} catch (final GLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
