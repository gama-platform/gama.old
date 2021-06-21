/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.GridLayerStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import static msi.gama.common.interfaces.IKeyword.EMPTY;
import static msi.gaml.expressions.IExpressionFactory.TRUE_EXPR;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.GridLayerStatement.GridLayerSerializer;
import msi.gama.outputs.layers.GridLayerStatement.GridLayerValidator;
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
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol (
		name = IKeyword.GRID_POPULATION,
		kind = ISymbolKind.LAYER,
		with_sequence = true, // Necessary to declare the elevation facet as remote itself
		remote_context = true,
		concept = { IConcept.GRID, IConcept.DISPLAY, IConcept.INSPECTOR })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = { @facet (
				name = IKeyword.POSITION,
				type = IType.POINT,
				optional = true,
				doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1 for x and y. The z-ordinate can only be defined between 0 and 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
				@facet (
						name = IKeyword.SELECTABLE,
						type = { IType.BOOL },
						optional = true,
						doc = @doc ("Indicates whether the agents present on this layer are selectable by the user. Default is true")),
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
						name = IKeyword.SPECIES,
						type = IType.SPECIES,
						optional = false,
						doc = @doc ("the species of the agents in the grid")),
				@facet (
						name = IKeyword.LINES,
						type = IType.COLOR,
						optional = true,
						doc = @doc (
								deprecated = "use 'border' instead",
								value = "the color to draw lines (borders of cells)")),
				@facet (
						name = IKeyword.BORDER,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("the color to draw lines (borders of cells)")),
				@facet (
						name = IKeyword.ELEVATION,
						type = { IType.MATRIX, IType.FLOAT, IType.INT, IType.BOOL },
						remote_context = true,
						optional = true,
						doc = @doc ("Allows to specify the elevation of each cell, if any. Can be a matrix of float (provided it has the same size than the grid), an int or float variable of the grid species, or simply true (in which case, the variable called 'grid_value' is used to compute the elevation of each cell)")),
				@facet (
						name = IKeyword.TEXTURE,
						type = { IType.FILE },
						optional = true,
						doc = @doc ("Either file  containing the texture image to be applied on the grid or, if not specified, the use of the image composed by the colors of the cells")),
				@facet (
						name = IKeyword.SMOOTH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Applies a simple convolution (box filter) to smooth out the terrain produced by this field. Does not change the values of course.")),
				@facet (
						name = IKeyword.GRAYSCALE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("if true, givse a grey value to each polygon depending on its elevation (false by default)")),
				@facet (
						name = IKeyword.TRIANGULATION,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("specifies whther the cells will be triangulated: if it is false, they will be displayed as horizontal squares at a given elevation, whereas if it is true, cells will be triangulated and linked to neighbors in order to have a continuous surface (false by default)")),
				@facet (
						name = "hexagonal",
						type = IType.BOOL,
						optional = true,
						internal = true,
						doc = @doc ("")),
				@facet (
						name = IKeyword.TEXT,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("specify whether the attribute used to compute the elevation is displayed on each cells (false by default)")),
				@facet (
						name = "draw_as_dem",
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								deprecated = "use 'elevation' instead. This facet is not functional anymore")),
				@facet (
						name = EMPTY,
						type = IType.BOOL,
						optional = true,
						doc = @doc (
								deprecated = "Use 'wireframe' instead",
								value = "if true displays the grid in wireframe using the lines color")),
				@facet (
						name = IKeyword.WIREFRAME,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("if true displays the grid in wireframe using the lines color")),
				@facet (
						name = "dem",
						type = IType.MATRIX,
						optional = true,
						doc = @doc (
								deprecated = "use 'elevation' instead. This facet is not functional anymore")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("(openGL only) specify whether the display of the species is refreshed. (true by default, usefull in case of agents that do not move)")) },
		omissible = IKeyword.SPECIES)
@doc (
		value = "`" + IKeyword.GRID_POPULATION + "` is used using the `" + IKeyword.GRID
				+ "` keyword. It allows the modeler to display in an optimized way all cell agents of a grid (i.e. all agents of a species having a grid topology).",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "display my_display {",
						isExecutable = false),
						@example (
								value = "   grid ant_grid lines: #black position: { 0.5, 0 } size: {0.5,0.5};",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "To display a grid as a DEM:",
						examples = { @example (
								value = "display my_display {",
								isExecutable = false),
								@example (
										value = "    grid cell texture: texture_file text: false triangulation: true elevation: true;",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.IMAGE,
				IKeyword.OVERLAY, IKeyword.POPULATION })
@serializer (GridLayerSerializer.class)
@validator (GridLayerValidator.class)
public class GridLayerStatement extends AbstractLayerStatement {

	public static class GridLayerSerializer extends SymbolSerializer<SymbolDescription> {

		@Override
		protected void serializeKeyword(final SymbolDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			sb.append("grid ");
		}

	}

	public static class GridLayerValidator implements IDescriptionValidator<StatementDescription> {

		@Override
		public void validate(final StatementDescription d) {
			final IExpressionDescription empty = d.getFacet(IKeyword.EMPTY);
			if (empty != null) {
				d.setFacet(IKeyword.WIREFRAME, empty);
				d.removeFacets(EMPTY);
			}
			final String name = d.getFacet(SPECIES).serialize(true);
			final SpeciesDescription sd = d.getModelDescription().getSpeciesDescription(name);
			if (sd == null || !sd.isGrid()) {
				d.error(name + " is not a grid species", IGamlIssue.WRONG_TYPE, SPECIES);
				return;
			}
			final IExpression exp = sd.getFacetExpr(NEIGHBORS);
			if (exp != null && exp.isConst()) {
				final Integer n = (Integer) exp.getConstValue();
				if (n == 6) { d.setFacet("hexagonal", TRUE_EXPR); }
			}
			final IExpression lines = d.getFacetExpr(LINES);
			if (lines != null) { d.setFacet(BORDER, lines); }
			final IExpression tx = d.getFacetExpr(TEXTURE);
			final IExpression el = d.getFacetExpr(ELEVATION);
			if (el == null || FALSE.equals(el.serialize(true))) {
				if (tx == null) {
					d.setFacet("flat", TRUE_EXPR);
				} else {
					// if texture is defined and elevation no, we need to set a fake elevation otherwise texture will
					// not be drawn
					d.setFacet(ELEVATION, GAML.getExpressionFactory().createConst(0.0, Types.FLOAT));
				}
			}
		}

	}

	final boolean isHexagonal, isFlatGrid;

	public GridLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(getFacet(IKeyword.SPECIES).literalValue());
		isHexagonal = desc.hasFacet("hexagonal");
		isFlatGrid = desc.hasFacet("flat");
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		return true;
	}

	boolean isOpenGLFlatGrid(final LayeredDisplayOutput out) {
		final boolean isOpenGL = out.getData().isOpenGL();
		return isOpenGL && isFlatGrid;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput out) {
		return isHexagonal || isOpenGLFlatGrid(out) ? LayerType.GRID_AGENTS : LayerType.GRID;
	}

	@Override
	public boolean _step(final IScope sim) throws GamaRuntimeException {
		return true;
	}

}
