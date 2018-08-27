/*******************************************************************************************************
 *
 * ummisco.gama.opengl.renderer.caches.TextureCache.java, in plugin ummisco.gama.opengl, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.caches;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.ImageUtils;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;

public class TextureCache implements ITextureCache {

	private final LoadingCache<BufferedImage, TextureRenderer> volatileTextures;
	private final Cache<String, TextureRenderer> staticTextures =
			CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS).build();
	final List<String> texturesToProcess = new CopyOnWriteArrayList<>();
	final OpenGL gl;
	Boolean isNonPowerOf2TexturesAvailable;

	public TextureCache(final OpenGL gl) {
		this.gl = gl;
		volatileTextures = CacheBuilder.newBuilder().build(new CacheLoader<BufferedImage, TextureRenderer>() {

			@Override
			public TextureRenderer load(final BufferedImage key) throws Exception {
				return buildTextureRenderer(gl.getGL(), key);
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
				AWTTextureIO.setTexRectEnabled(newValue);
			});
			AWTTextureIO.setTexRectEnabled(GamaPreferences.Displays.DISPLAY_POWER_OF_TWO.getValue());
			DEBUG.OUT("Non power-of-two textures available: " + isNonPowerOf2TexturesAvailable);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#deleteVolatileTextures()
	 */
	@Override
	public void deleteVolatileTextures() {
		final Collection<TextureRenderer> textures = volatileTextures.asMap().values();
		for (final TextureRenderer t : textures) {
			t.dispose();
		}
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
		staticTextures.asMap().forEach((s, t) -> {
			t.dispose();
		});
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
		if (!texturesToProcess.contains(file.getAbsolutePath())) {
			texturesToProcess.add(file.getAbsolutePath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#processUnloaded()
	 */
	@Override
	public void processUnloaded() {
		for (final String path : texturesToProcess) {
			getTextureRenderer(new File(path), false, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#getTexture(java.awt.image.BufferedImage)
	 */
	@Override
	public Texture getTexture(final BufferedImage img) {
		final TextureRenderer renderer = volatileTextures.getUnchecked(img);
		if (renderer == null) { return null; }
		return renderer.getTexture();
	}

	private TextureRenderer getTextureRenderer(final File file, final boolean isAnimated, final boolean useCache) {
		if (file == null) { return null; }
		TextureRenderer texture = null;
		if (isAnimated || !useCache) {
			final BufferedImage image = ImageUtils.getInstance().getImageFromFile(file, useCache, true);
			texture = volatileTextures.getUnchecked(image);

		} else {
			try {
				texture = staticTextures.get(file.getAbsolutePath(), () -> buildTextureRenderer(gl.getGL(), file));
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		}
		return texture;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ummisco.gama.opengl.renderer.caches.ITextureCache#getTexture(java.io.File, boolean, boolean)
	 */
	@Override
	public Texture getTexture(final File file, final boolean isAnimated, final boolean useCache) {
		if (file == null) { return null; }
		final TextureRenderer renderer = getTextureRenderer(file, isAnimated, useCache);
		if (renderer == null) { return null; }
		final Texture texture = renderer.getTexture();
		return texture;
	}

	private TextureRenderer buildTextureRenderer(final GL gl, final File file) {
		try {
			final BufferedImage im = ImageUtils.getInstance().getImageFromFile(file, true, true);
			return buildTextureRenderer(gl, im);
		} catch (final GLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private TextureRenderer buildTextureRenderer(final GL gl, final BufferedImage im) {
		try {
			final int width, height;
			if (isNonPowerOf2TexturesAvailable) {
				width = im.getWidth();
				height = im.getHeight();
			} else {
				width = getClosestPow(im.getWidth());
				height = getClosestPow(im.getHeight());
			}
			final TextureRenderer tr = new TextureRenderer(width, height, ImageUtils.checkTransparency(im), true);
			tr.setSmoothing(true);
			final Graphics2D g = tr.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(im, 0, 0, width, height, null);
			return tr;
		} catch (final GLException e) {
			e.printStackTrace();
			return null;
		}
	}

	private int getClosestPow(final int value) {
		int power = 1;
		while (power < value) {
			power *= 2;
		}
		return power;
	}
}
