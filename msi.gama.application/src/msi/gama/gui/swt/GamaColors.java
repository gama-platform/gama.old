/*********************************************************************************************
 *
 *
 * 'GamaIcons.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import gnu.trove.map.hash.THashMap;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaColors {

	public static class GamaUIColor {

		Color active, inactive, darker, gray, lighter;

		public GamaUIColor(final Color c) {
			active = c;
		}

		// Returns a normal or lighter version of the color depending on the user's choice (see GamaIcons.CORE_ICONS_BRIGHTNESS). Used only for the basic palette
		public GamaUIColor validate() {
			if ( GamaIcons.CORE_ICONS_BRIGHTNESS.getValue() ) {
				return this;
			} else {
				return GamaColors.get(lighter().getRGB());
			}
		}

		@Override
		public String toString() {
			return active.getRed() + ", " + active.getGreen() + ", " + active.getBlue();
		}

		public int luminance() {
			return GamaColors.luminanceOf(active);
		}

		public boolean isDark() {
			return GamaColors.isDark(active);
		}

		public GamaUIColor(final Color c, final Color i) {
			active = c;
			inactive = i;
		}

		public Color color() {
			return active;
		}

		public Color inactive() {
			if ( inactive == null ) {
				inactive = computeInactive(active);
			}
			return inactive;
		}

		public Color darker() {
			if ( darker == null ) {
				darker = computeDarker(active);
			}
			return darker;
		}

		public Color lighter() {
			if ( lighter == null ) {
				lighter = computeLighter(active);
			}
			return lighter;
		}

		public Color gray() {
			if ( gray == null ) {
				gray = computeGray(active);
			}
			return gray;
		}

		public void dispose() {
			if ( active != null ) {
				colors.remove(getRGB());
				active.dispose();
				active = null;
			}
			if ( inactive != null ) {
				inactive.dispose();
				inactive = null;
			}

			if ( gray != null ) {
				gray.dispose();
				gray = null;
			}
			if ( darker != null ) {
				darker.dispose();
				darker = null;
			}
			if ( lighter != null ) {
				lighter.dispose();
				lighter = null;
			}

		}

		public RGB getRGB() {
			return active.getRGB();
		}
	}

	static THashMap<RGB, GamaUIColor> colors = new THashMap();

	private static Color computeInactive(final Color c) {
		RGB data = c.getRGB();
		float[] hsb = data.getHSB();
		float[] newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = hsb[1] / 2;
		newHsb[2] = Math.min(1.0f, hsb[2] + 0.2f);
		RGB newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	private static Color computeDarker(final Color c) {
		RGB data = c.getRGB();
		float[] hsb = data.getHSB();
		float[] newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = hsb[1];
		newHsb[2] = Math.max(0.0f, hsb[2] - 0.1f);
		RGB newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	private static Color computeLighter(final Color c) {
		RGB data = c.getRGB();
		float[] hsb = data.getHSB();
		float[] newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = hsb[1];
		newHsb[2] = Math.min(1f, hsb[2] + 0.2f);
		RGB newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	private static Color computeGray(final Color c) {
		RGB data = c.getRGB();
		float[] hsb = data.getHSB();
		float[] newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = 0.0f;
		newHsb[2] = hsb[2];
		RGB newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	private static Color getColor(final int r, final int g, final int b) {
		return new Color(Display.getCurrent(), r, g, b);
	}

	public static GamaUIColor get(final java.awt.Color color) {
		return get(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static GamaUIColor get(final RGB rgb) {
		if ( rgb == null ) { return null; }
		GamaUIColor c = colors.get(rgb);
		if ( c == null ) {
			Color cc = getColor(rgb.red, rgb.green, rgb.blue);
			c = new GamaUIColor(cc);
			colors.put(rgb, c);
		}
		return c;
	}

	public static GamaUIColor get(final int r, final int g, final int b) {
		RGB rgb = new RGB(r, g, b);
		return get(rgb);
	}

	public static Color system(final int c) {
		return Display.getCurrent().getSystemColor(c);
	}

	public static GamaUIColor get(final int ... c) {
		if ( c.length >= 3 ) {
			return get(c[0], c[1], c[2]);
		} else {
			int rgb = c[0];
			int red = rgb >> 16 & 0xFF;
			int green = rgb >> 8 & 0xFF;
			int blue = rgb & 0xFF;
			return get(red, green, blue);
		}
	}

	/**
	 * Get the color of the icon passed in parameter (supposing it's mono-colored)
	 * @param create
	 * @return
	 */
	public static GamaUIColor get(final GamaIcon icon) {
		Image image = icon.image();
		ImageData data = image.getImageData();
		PaletteData palette = data.palette;
		int pixelValue = data.getPixel(0, 0);
		return get(palette.getRGB(pixelValue));
	}

	/**
	 * @param background
	 * @return
	 */
	public static boolean isDark(final Color color) {
		return luminanceOf(color) < 130;
	};

	public static int luminanceOf(final Color color) {
		// return (int) (0.2126 * color.getRed() * color.getRed() / 255+ 0.7152 * color.getGreen() * color.getGreen() / 255+ 0.0722 * color.getBlue()* color.getRed() / 255); //first formula
		return (int) (0.299 * color.getRed() * color.getRed() / 255 +
			0.587 * color.getGreen() * color.getGreen() / 255 + 0.114 * color.getBlue() * color.getBlue() / 255); // http://alienryderflex.com/hsp.html
		// return (int) (0.241 * color.getRed() * color.getRed() / 255 + 0.691 * color.getGreen() * color.getGreen() / 255 + 0.068 * color.getBlue()* color.getBlue() / 255); //
		// http://www.nbdtech.com/Blog/archive/2008/04/27/Calculating-the-Perceived-Brightness-of-a-Color.aspx
	}

	public static java.awt.Color toAwtColor(final Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * @param background
	 * @return
	 */
	public static GamaUIColor getTextColorForBackground(final Color background) {
		return isDark(background) ? IGamaColors.WHITE : IGamaColors.BLACK;
	}

	public static GamaUIColor getTextColorForBackground(final GamaUIColor background) {
		return getTextColorForBackground(background.color());
	}

}
