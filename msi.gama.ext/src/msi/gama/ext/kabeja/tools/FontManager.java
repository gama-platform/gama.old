/*******************************************************************************************************
 *
 * FontManager.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.ext.kabeja.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class FontManager {
	
	/** The instance. */
	private static FontManager instance = new FontManager();
	
	/** The font description. */
	private String fontDescription = "conf/font.properties";
	
	/** The font properties. */
	private final Hashtable<String, String> fontProperties = new Hashtable<>();

	/**
	 * Instantiates a new font manager.
	 */
	private FontManager() {
		loadFontDescription();
	}

	/**
	 * Sets the font description.
	 *
	 * @param file the new font description
	 */
	public void setFontDescription(final String file) {
		this.fontDescription = file;
		loadFontDescription();
	}

	/**
	 * Load font description.
	 */
	private void loadFontDescription() {
		fontProperties.clear();

		try {
			InputStream stream = this.getClass().getResourceAsStream(this.fontDescription);

			if (stream == null) {
				try {
					stream = new FileInputStream(this.fontDescription);
				} catch (FileNotFoundException e1) {}
			}

			if (stream != null) {
				BufferedReader in = new BufferedReader(new InputStreamReader(stream));
				String line = null;

				while ((line = in.readLine()) != null) {
					int index = line.indexOf("=");

					if (index >= 0) {
						String font = line.substring(0, index).trim().toLowerCase();
						String svgFont = line.substring(index + 1).trim();
						fontProperties.put(font, svgFont);
					}
				}
			} else {
				// System.out.println("no font.properties");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the single instance of FontManager.
	 *
	 * @return single instance of FontManager
	 */
	public static FontManager getInstance() { return instance; }

	/**
	 * Query if a SVG font description exists for the given shx font.
	 *
	 * @param font
	 *            The font.shx or font
	 * @return
	 */
	public boolean hasFontDescription(String font) {
		font = getFontKey(font);

		if (fontProperties.containsKey(font)) return true;

		return false;
	}

	/**
	 * Gets the font description.
	 *
	 * @param font the font
	 * @return the font description
	 */
	public String getFontDescription(final String font) {
		return fontProperties.get(getFontKey(font));
	}

	/**
	 * Gets the font key.
	 *
	 * @param font the font
	 * @return the font key
	 */
	private String getFontKey(String font) {
		font = font.toLowerCase();

		if (font.endsWith(".shx")) { font = font.substring(0, font.indexOf(".shx")); }

		return font;
	}
}
