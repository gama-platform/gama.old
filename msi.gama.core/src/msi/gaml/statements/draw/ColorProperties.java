package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.List;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.GamaColor;

public class ColorProperties {

	public static final GamaColor TEXTURED_COLOR = GamaColor.getInt(Color.white.getRGB());

	GamaColor fill;
	GamaColor border;
	List<?> textures;
	GamaColor[] colors;
	boolean empty;

	public GamaColor getFillColor() {
		if (empty)
			return null;
		if (fill == null) {
			if (colors != null) { return colors[0]; }
			if (textures != null) { return TEXTURED_COLOR; }
			if (border == null) { return GamaPreferences.Displays.CORE_COLOR.getValue(); }
			return null;
		}
		return fill;
	}

	public GamaColor getBorderColor() {
		return border;
	}

	public GamaColor[] getColors() {
		if (empty)
			return null;
		if (colors == null) {
			if (fill == null)
				return null;
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

}
