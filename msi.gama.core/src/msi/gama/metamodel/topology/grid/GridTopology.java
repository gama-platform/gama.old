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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.grid;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.path.GamaSpatialPath;

public class GridTopology extends AbstractTopology {

	public GridTopology(final IScope scope, final GamaSpatialMatrix matrix) {
		super(scope, matrix.environmentFrame, null);
		places = matrix;
	}

	// @Override
	// public void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
	// final ILocation previousLoc, final Envelope previousEnv) {
	// // TODO grid agents should not be added to the spatial index. However, it may break some
	// // algorithms.
	// // super.updateAgent(agent, previousShapeIsPoint, previousLoc, previousEnv);
	// }

	@Override
	public void updateAgent(final IShape previous, final IShape agent) {

	}

	@Override
	public void initialize(final IPopulation pop) throws GamaRuntimeException {
		getPlaces().setCellSpecies(pop);
		((ISpatialIndex.Compound) getSpatialIndex()).add(getPlaces(), pop.getSpecies());
		super.initialize(pop);
		if ( getPlaces().getGridValue() != null && !getPlaces().getGridValue().isEmpty() ) {
			for ( IAgent ag : pop ) {
				ag.setAttribute("grid_value", getPlaces().getGridValue(ag));
			}
			getPlaces().clearGridValue();
		}
	}

	@Override
	protected boolean canCreateAgents() {
		return true;
	}

	public GridTopology(final IScope scope, final IShape environment, final int rows, final int columns,
		final boolean isTorus, final boolean usesVN, final boolean isHexagon) throws GamaRuntimeException {
		super(scope, environment, null);
		if ( isHexagon ) {
			places = new GamaSpatialMatrix(scope, environment, rows, columns, isTorus, usesVN, isHexagon);
		} else {
			places = new GamaSpatialMatrix(scope, environment, rows, columns, isTorus, usesVN);
		}
		// FIXME Not sure it needs to be set
		// root.setTorus(isTorus);

	}

	public GridTopology(final IScope scope, final IShape environment, final GamaGridFile file, final boolean isTorus,
		final boolean usesVN) throws GamaRuntimeException {
		super(scope, environment, null);
		places = new GamaSpatialMatrix(scope, file, isTorus, usesVN);
		// FIXME Not sure it needs to be set

		// root.setTorus(isTorus);
	}

	@Override
	public IAgent getAgentClosestTo(final IShape source, final IAgentFilter filter) {
		// We first grab the cell at the location closest to the centroid of the source
		IAgent place = getPlaces().getAgentAt(source.getLocation());
		// If the filter accepts it, we return it
		if ( filter.accept(source, place) ) { return place; }
		// Otherwise we get the "normal" closest agent (in the spatial index)
		return super.getAgentClosestTo(source, filter);
	}

	@Override
	public IAgent getAgentClosestTo(final ILocation source, final IAgentFilter filter) {
		// We first grab the cell at the location closest to the centroid of the source
		IAgent place = getPlaces().getAgentAt(source);
		// If the filter accepts it, we return it
		if ( filter.accept(source, place) ) { return place; }
		// Otherwise we get the "normal" closest agent (in the spatial index)
		return super.getAgentClosestTo(source, filter);
	}

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "Grid topology in " + environment.toString() + " as " + places.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml() {
		return IKeyword.TOPOLOGY + " (" + places.toGaml() + ")";
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(IScope scope) throws GamaRuntimeException {
		return new GridTopology(scope, environment, ((GamaSpatialMatrix) places).getRows(scope),
			((GamaSpatialMatrix) places).getCols(scope), ((GamaSpatialMatrix) places).isTorus,
			((GamaSpatialMatrix) places).neighbourhood.isVN(), ((GamaSpatialMatrix) places).isHexagon);
	}

	@Override
	public GamaSpatialMatrix getPlaces() {
		return (GamaSpatialMatrix) super.getPlaces();
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(IScope scope, final IShape source, final IShape target)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this);
	}

	@Override
	public GamaSpatialPath pathBetween(IScope scope, final ILocation source, final ILocation target)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final ILocation p) {
		return ((GamaSpatialMatrix) places).getPlaceAt(p) != null;

	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IShape g) {
		return isValidLocation(g.getLocation());
	}

	/**
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry,
	 *      java.lang.Double)
	 */
	@Override
	public Double distanceBetween(IScope scope, final IShape source, final IShape target) {
		if ( !isValidGeometry(source) || !isValidGeometry(target) ) { return Double.MAX_VALUE; }
		// TODO null or Double.MAX_VALUE ?
		return (double) getPlaces().manhattanDistanceBetween(source, target);
	}

	@Override
	public Double distanceBetween(IScope scope, final ILocation source, final ILocation target) {
		if ( !isValidLocation(source) || !isValidLocation(target) ) { return Double.MAX_VALUE; }
		// TODO null or Double.MAX_VALUE ?
		return (double) getPlaces().manhattanDistanceBetween(source, target);
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(IScope scope, final IShape source, final IShape target) {
		// TODO compute from the path
		return null;
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsInEnvelope(msi.gama.interfaces.IGeometry,
	 *      com.vividsolutions.jts.geom.Envelope, msi.gama.environment.IAgentFilter, boolean)
	 */
	// @Override
	// public IList<IAgent> getAgentsIn(final IShape source, final IAgentFilter f,
	// final boolean covered) {
	// List<IAgent> agents = super.getAgentsIn(source, f, covered);
	// GamaList<IAgent> result = new GamaList();
	// for ( IAgent ag : agents ) {
	// if ( !ag.dead() && isValidGeometry(ag) ) {
	// result.add(ag);
	// }
	// }
	// // We then add the corresponding cells (if they are accepted by the filter).
	// result.addAll(getPlaces().getAgentsCoveredBy(source, f, covered));
	// return result;
	// }

	@Override
	public GamaList<IAgent> getNeighboursOf(final IShape source, final Double distance, final IAgentFilter filter)
		throws GamaRuntimeException {
		return getPlaces().getNeighboursOf(scope, this, source, distance); // AgentFilter ?
	}

	@Override
	public GamaList<IAgent> getNeighboursOf(final ILocation source, final Double distance, final IAgentFilter filter)
		throws GamaRuntimeException {
		return getPlaces().getNeighboursOf(scope, this, source, distance); // AgentFilter ?
	}

}
