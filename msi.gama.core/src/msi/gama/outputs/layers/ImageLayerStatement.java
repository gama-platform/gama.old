/*********************************************************************************************
 *
 * 'ImageLayerStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import msi.gama.util.GAML;
import msi.gama.util.GamaColor;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol (
		name = IKeyword.IMAGE,
		kind = ISymbolKind.LAYER,
		with_sequence = false,
		concept = { IConcept.DISPLAY, IConcept.FILE, IConcept.LOAD_FILE })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = { @facet (
				name = IKeyword.FILE,
				type = { IType.STRING, IType.FILE },
				optional = true,
				doc = @doc ("the name/path of the image (in the case of a raster image)")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1 for x and y. The z-ordinate can only be defined between 0 and 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
				@facet (
						name = IKeyword.SIZE,
						type = IType.POINT,
						optional = true,
						doc = @doc ("extent of the layer in the screen from its position. Coordinates in [0,1[ are treated as percentages of the total surface, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Like in 'position', an elevation can be provided with the z coordinate, allowing to scale the layer in the 3 directions ")),
				@facet (
						name = IKeyword.TRANSPARENCY,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the transparency rate of the agents (between 0 and 1, 1 means no transparency)")),
				@facet (
						name = IKeyword.NAME,
						type = { IType.STRING, IType.FILE },
						optional = true,
						doc = @doc ("Human readable title of the image layer")),
				@facet (
						name = IKeyword.GIS,
						type = { IType.FILE, IType.STRING },
						optional = true,
						doc = @doc ("the name/path of the shape file (to display a shapefile as background, without creating agents from it)")),
				@facet (
						name = IKeyword.COLOR,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("in the case of a shapefile, this the color used to fill in geometries of the shapefile. In the case of an image, it is used to tint the image")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("(openGL only) specify whether the image display is refreshed or not. (false by default, true should be used in cases of images that are modified over the simulation)")) },
		omissible = IKeyword.NAME)
@doc (
		value = "`" + IKeyword.IMAGE + "` allows modeler to display an image (e.g. as background of a simulation).",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "display my_display {",
						isExecutable = false),
						@example (
								value = "   image layer_name file: image_file [additional options];",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "For instance, in the case of a bitmap image",
						examples = { @example (
								value = "display my_display {",
								isExecutable = false),
								@example (
										value = "   image background file:\"../images/my_backgound.jpg\";",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "Or in the case of a shapefile:",
						examples = { @example (
								value = "display my_display {",
								isExecutable = false),
								@example (
										value = "   image testGIS gis: \"../includes/building.shp\" color: rgb('blue');",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "It is also possible to superpose images on different layers in the same way as for species using opengl display:",
						examples = { @example (
								value = "display my_display {",
								isExecutable = false),
								@example (
										value = "  image image1 file:\"../images/image1.jpg\";",
										isExecutable = false),
								@example (
										value = "  image image2 file:\"../images/image2.jpg\";",
										isExecutable = false),
								@example (
										value = "  image image3 file:\"../images/image3.jpg\" position: {0,0,0.5};",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
				IKeyword.OVERLAY, IKeyword.POPULATION })
public class ImageLayerStatement extends AbstractLayerStatement {

	public ImageLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);

		imageFileExpression = getFacet(IKeyword.FILE, IKeyword.NAME);
		gisExpression = getFacet(IKeyword.GIS);
		colorExpression = getFacet(IKeyword.COLOR);
	}

	/**
	 * In this particular case, returns false by default;
	 */
	@Override
	public IExpression getRefreshFacet() {
		IExpression exp = super.getRefreshFacet();
		if (exp == null)
			exp = IExpressionFactory.FALSE_EXPR;
		return exp;
	}

	final IExpression imageFileExpression;
	IExpression gisExpression;
	final IExpression colorExpression;
	String constantImage = null;
	String currentImage = null;
	GamaColor color = null;

	public GamaColor getColor() {
		return color;
	}

	@Override
	public short getType() {
		if (gisExpression == null) { return ILayerStatement.IMAGE; }
		return ILayerStatement.GIS;
	}

	public String getImageFileName() {
		return currentImage;
	}

	// FIXME Use GamaImageFile
	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		if (gisExpression == null) {
			if (constantImage == null) {
				// Redefined to allow replacing the "name" attribute by "file"
				IExpression tag = getFacet(IKeyword.NAME);
				if (tag == null) {
					tag = getFacet(IKeyword.FILE);
				}
				if (tag == null) { throw GamaRuntimeException
						.error("Missing properties " + IKeyword.NAME + " and " + IKeyword.FILE, scope); }
				if (tag.isConst()) {
					setName(Cast.asString(scope, tag.value(scope)));
				} else {
					setName(tag.serialize(false));
				}
				if (imageFileExpression == null) { throw GamaRuntimeException.error("Image file not defined", scope); }
				if (imageFileExpression.isConst()) {
					constantImage = Cast.asString(scope, imageFileExpression.value(scope));
					currentImage = constantImage;
					try {
						ImageUtils.getInstance().getImageFromFile(scope, constantImage, !getRefresh());
					} catch (final GamaRuntimeFileException ex) {
						constantImage = null;
						throw ex;
					} catch (final Throwable e) {
						constantImage = null;
						throw GamaRuntimeException.create(e, scope);
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		if (gisExpression == null) {
			currentImage =
					constantImage != null ? constantImage : Cast.asString(scope, imageFileExpression.value(scope));
		}
		return true;
	}

	/**
	 * @throws GamlException
	 * @throws GamaRuntimeException
	 * @param newValue
	 */
	public void setGisLayerName(final IScope scope, final String newValue) throws GamaRuntimeException {
		gisExpression = GAML.getExpressionFactory().createConst(newValue, Types.STRING);
	}

	/**
	 * @param newValue
	 */
	public void setImageFileName(final String newValue) {
		constantImage = newValue;
	}

}
