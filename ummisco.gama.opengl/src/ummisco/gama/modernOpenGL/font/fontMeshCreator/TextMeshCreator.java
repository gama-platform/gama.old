/*********************************************************************************************
 *
 * 'TextMeshCreator.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.font.fontMeshCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextMeshCreator {

	protected static final double LINE_HEIGHT = 0.03f;
	protected static final int SPACE_ASCII = 32;

	private final MetaFile metaData;

	protected TextMeshCreator(final File metaFile) {
		metaData = new MetaFile(metaFile);
	}

	protected TextMeshData createTextMesh(final GUIText text) {
		final List<Line> lines = createStructure(text);
		final TextMeshData data = createQuadVertices(text, lines);
		return data;
	}

	private List<Line> createStructure(final GUIText text) {
		final char[] chars = text.getTextString().toCharArray();
		final List<Line> lines = new ArrayList<Line>();
		Line currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());
		for (final char c : chars) {
			final int ascii = c;
			if (ascii == SPACE_ASCII) {
				final boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.attemptToAddWord(currentWord);
				}
				currentWord = new Word(text.getFontSize());
				continue;
			}
			final Character character = metaData.getCharacter(ascii);
			currentWord.addCharacter(character);
		}
		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(final List<Line> lines, Line currentLine, final Word currentWord,
			final GUIText text) {
		final boolean added = currentLine.attemptToAddWord(currentWord);
		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(final GUIText text, final List<Line> lines) {
		text.setNumberOfLines(lines.size());
		double curserX = 0f;
		double curserY = 0f;
		final List<Float> vertices = new ArrayList<Float>();
		final List<Float> textureCoords = new ArrayList<Float>();
		for (final Line line : lines) {
			// if (text.isCentered()) {
			// curserX = (line.getMaxLength() - line.getLineLength()) / 2;
			curserY = 3 * LINE_HEIGHT * text.getFontSize() / 4; // weird value,
																// but seems to
																// have an
																// aspect close
																// to the old
																// renderer.
			// }
			for (final Word word : line.getWords()) {
				for (final Character letter : word.getCharacters()) {
					addVerticesForCharacter(curserX, curserY, letter, text.getFontSize(), vertices);
					addTexCoords(textureCoords, letter.getxTextureCoord(), letter.getyTextureCoord(),
							letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
					curserX += letter.getxAdvance() * text.getFontSize();
				}
				curserX += metaData.getSpaceWidth() * text.getFontSize();
			}
			curserX = 0;
			curserY += LINE_HEIGHT * text.getFontSize();
		}
		return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
	}

	private void addVerticesForCharacter(final double curserX, final double curserY, final Character character,
			final double fontSize, final List<Float> vertices) {
		final double x = curserX + character.getxOffset() * fontSize;
		final double y = curserY + character.getyOffset() * fontSize;
		final double maxX = x + character.getSizeX() * fontSize;
		final double maxY = y + character.getSizeY() * fontSize;
		final double properX = 2 * x;
		final double properY = -(2 * y);
		final double properMaxX = 2 * maxX;
		final double properMaxY = -(2 * maxY);
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}

	private static void addVertices(final List<Float> vertices, final double x, final double y, final double maxX,
			final double maxY) {
		vertices.add((float) x);
		vertices.add((float) y);
		vertices.add((float) 0);
		vertices.add((float) maxX);
		vertices.add((float) y);
		vertices.add((float) 0);
		vertices.add((float) maxX);
		vertices.add((float) maxY);
		vertices.add((float) 0);
		vertices.add((float) x);
		vertices.add((float) maxY);
		vertices.add((float) 0);
	}

	private static void addTexCoords(final List<Float> texCoords, final double x, final double y, final double maxX,
			final double maxY) {
		texCoords.add((float) x);
		texCoords.add((float) y);
		texCoords.add((float) maxX);
		texCoords.add((float) y);
		texCoords.add((float) maxX);
		texCoords.add((float) maxY);
		texCoords.add((float) x);
		texCoords.add((float) maxY);
	}

	private static float[] listToArray(final List<Float> listOfFloats) {
		final float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = listOfFloats.get(i);
		}
		return array;
	}

}
