/*******************************************************************************************************
 *
 * MeshDrawingAttributes.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.Color;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gama.util.IList;
import msi.gama.util.matrix.IField;
import msi.gaml.operators.Colors.GamaGradient;
import msi.gaml.operators.Colors.GamaPalette;
import msi.gaml.operators.Colors.GamaScale;

/**
 * The Class MeshDrawingAttributes.
 */
public class MeshDrawingAttributes extends FileDrawingAttributes {

	/** The color. */
	public IMeshColorProvider color;

	/** The species name. */
	public String speciesName;

	/** The dimensions. */
	GamaPoint dimensions;

	/** The cell size. */
	GamaPoint cellSize;

	/** The scale. */
	Double scale;

	/** The no data. */
	double noData;

	/** The smooth. */
	int smooth;

	/**
	 * Instantiates a new mesh drawing attributes.
	 *
	 * @param name
	 *            the name
	 * @param border
	 *            the border
	 * @param isImage
	 *            the is image
	 */
	public MeshDrawingAttributes(final String name, final GamaColor border, final boolean isImage) {
		super(null, isImage);
		setBorder(border);
		speciesName = name;
	}

	/**
	 * Sets the species name.
	 *
	 * @param name
	 *            the new species name
	 */
	public void setSpeciesName(final String name) { speciesName = name; }

	/**
	 * Sets the colors.
	 *
	 * @param colors
	 *            the new colors
	 */
	@SuppressWarnings ("unchecked")
	public void setColors(final Object colors) {
		if (colors instanceof GamaColor) {
			color = new ColorBasedMeshColorProvider((GamaColor) colors);
		} else if (colors instanceof GamaPalette) {
			color = new PaletteBasedMeshColorProvider((GamaPalette) colors);
		} else if (colors instanceof GamaScale) {
			color = new ScaleBasedMeshColorProvider((GamaScale) colors);
		} else if (colors instanceof GamaGradient) {
			color = new GradientBasedMeshColorProvider((GamaGradient) colors);
		} else if (colors instanceof IList) {
			if (((IList) colors).get(0) instanceof IField) {
				// We have bands
				color = new BandsBasedMeshColorProvider((IList<IField>) colors);
			} else {
				color = new ListBasedMeshColorProvider((IList<Color>) colors);
			}
		} else if (isGrayscaled()) {
			color = IMeshColorProvider.GRAYSCALE;
		} else {
			color = IMeshColorProvider.DEFAULT;
		}
	}

	// Rules are a bit different for the fill color for fields.

	/**
	 * Gets the color provider.
	 *
	 * @return the color provider
	 */
	public IMeshColorProvider getColorProvider() {
		if (isSet(Flag.Selected)) return new ColorBasedMeshColorProvider(SELECTED_COLOR);
		if (highlight != null) return new ColorBasedMeshColorProvider(highlight);
		if (isSet(Flag.Empty)) return null;
		return color;
	}

	@Override
	public GamaColor getColor() {
		if (isSet(Flag.Selected)) return SELECTED_COLOR;
		if (highlight != null) return highlight;
		if (isSet(Flag.Empty) || isSet(Flag.Grayscaled)) return null;
		if (textures != null) return TEXTURED_COLOR;
		return fill == null ? GamaPreferences.Displays.CORE_COLOR.getValue() : fill;
	}

	@Override
	public String getSpeciesName() { return speciesName; }

	/**
	 * Gets the XY dimension.
	 *
	 * @return the XY dimension
	 */
	public GamaPoint getXYDimension() { return dimensions; }

	/**
	 * Sets the XY dimension.
	 *
	 * @param dim
	 *            the new XY dimension
	 */
	public void setXYDimension(final GamaPoint dim) { dimensions = dim; }

	/**
	 * Sets the cell size.
	 *
	 * @param p
	 *            the new cell size
	 */
	public void setCellSize(final GamaPoint p) { cellSize = p; }

	/**
	 * Gets the cell size.
	 *
	 * @return the cell size
	 */
	public GamaPoint getCellSize() { return cellSize; }

	/**
	 * Sets the scale.
	 *
	 * @param s
	 *            the new scale
	 */
	public void setScale(final Double s) { scale = s; }

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
	public double getZFactor() { return getSize().getZ(); }

	/**
	 * Checks if is triangulated.
	 *
	 * @return true, if is triangulated
	 */
	public boolean isTriangulated() { return isSet(Flag.Triangulated); }

	/**
	 * Checks if is grayscaled.
	 *
	 * @return true, if is grayscaled
	 */
	public boolean isGrayscaled() { return isSet(Flag.Grayscaled); }

	/**
	 * Checks if is with text.
	 *
	 * @return true, if is with text
	 */
	public boolean isWithText() { return isSet(Flag.WithText); }

	/**
	 * Sets the grayscaled.
	 *
	 * @param grayScaled2
	 *            the new grayscaled
	 */
	public void setGrayscaled(final Boolean grayScaled2) {
		if (color == null) { color = new GrayscaleMeshColorProvider(); }
		setFlag(Flag.Grayscaled, grayScaled2);
	}

	/**
	 * Sets the triangulated.
	 *
	 * @param triangulated2
	 *            the new triangulated
	 */
	public void setTriangulated(final Boolean triangulated2) {
		setFlag(Flag.Triangulated, triangulated2);
	}

	/**
	 * Sets the with text.
	 *
	 * @param showText
	 *            the new with text
	 */
	public void setWithText(final Boolean showText) {
		setFlag(Flag.WithText, showText);
	}

	/**
	 * Sets the smooth.
	 *
	 * @param smooth
	 *            the new smooth
	 */
	public void setSmooth(final int smooth) { this.smooth = smooth; }

	/**
	 * Gets the smooth.
	 *
	 * @return the smooth
	 */
	public int getSmooth() { return smooth; }

	/**
	 * Sets the no data.
	 *
	 * @param noData
	 *            the new no data
	 */
	public void setNoData(final double noData) { this.noData = noData; }

	/**
	 * Gets the no data value.
	 *
	 * @return the no data value
	 */
	public double getNoDataValue() { return noData; }

}