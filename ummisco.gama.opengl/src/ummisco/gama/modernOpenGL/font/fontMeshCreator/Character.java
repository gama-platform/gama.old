/*********************************************************************************************
 *
 * 'Character.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.font.fontMeshCreator;

/**
 * Simple data structure class holding information about a certain glyph in the
 * font texture atlas. All sizes are for a font-size of 1.
 *
 */
public class Character {

	private int id;
	private double xTextureCoord;
	private double yTextureCoord;
	private double xMaxTextureCoord;
	private double yMaxTextureCoord;
	private double xOffset;
	private double yOffset;
	private double sizeX;
	private double sizeY;
	private double xAdvance;

	/**
	 * @param id
	 *            - the ASCII value of the character.
	 * @param xTextureCoord
	 *            - the x texture coordinate for the top left corner of the
	 *            character in the texture atlas.
	 * @param yTextureCoord
	 *            - the y texture coordinate for the top left corner of the
	 *            character in the texture atlas.
	 * @param xTexSize
	 *            - the width of the character in the texture atlas.
	 * @param yTexSize
	 *            - the height of the character in the texture atlas.
	 * @param xOffset
	 *            - the x distance from the curser to the left edge of the
	 *            character's quad.
	 * @param yOffset
	 *            - the y distance from the curser to the top edge of the
	 *            character's quad.
	 * @param sizeX
	 *            - the width of the character's quad in screen space.
	 * @param sizeY
	 *            - the height of the character's quad in screen space.
	 * @param xAdvance
	 *            - how far in pixels the cursor should advance after adding
	 *            this character.
	 */
	protected Character(int id, double xTextureCoord, double yTextureCoord, double xTexSize, double yTexSize,
			double xOffset, double yOffset, double sizeX, double sizeY, double xAdvance) {
		this.id = id;
		this.xTextureCoord = xTextureCoord;
		this.yTextureCoord = yTextureCoord;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.xMaxTextureCoord = xTexSize + xTextureCoord;
		this.yMaxTextureCoord = yTexSize + yTextureCoord;
		this.xAdvance = xAdvance;
	}

	protected int getId() {
		return id;
	}

	protected double getxTextureCoord() {
		return xTextureCoord;
	}

	protected double getyTextureCoord() {
		return yTextureCoord;
	}

	protected double getXMaxTextureCoord() {
		return xMaxTextureCoord;
	}

	protected double getYMaxTextureCoord() {
		return yMaxTextureCoord;
	}

	protected double getxOffset() {
		return xOffset;
	}

	protected double getyOffset() {
		return yOffset;
	}

	protected double getSizeX() {
		return sizeX;
	}

	protected double getSizeY() {
		return sizeY;
	}

	protected double getxAdvance() {
		return xAdvance;
	}

}
