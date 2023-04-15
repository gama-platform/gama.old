/*******************************************************************************************************
 *
 * ImageLayerStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.ImageLayerStatement.ImageLayerValidator;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol (
		name = IKeyword.IMAGE_LAYER,
		kind = ISymbolKind.LAYER,
		with_sequence = false,
		concept = { IConcept.DISPLAY, IConcept.FILE, IConcept.LOAD_FILE })

@facets (
		value = { @facet (
				name = IKeyword.FILE,
				type = { IType.NONE },
				optional = true,
				doc = @doc (
						deprecated = "Directly pass the name or the file itself to the default facet",
						value = "the name/path of the image (in the case of a raster image), a matrix of int, an image file")),
				@facet (
						name = IKeyword.ROTATE,
						type = { IType.FLOAT },
						optional = true,
						doc = @doc ("Defines the angle of rotation of this layer, in degrees, around the z-axis.")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1 for x and y. The z-ordinate can only be defined between 0 and 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer. In case of negative value OpenGl will position the layer out of the environment.")),
				@facet (
						name = IKeyword.SIZE,
						type = IType.POINT,
						optional = true,
						doc = @doc ("extent of the layer in the screen from its position. Coordinates in [0,1[ are treated as percentages of the total surface, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Like in 'position', an elevation can be provided with the z coordinate, allowing to scale the layer in the 3 directions ")),
				@facet (
						name = IKeyword.TRANSPARENCY,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the transparency level of the layer (between 0 -- opaque -- and 1 -- fully transparent)")),
				@facet (
						name = IKeyword.VISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Defines whether this layer is visible or not")),
				@facet (
						name = IKeyword.NAME,
						type = { IType.NONE },
						optional = true,
						doc = @doc ("the name/path of the image (in the case of a raster image), a matrix of int, an image file")),
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
						name = IKeyword.MATRIX,
						type = { IType.MATRIX },
						optional = true,
						doc = @doc (
								value = "the matrix containing the values of each pixel as integer following ARGB format",
								deprecated = "Use a 'field' and a 'mesh' layer instead, or simply pass the matrix to the default facet")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("(openGL only) specify whether the image display is refreshed or not. (false by default, true should be used in cases of images that are modified over the simulation)")) },
		omissible = IKeyword.NAME)
@doc (
		value = "`" + IKeyword.IMAGE_LAYER
				+ "` allows modeler to display an image (e.g. as background of a simulation). Note that this image will not be dynamically changed or moved in OpenGL, unless the refresh: facet is set to true.",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "display my_display {",
						isExecutable = false),
						@example (
								value = "   image image_file [additional options];",
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
										value = "   image \"../images/my_backgound.jpg\";",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }),
				@usage (
						value = "If you already have your image stored in a matrix",
						examples = { @example (
								value = "display my_display {",
								isExecutable = false),
								@example (
										value = "   image my_image_matrix;",
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
										value = "  image \"../images/image1.jpg\";",
										isExecutable = false),
								@example (
										value = "  image \"../images/image2.jpg\";",
										isExecutable = false),
								@example (
										value = "  image \"../images/image3.jpg\" position: {0,0,0.5};",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_LAYER,
				IKeyword.OVERLAY, IKeyword.SPECIES_LAYER })
@validator (ImageLayerValidator.class)
public class ImageLayerStatement extends AbstractLayerStatement {

	/**
	 * The Class ImageLayerValidator.
	 */
	public static class ImageLayerValidator implements IDescriptionValidator<StatementDescription> {

		@Override
		public void validate(final StatementDescription description) {
			if (!description.hasFacet(GIS)) {
				if (!description.hasFacet(NAME) && !description.hasFacet(FILE) && !description.hasFacet(MATRIX)) {
					description.error(
							"Missing facets " + IKeyword.NAME + " or " + IKeyword.FILE + " or " + IKeyword.MATRIX,
							IGamlIssue.MISSING_FACET, description.getUnderlyingElement(), FILE, "\"\"");
				}
			} else if (description.hasFacet(FILE)) {
				description.error("gis: and file: cannot be defined at the same time", IGamlIssue.CONFLICTING_FACETS);
			}
		}

	}

	/** The file. */
	IExpression file;

	/** The matrix. */
	IExpression matrix;

	/**
	 * Instantiates a new image layer statement.
	 *
	 * @param desc
	 *            the desc
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public ImageLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		file = getFacet(IKeyword.FILE, IKeyword.NAME);
		matrix = file == null ? getFacet(IKeyword.MATRIX, IKeyword.NAME) : null;
	}

	/**
	 * In this particular case, returns false by default;
	 */
	@Override
	public IExpression getRefreshFacet() {
		IExpression exp = super.getRefreshFacet();
		if (exp == null) { exp = IExpressionFactory.FALSE_EXPR; }
		return exp;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		if (hasFacet(IKeyword.GIS)) return LayerType.GIS;
		return LayerType.IMAGE;
	}

	// FIXME Use GamaImageFile
	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		if (file.isConst()) {
			setName(Cast.asString(scope, file.value(scope)));
		} else {
			setName(file.serialize(false));
		}
		return true;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		return true;
	}

}
