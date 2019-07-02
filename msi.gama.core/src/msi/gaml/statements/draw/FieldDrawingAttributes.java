/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.FieldDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;

public class FieldDrawingAttributes extends FileDrawingAttributes {

	public String speciesName;
	public boolean triangulated;
	public boolean grayScaled;
	public boolean withText;
	public GamaPoint cellSize;

	public void setTextures(final List<?> textures) {
		colorProperties.withTextures(textures);

	}

	/**
	 * @param name
	 * @param lineColor
	 */
	public FieldDrawingAttributes(final String name, final GamaColor border, final boolean isImage) {
		super(null, isImage);
		speciesName = name;
	}

	public void setSpeciesName(final String name) {
		speciesName = name;
	}

	@Override
	public String getSpeciesName() {
		return speciesName;
	}

	public void setCellSize(final GamaPoint p) {
		cellSize = p;

	}

	public GamaPoint getCellSize() {
		return cellSize;
	}

}