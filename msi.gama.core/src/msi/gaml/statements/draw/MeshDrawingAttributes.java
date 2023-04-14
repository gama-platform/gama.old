/*******************************************************************************************************
 *
 * MeshDrawingAttributes.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.List;

import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.MeshLayerData;
import msi.gama.util.GamaColor;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaField;
import msi.gama.util.matrix.IField;
import msi.gaml.operators.Colors.GamaGradient;
import msi.gaml.operators.Colors.GamaPalette;
import msi.gaml.operators.Colors.GamaScale;

/**
 * The Class MeshDrawingAttributes.
 */
public class MeshDrawingAttributes extends AssetDrawingAttributes {

	/** The smooth provider. */
	public IMeshSmoothProvider smoothProvider;

	/** The color. */
	public IMeshColorProvider colorProvider;

	/** The species name. */
	public String speciesName;

	/** The dimensions. */
	GamaPoint dimensions;

	/** The cell size. */
	GamaPoint cellSize;

	/** The scale. */
	Double scale;

	/** The no data. */
	double noData = IField.NO_NO_DATA;

	/** The above. */
	double above = MeshLayerData.ABOVE;

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
	public MeshDrawingAttributes(final String name, final boolean isImage) {
		super(null, isImage);
		speciesName = name;
		smoothProvider = IMeshSmoothProvider.NULL;
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
	public void setColors(final Object colors) { colorProvider = computeColors(colors, isGrayscaled()); }

	/**
	 * Compute colors.
	 *
	 * @param colors
	 *            the colors
	 * @return the i mesh color provider
	 */
	@SuppressWarnings ("unchecked")
	public static IMeshColorProvider computeColors(final Object colors, final boolean isGrayscale) {
		if (colors instanceof GamaColor) return new ColorBasedMeshColorProvider((GamaColor) colors);
		if (colors instanceof GamaPalette) return new PaletteBasedMeshColorProvider((GamaPalette) colors);
		if (colors instanceof GamaScale)
			return new ScaleBasedMeshColorProvider((GamaScale) colors);
		else if (colors instanceof GamaGradient)
			return new GradientBasedMeshColorProvider((GamaGradient) colors);
		else if (colors instanceof IList) {
			if (((IList) colors).get(0) instanceof GamaField) // We have bands
				return new BandsBasedMeshColorProvider((List<GamaField>) colors);
			else
				return new ListBasedMeshColorProvider((IList<Color>) colors);
		} else if (isGrayscale)
			return IMeshColorProvider.GRAYSCALE;
		else
			return IMeshColorProvider.DEFAULT;
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
		return colorProvider;
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
	// public void setCellSize(final GamaPoint p) { cellSize = p; }

	/**
	 * Gets the cell size.
	 *
	 * @return the cell size
	 */
	// public GamaPoint getCellSize() { return cellSize; }

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
		if (colorProvider == null) { colorProvider = new GrayscaleMeshColorProvider(); }
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
	 * Sets the smooth. Reinitialized the smooth provider
	 *
	 * @param smooth
	 *            the new smooth
	 */
	public void setSmooth(final int smooth) {
		this.smooth = smooth;
		smoothProvider = IMeshSmoothProvider.FOR(smooth, noData);
	}

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
	public void setNoData(final double noData) {
		this.noData = noData;
		smoothProvider = IMeshSmoothProvider.FOR(smooth, noData);
	}

	/**
	 * Gets the no data value.
	 *
	 * @return the no data value
	 */
	public double getNoDataValue() { return noData; }

	/**
	 * Sets the above.
	 *
	 * @param above2
	 *            the new above
	 */
	public void setAbove(final double above2) { above = above2; }

	/**
	 * Gets the above.
	 *
	 * @return the above
	 */
	public double getAbove() { return above; }

	/**
	 * Gets the smooth provider.
	 *
	 * @return the smooth provider
	 */
	public IMeshSmoothProvider getSmoothProvider() { return smoothProvider; }

}