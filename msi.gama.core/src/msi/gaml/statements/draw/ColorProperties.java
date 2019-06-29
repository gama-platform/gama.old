/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.ColorProperties.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.List;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.PoolUtils;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGifFile;

public class ColorProperties implements IDisposable {

	static PoolUtils.ObjectPool<ColorProperties> POOL =
			PoolUtils.create("ColorProperties", true, () -> new ColorProperties(), null);

	public static ColorProperties create() {
		// return new ColorProperties();
		return POOL.get();
	}

	private ColorProperties() {}

	public static final GamaColor TEXTURED_COLOR = GamaColor.getInt(Color.white.getRGB());
	public static final GamaColor SELECTED_COLOR = new GamaColor(Color.red);
	GamaColor fill, border, highlight;
	List<?> textures;
	GamaColor[] colors;
	boolean empty = false, lighting = true, useCache = true;

	@Override
	public void dispose() {
		fill = null;
		border = null;
		highlight = null;
		textures = null;
		colors = null;
		POOL.release(this);
	}

	public GamaColor getFillColor() {
		if (highlight != null) { return highlight; }
		if (empty) { return null; }
		if (fill == null) {
			if (colors != null) { return colors[0]; }
			if (textures != null) { return TEXTURED_COLOR; }
			// if (border == null) {
			// Always returns the color as we are solid; so null cannot be an option
			// see issue #2724
			return GamaPreferences.Displays.CORE_COLOR.getValue();
			// }
			// return null;
		}
		return fill;
	}

	public GamaColor getBorderColor() {
		if (empty && border == null) { return fill; }
		return border;
	}

	public GamaColor[] getColors() {
		if (empty) { return null; }
		if (colors == null) {
			if (fill == null) { return null; }
			return new GamaColor[] { fill };
		}
		return colors;
	}

	void toEmpty() {
		empty = true;
	}

	void toFilled() {
		empty = false;
	}

	void withFill(final GamaColor color) {
		fill = color;
	}

	void withColors(final GamaColor[] colors) {
		this.colors = colors;
	}

	void withBorder(final GamaColor border) {
		this.border = border;
	}

	void withLighting(final Boolean lighting) {
		this.lighting = lighting != null && lighting;
	}

	void toNoBorder() {
		border = null;
	}

	void withTextures(final List<?> textures) {
		this.textures = textures;
	}

	List<?> getTextures() {
		return textures;
	}

	boolean isEmpty() {
		return empty;
	}

	public boolean isAnimated() {
		if (!useCache) { return true; }
		if (textures == null) { return false; }
		final Object o = textures.get(0);
		if (!(o instanceof GamaGifFile)) { return false; }
		return true;
	}

	public boolean isLighting() {
		return lighting;
	}

}
