package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.util.GamaColor;

public abstract class ColorProperties {

	public static final GamaColor TEXTURED_COLOR = GamaColor.getInt(Color.white.getRGB());

	public static final ColorProperties NONE = new ColorProperties() {

		@Override
		public GamaColor getFillColor() {
			return null;
		}

		@Override
		public GamaColor getBorderColor() {
			return null;
		}

		@Override
		public GamaColor[] getColors() {
			return null;
		}

		@Override
		ColorProperties toEmpty() {
			return this;
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null)
				return this;
			return new FillOnly(color);
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			if (color == null)
				return this;
			return withFill(color);
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return this;
			return new BorderOnly(border);
		}

		@Override
		ColorProperties toNoBorder() {
			return this;
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			if (colors == null)
				return this;
			if (colors.length == 1)
				return new FillOnly(colors[0]);
			return new MultipleColors(colors);
		}

		@Override
		ColorProperties toFilled() {
			return new FillOnly(GamaPreferences.Displays.CORE_COLOR.getValue());
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			return new TextureOnly(textures);
		}
	};

	public abstract GamaColor getFillColor();

	public abstract GamaColor getBorderColor();

	public abstract GamaColor[] getColors();

	abstract ColorProperties toEmpty();

	abstract ColorProperties toFilled();

	abstract ColorProperties withFill(GamaColor color);

	abstract ColorProperties withColors(GamaColor[] colors);

	abstract ColorProperties withBorder(GamaColor border);

	abstract ColorProperties withHighlight(GamaColor color);

	abstract ColorProperties toNoBorder();

	abstract ColorProperties withTextures(List<?> textures);

	List<?> getTextures() {
		return null;
	}

	boolean isEmpty() {
		return false;
	}

	static class FillOnly extends ColorProperties {
		GamaColor fill;

		@Override
		public GamaColor getFillColor() {
			return fill;
		}

		public FillOnly(final GamaColor fill) {
			super();
			this.fill = fill;
		}

		@Override
		public GamaColor getBorderColor() {
			return null;
		}

		@Override
		public GamaColor[] getColors() {
			return new GamaColor[] { fill };
		}

		@Override
		ColorProperties toEmpty() {
			return new BorderOnly(fill);
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null)
				return toEmpty();
			fill = color;
			return this;
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			if (color == null)
				return this;
			return withFill(color);
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return this;
			return new FillAndBorder(fill, border);
		}

		@Override
		ColorProperties toNoBorder() {
			return this;
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			if (colors == null)
				return this;
			if (colors.length == 1) {
				fill = colors[0];
				return this;
			}
			return new MultipleColors(colors);
		}

		@Override
		ColorProperties toFilled() {
			return this;
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			if (textures == null)
				return this;
			return new TextureAndColor(textures, fill);
		}
	}

	static class FillAndBorder extends ColorProperties {

		GamaColor fill, border;

		public FillAndBorder(final GamaColor fill, final GamaColor border) {
			super();
			this.fill = fill;
			this.border = border;
		}

		@Override
		public GamaColor getFillColor() {
			return fill;
		}

		@Override
		public GamaColor getBorderColor() {
			return border;
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			return withFill(color);
		}

		@Override
		public GamaColor[] getColors() {
			return new GamaColor[] { fill };
		}

		@Override
		ColorProperties toEmpty() {
			return new BorderOnly(border);
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null)
				return toEmpty();
			fill = color;
			return this;
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return toNoBorder();
			this.border = border;
			return this;
		}

		@Override
		ColorProperties toNoBorder() {
			return new FillOnly(fill);
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			if (colors == null)
				return this;
			if (colors.length == 1) {
				fill = colors[0];
				return this;
			}
			return new MultipleColorsAndBorder(colors, border);
		}

		@Override
		ColorProperties toFilled() {
			return this;
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			if (textures == null)
				return this;
			return new TextureAndBorder(textures, border);
		}
	}

	static class BorderOnly extends ColorProperties {

		GamaColor border;

		public BorderOnly(final GamaColor border) {
			this.border = border;
		}

		@Override
		public GamaColor getFillColor() {
			return null;
		}

		@Override
		public GamaColor getBorderColor() {
			return border;
		}

		@Override
		public GamaColor[] getColors() {
			return null;
		}

		@Override
		ColorProperties toEmpty() {
			return this;
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null)
				return this;
			return new FillAndBorder(color, border);
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			if (color == null)
				return this;
			return withFill(color);
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return toNoBorder();
			this.border = border;
			return this;
		}

		@Override
		ColorProperties toNoBorder() {
			return NONE;
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			if (colors == null)
				return this;
			if (colors.length == 1) { return new FillAndBorder(colors[0], border); }
			return new MultipleColorsAndBorder(colors, border);
		}

		@Override
		ColorProperties toFilled() {
			return new FillAndBorder(GamaPreferences.Displays.CORE_COLOR.getValue(), border);
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			if (textures == null)
				return this;
			return new TextureAndBorder(textures, border);
		}
	}

	static class MultipleColors extends ColorProperties {
		GamaColor[] colors;

		public MultipleColors(final GamaColor[] colors) {
			this.colors = colors;
		}

		@Override
		public GamaColor getFillColor() {
			if (colors == null || colors.length == 0)
				return null;
			return colors[0];
		}

		@Override
		public GamaColor getBorderColor() {
			return null;
		}

		@Override
		public GamaColor[] getColors() {
			return colors;
		}

		@Override
		ColorProperties toEmpty() {
			final GamaColor color = getFillColor();
			if (color == null)
				return NONE;
			return new BorderOnly(color);
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null)
				return toEmpty();
			colors[0] = color;
			return this;
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			if (color == null)
				return this;
			Arrays.fill(colors, color);
			return this;
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return toNoBorder();
			return new MultipleColorsAndBorder(colors, border);
		}

		@Override
		ColorProperties toNoBorder() {
			return this;
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			if (colors == null)
				return NONE;
			if (colors.length == 1) { return new FillOnly(colors[0]); }
			this.colors = colors;
			return this;
		}

		@Override
		ColorProperties toFilled() {
			return this;
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			if (textures == null)
				return this;
			return new TextureOnly(textures);
		}
	}

	static class MultipleColorsAndBorder extends ColorProperties {

		GamaColor[] colors;
		GamaColor border;

		private MultipleColorsAndBorder(final GamaColor[] colors, final GamaColor border) {
			this.colors = colors;
			this.border = border;
		}

		@Override
		public GamaColor getFillColor() {
			if (colors == null || colors.length == 0)
				return null;
			return colors[0];
		}

		@Override
		public GamaColor getBorderColor() {
			return border;
		}

		@Override
		public GamaColor[] getColors() {
			return colors;
		}

		@Override
		ColorProperties toEmpty() {
			return new BorderOnly(border);
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null) { return toEmpty(); }
			colors[0] = color;
			return this;
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			if (color == null)
				return this;
			Arrays.fill(colors, color);
			return this;
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return toNoBorder();
			this.border = border;
			return this;
		}

		@Override
		ColorProperties toNoBorder() {
			return new MultipleColors(colors);
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			if (colors == null)
				return new BorderOnly(border);
			if (colors.length == 1) { return new FillAndBorder(colors[0], border); }
			this.colors = colors;
			return this;
		}

		@Override
		ColorProperties toFilled() {
			return this;
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			if (textures == null)
				return this;
			return new TextureAndBorder(textures, border);
		}
	}

	static class TextureOnly extends ColorProperties {

		List<?> textures;

		TextureOnly(final List<?> textures) {
			this.textures = textures;
		}

		@Override
		List<?> getTextures() {
			return textures;
		}

		@Override
		public GamaColor getFillColor() {
			return TEXTURED_COLOR;
		}

		@Override
		public GamaColor getBorderColor() {
			return null;
		}

		@Override
		public GamaColor[] getColors() {
			return null;
		}

		@Override
		ColorProperties toEmpty() {
			return NONE;
		}

		@Override
		ColorProperties toFilled() {
			return this;
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null)
				return this;
			return new TextureAndColor(textures, color);
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			if (color == null)
				return this;
			return new TextureAndColor(textures, color);
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			return this;
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return this;
			return new TextureAndBorder(textures, border);
		}

		@Override
		ColorProperties toNoBorder() {
			return this;
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			if (textures == null)
				return NONE;
			this.textures = textures;
			return this;
		}

	}

	static class TextureAndColor extends TextureOnly {

		GamaColor color;

		TextureAndColor(final List<?> textures, final GamaColor color) {
			super(textures);
			this.color = color;
		}

		@Override
		ColorProperties withHighlight(final GamaColor color) {
			if (color == null)
				return new TextureOnly(textures);
			this.color = color;
			return this;
		}

		@Override
		public GamaColor getFillColor() {
			return color;
		}

		@Override
		public GamaColor[] getColors() {
			return new GamaColor[] { color };
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			if (color == null)
				return new TextureOnly(textures);
			this.color = color;
			return this;
		}

	}

	static class TextureAndBorder extends TextureOnly {

		List<?> textures;
		GamaColor border;

		public TextureAndBorder(final List<?> textures, final GamaColor border) {
			super(textures);
			this.border = border;
		}

		@Override
		public GamaColor getBorderColor() {
			return border;
		}

		@Override
		public GamaColor[] getColors() {
			return null;
		}

		@Override
		ColorProperties toEmpty() {
			return new BorderOnly(border);
		}

		@Override
		ColorProperties toFilled() {
			return this;
		}

		@Override
		ColorProperties withFill(final GamaColor color) {
			return this;
		}

		@Override
		ColorProperties withColors(final GamaColor[] colors) {
			return this;
		}

		@Override
		ColorProperties withBorder(final GamaColor border) {
			if (border == null)
				return toNoBorder();
			this.border = border;
			return this;
		}

		@Override
		ColorProperties toNoBorder() {
			return new TextureOnly(textures);
		}

		@Override
		ColorProperties withTextures(final List<?> textures) {
			if (textures == null)
				return new BorderOnly(border);
			this.textures = textures;
			return this;
		}

	}

}
