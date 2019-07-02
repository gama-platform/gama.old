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

import msi.gama.metamodel.shape.GamaPoint;

public class FieldDrawingAttributes extends DrawingAttributes {

	public final String speciesName;
	public boolean triangulated;
	public boolean grayScaled;
	public boolean withText;

	public FieldDrawingAttributes(final String name) {
		speciesName = name;
	}

	@Override
	public String getSpeciesName() {
		return speciesName;
	}

	public void setCellSize(final GamaPoint p) {
		// We reuse the unused attribute
		geometryProperties.location = p;

	}

	public GamaPoint getCellSize() {
		return geometryProperties.location;
	}

}