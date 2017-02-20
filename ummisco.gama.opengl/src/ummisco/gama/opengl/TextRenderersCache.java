/*********************************************************************************************
 *
 * 'TextRenderersCache.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.awt.Font;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Global text renderers. Does not allow renderers to be created for text bigger than 200 pixels
 * 
 * @author drogoul
 *
 */
public class TextRenderersCache {

	Map<String, Map<Integer, Map<Integer, TextRenderer>>> cache = new LinkedHashMap<>();

	public TextRenderer get(final Font font) {
		return get(font.getName(), font.getSize(), font.getStyle());
	}

	public TextRenderer get(final String font, final int s, final int style) {
		final int size = s > 150 ? 150 : s;
		if (size < 6) { return null; }
		Map<Integer, Map<Integer, TextRenderer>> map1 = cache.get(font);
		if (map1 == null) {
			map1 = new HashMap<>();
			cache.put(font, map1);
		}
		Map<Integer, TextRenderer> map2 = map1.get(size);
		if (map2 == null) {
			map2 = new HashMap<>();
			map1.put(size, map2);
		}
		TextRenderer r = map2.get(style);
		if (r == null) {
			r = new TextRenderer(new Font(font, style, size), true, false, null, true);
			r.setSmoothing(false);
			r.setUseVertexArrays(false);
			map2.put(style, r);
		}
		return r;
	}

	/**
	 * @param gl
	 */
	public void dispose() {
		for (final String key1 : cache.keySet()) {
			final Map<Integer, Map<Integer, TextRenderer>> map1 = cache.get(key1);
			for (final Integer key2 : map1.keySet()) {
				final Map<Integer, TextRenderer> map2 = map1.get(key2);
				for (final TextRenderer tr : map2.values()) {
					tr.dispose();
				}
			}
		}
		cache.clear();
	}

}
