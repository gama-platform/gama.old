/*********************************************************************************************
 *
 * 'TextRenderersCache.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.renderer.caches;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextRenderer.RenderDelegate;

/**
 * Global text renderers. Does not allow renderers to be created for text bigger than 200 pixels
 * 
 * @author drogoul
 *
 */
public class TextRenderersCache {

	static final RenderDelegate DELEGATE = new RenderDelegate() {

		@Override
		public boolean intensityOnly() {
			return false;
		}

		@Override
		public Rectangle2D getBounds(final String str, final Font font, final FontRenderContext frc) {
			return font.getStringBounds(str, frc);
		}

		@Override
		public Rectangle2D getBounds(final CharSequence str, final Font font, final FontRenderContext frc) {
			return font.getStringBounds(str.toString(), frc);
		}

		@Override
		public Rectangle2D getBounds(final GlyphVector gv, final FontRenderContext frc) {
			return gv.getVisualBounds();
		}

		@Override
		public void draw(final Graphics2D graphics, final String str, final int x, final int y) {
			graphics.drawString(str, x, y);

		}

		@Override
		public void drawGlyphVector(final Graphics2D graphics, final GlyphVector str, final int x, final int y) {
			graphics.drawGlyphVector(str, x, y);
		}

	};

	final Set<Font> fontsToProcess = new HashSet<>();

	LoadingCache<Font, TextRenderer> cache =
			newBuilder().expireAfterAccess(10, SECONDS).build(new CacheLoader<Font, TextRenderer>() {

				@Override
				public TextRenderer load(final Font f) throws Exception {
					final TextRenderer r = new TextRenderer(f, true, true, DELEGATE, true);
					r.setSmoothing(true);
					r.setUseVertexArrays(false);
					return r;
				}
			});

	public TextRenderer get(final Font font, final float withSize) {
		final Font f = new Font(font.getFontName(), font.getStyle(), (int) withSize);
		return cache.getUnchecked(f);
	}

	public void saveFontToProcess(final Font font, final float withSize) {
		final Font f = new Font(font.getFontName(), font.getStyle(), (int) withSize);
		fontsToProcess.add(f);
	}

	public void processUnloadedFonts() {
		for (final Font f : fontsToProcess) {
			cache.getUnchecked(f);
		}
		fontsToProcess.clear();
	}

}
