/*********************************************************************************************
 *
 * 'MetaFile.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.font.fontMeshCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides functionality for getting the values from a font file.
 *
 */
public class MetaFile {

	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT = 3;

	private static final int DESIRED_PADDING = 8; // this value is equal to the
													// padding set for the
													// texture through "hiero"

	private static final String SPLITTER = " ";
	private static final String NUMBER_SEPARATOR = ",";

	private final double aspectRatio;

	private double verticalPerPixelSize;
	private double horizontalPerPixelSize;
	private double spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;

	private final Map<Integer, Character> metaData = new HashMap<Integer, Character>();

	private BufferedReader reader;
	private final Map<String, String> values = new HashMap<String, String>();

	/**
	 * Opens a font file in preparation for reading.
	 * 
	 * @param file
	 *            - the font file.
	 */
	protected MetaFile(final File file) {
		this.aspectRatio = 1;// (double) Display.getWidth() / (double)
								// Display.getHeight();

		openFile(file);
		loadPaddingData();
		loadLineSizes();
		final int imageWidth = getValueOfVariable("scaleW");
		loadCharacterData(imageWidth);
		close();
	}

	protected double getSpaceWidth() {
		return spaceWidth;
	}

	protected Character getCharacter(final int ascii) {
		return metaData.get(ascii);
	}

	/**
	 * Read in the next line and store the variable values.
	 * 
	 * @return {@code true} if the end of the file hasn't been reached.
	 */
	private boolean processNextLine() {
		values.clear();
		String line = null;
		try {
			line = reader.readLine();
		} catch (final IOException e1) {
		}
		if (line == null) {
			return false;
		}
		// treat the line for the case of "" : replace the char " " to the char
		// "__" when between double quotes.
		final String[] lineTmp = line.split("\"");
		line = lineTmp[0];
		for (int i = 1; i < lineTmp.length; i++) {
			if (i % 2 != 0) {
				// this string bloc is between double quote : we replace the
				// space char " " by "__".
				line += "\"" + lineTmp[i].replace(" ", "__");
			} else {
				line += "\"" + lineTmp[i];
			}
		}
		for (final String part : line.split(SPLITTER)) {
			final String[] valuePairs = part.split("=");
			if (valuePairs.length == 2) {
				values.put(valuePairs[0], valuePairs[1].replace("__", " "));
			}
		}
		return true;
	}

	/**
	 * Gets the {@code int} value of the variable with a certain name on the
	 * current line.
	 * 
	 * @param variable
	 *            - the name of the variable.
	 * @return The value of the variable.
	 */
	private int getValueOfVariable(final String variable) {
		return Integer.parseInt(values.get(variable));
	}

	/**
	 * Gets the array of ints associated with a variable on the current line.
	 * 
	 * @param variable
	 *            - the name of the variable.
	 * @return The int array of values associated with the variable.
	 */
	private int[] getValuesOfVariable(final String variable) {
		final String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		final int[] actualValues = new int[numbers.length];
		for (int i = 0; i < actualValues.length; i++) {
			actualValues[i] = Integer.parseInt(numbers[i]);
		}
		return actualValues;
	}

	/**
	 * Closes the font file after finishing reading.
	 */
	private void close() {
		try {
			reader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens the font file, ready for reading.
	 * 
	 * @param file
	 *            - the font file.
	 */
	private void openFile(final File file) {
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (final Exception e) {
			e.printStackTrace();
			System.err.println("Couldn't read font meta file!");
		}
	}

	/**
	 * Loads the data about how much padding is used around each character in
	 * the texture atlas.
	 */
	private void loadPaddingData() {
		processNextLine();
		this.padding = getValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	/**
	 * Loads information about the line height for this font in pixels, and uses
	 * this as a way to find the conversion rate between pixels in the texture
	 * atlas and screen-space.
	 */
	private void loadLineSizes() {
		processNextLine();
		final int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextMeshCreator.LINE_HEIGHT / lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
	}

	/**
	 * Loads in data about each character and stores the data in the
	 * {@link Character} class.
	 * 
	 * @param imageWidth
	 *            - the width of the texture atlas in pixels.
	 */
	private void loadCharacterData(final int imageWidth) {
		processNextLine();
		processNextLine();
		while (processNextLine()) {
			final Character c = loadCharacter(imageWidth);
			if (c != null) {
				metaData.put(c.getId(), c);
			}
		}
	}

	/**
	 * Loads all the data about one character in the texture atlas and converts
	 * it all from 'pixels' to 'screen-space' before storing. The effects of
	 * padding are also removed from the data.
	 * 
	 * @param imageSize
	 *            - the size of the texture atlas in pixels.
	 * @return The data about the character.
	 */
	private Character loadCharacter(final int imageSize) {
		final int id = getValueOfVariable("id");
		if (id == TextMeshCreator.SPACE_ASCII) {
			this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
			return null;
		}
		final double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
		final double yTex = 1.0 - ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
		final int width = getValueOfVariable("width") - (paddingWidth - 2 * DESIRED_PADDING);
		final int height = -(getValueOfVariable("height") - (paddingHeight - 2 * DESIRED_PADDING));
		final double quadWidth = width * horizontalPerPixelSize;
		final double quadHeight = height * verticalPerPixelSize;
		final double xTexSize = (double) width / imageSize;
		final double yTexSize = (double) height / imageSize;
		final double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING)
				* horizontalPerPixelSize;
		final double yOff = -(getValueOfVariable("yoffset") + padding[PAD_TOP] - DESIRED_PADDING)
				* verticalPerPixelSize;
		final double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
	}
}
