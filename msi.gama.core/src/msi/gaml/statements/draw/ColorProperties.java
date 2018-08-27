/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.ColorProperties.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.List;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGifFile;

public class ColorProperties {

	public static final GamaColor TEXTURED_COLOR = GamaColor.getInt(Color.white.getRGB());

	GamaColor fill;
	GamaColor border;
	List<?> textures;
	GamaColor[] colors;
	boolean empty;
	boolean lighting = true;

	public GamaColor getFillColor() {
		if (empty) { return null; }
		if (fill == null) {
			if (colors != null) { return colors[0]; }
			if (textures != null) { return TEXTURED_COLOR; }
			if (border == null) { return GamaPreferences.Displays.CORE_COLOR.getValue(); }
			return null;
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

	ColorProperties toEmpty() {
		empty = true;
		return this;
	}

	ColorProperties toFilled() {
		empty = false;
		return this;
	}

	ColorProperties withFill(final GamaColor color) {
		fill = color;
		return this;
	}

	ColorProperties withColors(final GamaColor[] colors) {
		this.colors = colors;
		return this;
	}

	ColorProperties withBorder(final GamaColor border) {
		this.border = border;
		return this;
	}

	ColorProperties withLighting(final boolean lighting) {
		this.lighting = lighting;
		return this;
	}

	ColorProperties toNoBorder() {
		border = null;
		return this;
	}

	ColorProperties withTextures(final List<?> textures) {
		this.textures = textures;
		return this;
	}

	List<?> getTextures() {
		return textures;
	}

	boolean isEmpty() {
		return empty;
	}

	public boolean isAnimated() {
		if (textures == null) { return false; }
		final Object o = textures.get(0);
		if (!(o instanceof GamaGifFile)) { return false; }
		return true;
	}

	public int getFrameCount() {
		if (textures == null) { return 1; }
		final Object o = textures.get(0);
		if (!(o instanceof GamaGifFile)) { return 1; }
		return ((GamaGifFile) o).getFrameCount();

	}

	public int getAverageDelay() {
		if (textures == null) { return 0; }
		final Object o = textures.get(0);
		if (!(o instanceof GamaGifFile)) { return 0; }
		return ((GamaGifFile) o).getAverageDelay();

	}

	public boolean isLighting() {
		return lighting;
	}

}
