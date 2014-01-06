/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaFile;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.GRID_POPULATION, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = {
	@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.SPECIES, type = IType.SPECIES, optional = false),
	@facet(name = IKeyword.LINES, type = IType.COLOR, optional = true),
	@facet(name = IKeyword.ELEVATION, type = { IType.MATRIX, IType.FLOAT, IType.INT, IType.BOOL }, optional = true, doc = @doc("Allows to specify the elevation of each cell, if any. Can be a matrix of float (provided it is the same size as the grid), a int or float variable of the grid species, or simply true (in which case, the variable called 'grid_value' is used to compute the elevation of each cell)")),
	@facet(name = IKeyword.TEXTURE, type = { IType.BOOL, IType.FILE }, optional = true),
	@facet(name = IKeyword.TRIANGULATION, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.TEXT, type = IType.BOOL, optional = true),
	@facet(name = "draw_as_dem", type = IType.BOOL, optional = true, doc = @doc(value = "", deprecated = "use 'elevation' instead")),
	@facet(name = "dem", type = IType.MATRIX, optional = true, doc = @doc(value = "", deprecated = "use 'elevation' instead")),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.SPECIES)
public class GridLayerStatement extends AbstractLayerStatement {

	public GridLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(getFacet(IKeyword.SPECIES).literalValue());
	}

	IGrid grid;
	IExpression lineColor, elevation, textureExp, triExp, textExp;
	Boolean isTextured = true;
	GamaFile textureFile = null;
	Boolean isTriangulated = false;
	Boolean showText = false;
	GamaColor currentColor, constantColor;
	int cellSize;
	private IExpression setOfAgents;

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		lineColor = getFacet(IKeyword.LINES);
		if ( lineColor != null ) {
			constantColor = Cast.asColor(scope, lineColor.value(scope));
			currentColor = constantColor;
		}

		elevation = getFacet(IKeyword.ELEVATION);
		if ( elevation == null ) {
			elevation = getFacet("dem");
			if ( elevation == null ) {
				elevation = getFacet("draw_as_dem");
			}
		}

		textureExp = getFacet(IKeyword.TEXTURE);
		if ( textureExp != null ) {

			if ( textureExp.getType().equals(Types.get(IType.BOOL)) ) {
				isTextured = Cast.asBool(scope, textureExp.value(scope));
			}

			if ( textureExp.getType().equals(Types.get(IType.FILE)) ) {
				textureFile = (GamaFile) textureExp.value(scope);
			}

		}

		triExp = getFacet(IKeyword.TRIANGULATION);
		if ( triExp != null ) {
			isTriangulated = Cast.asBool(scope, triExp.value(scope));
		}

		textExp = getFacet(IKeyword.TEXT);
		if ( textExp != null ) {
			showText = Cast.asBool(scope, textExp.value(scope));
		}

		final IPopulation gridPop = scope.getAgentScope().getPopulationFor(getName());
		if ( gridPop == null ) {
			throw GamaRuntimeException.error("missing environment for output " + getName());
		} else if ( !gridPop.isGrid() ) { throw GamaRuntimeException.error("not a grid environment for: " + getName()); }

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

	public Color getLineColor() {
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
		return /* agentsForLayer; */grid.getAgents();
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
					if ( (Boolean) elevation.value(scope) ) { return grid.getGridValue(); }
			}
		}
		return null;
	}

	public Boolean isTextured() {
		return isTextured;
	}

	public GamaFile textureFile() {
		return textureFile;
	}

	public Boolean isTriangulated() {
		return isTriangulated;
	}

	public Boolean isShowText() {
		return showText;
	}

}
