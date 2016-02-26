/*********************************************************************************************
 *
 *
 * 'GridLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.outputs.layers.GridLayerStatement.GridLayerSerializer;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaImageFile;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol(name = IKeyword.GRID_POPULATION, kind = ISymbolKind.LAYER, with_sequence = false, concept = { IConcept.GRID, IConcept.DISPLAY, IConcept.INSPECTOR })
@inside(symbols = IKeyword.DISPLAY)
@facets(
	value = { @facet(name = IKeyword.POSITION,
		type = IType.POINT,
		optional = true,
		doc = @doc("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1. The position can also be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.") ),
		@facet(name = IKeyword.SELECTABLE,
			type = { IType.BOOL },
			optional = true,
			doc = @doc("Indicates whether the agents present on this layer are selectable by the user. Default is true") ),
		@facet(name = IKeyword.SIZE,
			type = IType.POINT,
			optional = true,
			doc = @doc("extent of the layer in the screen from its position. Coordinates in [0,1[ are treated as percentages of the total surface, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Like in 'position', an elevation can be provided with the z coordinate, allowing to scale the layer in the 3 directions ") ),
		@facet(name = IKeyword.TRANSPARENCY,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("the transparency rate of the agents (between 0 and 1, 1 means no transparency)") ),
		@facet(name = IKeyword.SPECIES,
			type = IType.SPECIES,
			optional = false,
			doc = @doc("the species of the agents in the grid") ),
		@facet(name = IKeyword.LINES,
			type = IType.COLOR,
			optional = true,
			doc = @doc("the color to draw lines (borders of cells)") ),
		@facet(name = IKeyword.ELEVATION,
			type = { IType.MATRIX, IType.FLOAT, IType.INT, IType.BOOL },
			optional = true,
			doc = @doc("Allows to specify the elevation of each cell, if any. Can be a matrix of float (provided it has the same size than the grid), an int or float variable of the grid species, or simply true (in which case, the variable called 'grid_value' is used to compute the elevation of each cell)") ),
		@facet(name = IKeyword.TEXTURE,
			type = { IType.BOOL, IType.FILE },
			optional = true,
			doc = @doc("Either file  containing the texture image to be applied on the grid or, if true, the use of the image composed by the colors of the cells. If false, no texture is applied") ),
		@facet(name = IKeyword.GRAYSCALE,
			type = IType.BOOL,
			optional = true,
			doc = @doc("if true, givse a grey value to each polygon depending on its elevation (false by default)") ),
		@facet(name = IKeyword.TRIANGULATION,
			type = IType.BOOL,
			optional = true,
			doc = @doc("specifies whther the cells will be triangulated: if it is false, they will be displayed as horizontal squares at a given elevation, whereas if it is true, cells will be triangulated and linked to neighbors in order to have a continuous surface (false by default)") ),
		@facet(name = IKeyword.TEXT,
			type = IType.BOOL,
			optional = true,
			doc = @doc("specify whether the attribute used to compute the elevation is displayed on each cells (false by default)") ),
		@facet(name = "draw_as_dem",
			type = IType.BOOL,
			optional = true,
			doc = @doc(deprecated = "use 'elevation' instead") ),
		@facet(name = "dem", type = IType.MATRIX, optional = true, doc = @doc(deprecated = "use 'elevation' instead") ),
		@facet(name = IKeyword.REFRESH,
			type = IType.BOOL,
			optional = true,
			doc = @doc("(openGL only) specify whether the display of the species is refreshed. (true by default, usefull in case of agents that do not move)") ) },
	omissible = IKeyword.SPECIES)
@doc(
	value = "`" + IKeyword.GRID_POPULATION + "` is used using the `" + IKeyword.GRID +
		"` keyword. It allows the modeler to display in an optimized way all cell agents of a grid (i.e. all agents of a species having a grid topology).",
	usages = { @usage(value = "The general syntax is:",
		examples = { @example(value = "display my_display {", isExecutable = false),
			@example(value = "   grid ant_grid lines: #black position: { 0.5, 0 } size: {0.5,0.5};",
				isExecutable = false),
			@example(value = "}", isExecutable = false) }),
		@usage(value = "To display a grid as a DEM:",
			examples = { @example(value = "display my_display {", isExecutable = false),
				@example(value = "    grid cell texture: texture_file text: false triangulation: true elevation: true;",
					isExecutable = false),
				@example(value = "}", isExecutable = false) }) },
	see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.IMAGE,
		IKeyword.OVERLAY, IKeyword.QUADTREE, IKeyword.POPULATION, IKeyword.TEXT })
@serializer(GridLayerSerializer.class)
public class GridLayerStatement extends AbstractLayerStatement {

	public static class GridLayerSerializer extends SymbolSerializer {

		@Override
		protected void serializeKeyword(final SymbolDescription desc, final StringBuilder sb,
			final boolean includingBuiltIn) {
			sb.append("grid ");
		}

	}

	public GridLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(getFacet(IKeyword.SPECIES).literalValue());
	}

	IGrid grid;
	IExpression lineColor, elevation, textureExp, triExp, textExp, gsExp;
	// Boolean isTextured = false;
	GamaImageFile textureFile = null;
	Boolean isTriangulated = false;
	Boolean isGrayScaled = false;
	Boolean showText = false;
	GamaColor currentColor, constantColor;
	private IExpression setOfAgents;

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		lineColor = getFacet(IKeyword.LINES);
		if ( lineColor != null ) {
			constantColor = Cast.asColor(scope, lineColor.value(scope));
			currentColor = constantColor;
		}

		elevation = getFacet(IKeyword.ELEVATION, "dem", "draw_as_dem");
		textureExp = getFacet(IKeyword.TEXTURE);
		if ( textureExp != null ) {
			switch (textureExp.getType().id()) {
				case IType.BOOL:
					// isTextured = Cast.asBool(scope, textureExp.value(scope));
					break;
				case IType.FILE:
					Object result = textureExp.value(scope);
					if ( result instanceof GamaImageFile ) {
						textureFile = (GamaImageFile) textureExp.value(scope);
						// isTextured = true;
					} else {
						throw GamaRuntimeException.error("The texture of grids must be an image file", scope);
					}
					break;
			}
		}

		triExp = getFacet(IKeyword.TRIANGULATION);
		if ( triExp != null ) {
			isTriangulated = Cast.asBool(scope, triExp.value(scope));
		}

		gsExp = getFacet(IKeyword.GRAYSCALE);
		if ( gsExp != null ) {
			isGrayScaled = Cast.asBool(scope, gsExp.value(scope));
		}

		textExp = getFacet(IKeyword.TEXT);
		if ( textExp != null ) {
			showText = Cast.asBool(scope, textExp.value(scope));
		}

		final IPopulation gridPop = scope.getAgentScope().getPopulationFor(getName());
		if ( gridPop == null ) {
			throw GamaRuntimeException.error("missing environment for output " + getName(), scope);
		} else if ( !gridPop
			.isGrid() ) { throw GamaRuntimeException.error("not a grid environment for: " + getName(), scope); }

		grid = (IGrid) gridPop.getTopology().getPlaces();

		return true;
	}

	public IGrid getEnvironment() {
		return grid;
	}

	@Override
	public short getType() {
		if ( grid.isHexagon() ) { return ILayerStatement.AGENTS; }
		return ILayerStatement.GRID;
	}

	public GamaColor getLineColor() {
		return currentColor;
	}

	public boolean drawLines() {
		return currentColor != null;
	}

	public boolean agentsHaveChanged() {
		return false;
	}

	@Override
	public boolean _step(final IScope sim) throws GamaRuntimeException {
		if ( grid.isHexagon() ) {
			// synchronized (agents) {
			// if ( sim.getClock().getCycle() == 0 || agentsHaveChanged() ) {
			// agents.clear();
			// agents.addAll(computeAgents());
			// }
			// agentsForLayer = (HashSet<IAgent>) agents.clone();
			// }
		} else {
			if ( lineColor == null || constantColor != null ) { return true; }
			currentColor = Cast.asColor(sim, lineColor.value(sim));
		}
		return true;
	}

	public synchronized Collection<IAgent> getAgentsToDisplay() {
		return grid.getAgents();
	}

	public List<? extends IAgent> computeAgents() throws GamaRuntimeException {
		return grid.getAgents();
	}

	public void setAspect(final String currentAspect) {}

	public String getAspectName() {
		return IKeyword.DEFAULT;
	}

	public void setAgentsExpr(final IExpression setOfAgents) {
		this.setOfAgents = setOfAgents;
	}

	IExpression getAgentsExpr() {
		return setOfAgents;
	}

	public double[] getElevationMatrix(final IScope scope) {
		if ( elevation != null ) {
			switch (elevation.getType().id()) {
				case IType.MATRIX:
					return GamaFloatMatrix.from(scope, Cast.asMatrix(scope, elevation.value(scope))).getMatrix();
				case IType.FLOAT:
				case IType.INT:
					return grid.getGridValueOf(scope, elevation);
				case IType.BOOL:
					if ( (Boolean) elevation.value(scope) ) {
						return grid.getGridValue();
					} else {
						return null;
					}
			}
		}
		return null;
	}

	// public Boolean isTextured() {
	// return isTextured;
	// }

	public GamaImageFile textureFile() {
		return textureFile;
	}

	public Boolean isTriangulated() {
		return isTriangulated;
	}

	public Boolean isGrayScaled() {
		return isGrayScaled;
	}

	public Boolean isShowText() {
		return showText;
	}

}
