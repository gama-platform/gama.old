/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.FieldDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;

public class MeshDrawingAttributes extends FileDrawingAttributes {

	public static final int TRIANGULATED = 32;
	public static final int GRAYSCALED = 64;
	public static final int WITH_TEXT = 128;
	public static final int SMOOTH = 256;
	public String speciesName;
	GamaPoint dimensions;
	GamaPoint cellSize;
	Double scale;

	public MeshDrawingAttributes(final String name, final GamaColor border, final boolean isImage) {
		super(null, isImage);
		setBorder(border);
		speciesName = name;
	}

	public void setSpeciesName(final String name) {
		speciesName = name;
	}

	// Rules are a bit different for the fill color for fields.

	@Override
	public GamaColor getColor() {
		if (isSet(SELECTED)) return SELECTED_COLOR;
		if (highlight != null) return highlight;
		if (isSet(EMPTY) || isSet(GRAYSCALED)) return null;
		if (textures != null) return TEXTURED_COLOR;
		return fill == null ? GamaPreferences.Displays.CORE_COLOR.getValue() : fill;
	}

	@Override
	public String getSpeciesName() {
		return speciesName;
	}

	public GamaPoint getXYDimension() {
		return dimensions;
	}

	public void setXYDimension(final GamaPoint dim) {
		dimensions = dim;
	}

	public void setCellSize(final GamaPoint p) {
		cellSize = p;
	}

	public GamaPoint getCellSize() {
		return cellSize;
	}

	public void setScale(final Double s) {
		scale = s;
	}

	/**
	 * Returns the z-scaling factor for this field
	 *
	 * @return
	 */
	public Double getScale() {
		if (scale == null) {
			Scaling3D size = getSize();
			return size == null ? 1d : size.getZ();
		}
		return scale;
	}

	/**
	 * A value > 1 to indicate a maximum; a value between 0 and 1 to indicate a scaling of the elevation values
	 *
	 * @return
	 */
	public double getZFactor() {
		return getSize().getZ();
	}

	public boolean isTriangulated() {
		return isSet(TRIANGULATED);
	}

	public boolean isGrayscaled() {
		return isSet(GRAYSCALED);
	}

	public boolean isWithText() {
		return isSet(WITH_TEXT);
	}

	public void setGrayscaled(final Boolean grayScaled2) {
		setFlag(GRAYSCALED, grayScaled2);
	}

	public void setTriangulated(final Boolean triangulated2) {
		setFlag(TRIANGULATED, triangulated2);
	}

	public void setWithText(final Boolean showText) {
		setFlag(WITH_TEXT, showText);
	}

	public boolean isTextured() {
		return getTextures() != null;
	}

	public void setSmooth(final Boolean smooth) {
		setFlag(SMOOTH, smooth);
	}

	public boolean isSmooth() {
		return isSet(SMOOTH);
	}

}