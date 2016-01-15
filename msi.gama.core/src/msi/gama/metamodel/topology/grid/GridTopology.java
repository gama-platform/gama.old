/*********************************************************************************************
 * 
 * 
 * 'GridTopology.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.*;

import org.eclipse.core.runtime.dynamichelpers.IFilter;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.Different;
import msi.gama.metamodel.topology.filter.DifferentList;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.path.GamaSpatialPath;
import msi.gaml.types.*;

public class GridTopology extends AbstractTopology {

	public GridTopology(final IScope scope, final IGrid matrix) {
		super(scope, matrix.getEnvironmentFrame(), null);
		places = matrix;
	}

	@Override
	public void updateAgent(final IShape previous, final IAgent agent) {

	}

	@Override
	public void initialize(final IScope scope, final IPopulation pop) throws GamaRuntimeException {
		getPlaces().setCellSpecies(pop);
		((ISpatialIndex.Compound) getSpatialIndex()).add(getPlaces(), pop.getSpecies());
		super.initialize(scope, pop);
	}

	@Override
	protected boolean canCreateAgents() {
		return true;
	}

	public GridTopology(final IScope scope, final IShape environment, final int rows, final int columns,
		final boolean isTorus, final boolean usesVN, final boolean isHexagon, final boolean useIndividualShapes,
		final boolean useNeighboursCache) throws GamaRuntimeException {
		super(scope, environment, null);
		if ( isHexagon ) {
			places =
				new GamaSpatialMatrix(scope, environment, rows, columns, isTorus, usesVN, isHexagon,
					useIndividualShapes, useNeighboursCache);
		} else {
			places =
				new GamaSpatialMatrix(scope, environment, rows, columns, isTorus, usesVN, useIndividualShapes,
					useNeighboursCache);
		}
		// FIXME Not sure it needs to be set
		// root.setTorus(isTorus);

	}

	public GridTopology(final IScope scope, final IShape environment, final GamaGridFile file, final boolean isTorus,
		final boolean usesVN, final boolean useIndividualShapes, final boolean useNeighboursCache)
		throws GamaRuntimeException {
		super(scope, environment, null);
		places = new GamaSpatialMatrix(scope, file, isTorus, usesVN, useIndividualShapes, useNeighboursCache);
		// FIXME Not sure it needs to be set

		// root.setTorus(isTorus);
	}

	@Override
	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		return ((GamaSpatialMatrix) getPlaces()).getAgentClosestTo(scope, source,filter) ;

		
	}

	// @Override
	// public IAgent getAgentClosestTo(final IScope scope, final ILocation source, final IAgentFilter filter) {
	// // We first grab the cell at the location closest to the centroid of the source
	// final IAgent place = getPlaces().getAgentAt(source);
	// // If the filter accepts it, we return it
	// if ( filter.accept(scope, source, place) ) { return place; }
	// // Otherwise we get the "normal" closest agent (in the spatial index)
	// return super.getAgentClosestTo(scope, source, filter);
	// }

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "Grid topology in " + environment.toString() + " as " + places.toString();
	}

	/**
	 * @see msi.gama.environment.AbstractTopology#_toGaml()
	 */
	@Override
	protected String _toGaml(final boolean includingBuiltIn) {
		return IKeyword.TOPOLOGY + " (" + places.serialize(includingBuiltIn) + ")";
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(final IScope scope) throws GamaRuntimeException {
		final IGrid grid = (IGrid) places;
		return new GridTopology(scope, environment, grid.getRows(scope), grid.getCols(scope), grid.isTorus(), grid
			.getNeighbourhood().isVN(), grid.isHexagon(), grid.usesIndiviualShapes(), grid.usesNeighboursCache());
	}

	@Override
	public IGrid getPlaces() {
		return (IGrid) super.getPlaces();
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target,final IList<IAgent> on)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this, on);
	}

	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target,final IList<IAgent> on)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this, on);
	}
	
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this, null);
	}

	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this, null);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final IScope scope, final ILocation p) {
		return getPlaces().getPlaceAt(p) != null;

	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IScope scope, final IShape g) {
		return isValidLocation(scope, g.getLocation());
	}

	/**
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry, java.lang.Double)
	 */
	@Override
	public Double distanceBetween(final IScope scope, final IShape source, final IShape target) {
		if ( !isValidGeometry(scope, source) || !isValidGeometry(scope, target) ) { return Double.MAX_VALUE; }
		// TODO null or Double.MAX_VALUE ?
		return (double) getPlaces().manhattanDistanceBetween(source, target);
	}

	@Override
	public Double distanceBetween(final IScope scope, final ILocation source, final ILocation target) {
		if ( !isValidLocation(scope, source) || !isValidLocation(scope, target) ) { return Double.MAX_VALUE; }
		// TODO null or Double.MAX_VALUE ?
		return (double) getPlaces().manhattanDistanceBetween(source, target);
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IScope scope, final IShape source, final IShape target) {
		// TODO compute from the path
		return root.directionInDegreesTo(scope, source, target);
	}

	@Override
	public Collection<IAgent> getNeighboursOf(final IScope scope, final IShape source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		// We compute the neighbouring cells of the "source" shape
		
		Set<IAgent> placesConcerned = getPlaces().getNeighboursOf(scope, source, distance, getPlaces().getCellSpecies());
		// If we only accept cells from this topology, no need to look for other agents
		if ( filter.getSpecies() == getPlaces().getCellSpecies() ) { return placesConcerned; }
		// Otherwise, we return all the agents that intersect the geometry formed by the shapes of the cells (incl. the
		// cells themselves) and that are accepted by the filter 
		boolean normalFilter = filter.getSpecies() != null || !(filter instanceof Different);
		IAgentFilter fDL = normalFilter ? filter : (new DifferentList(getPlaces().getCellSpecies().listValue(scope, Types.NO_TYPE, false)));
		Set<IAgent> agents = (Set<IAgent>) getAgentsIn(scope,
			GamaGeometryType.geometriesToGeometry(scope, GamaListFactory.createWithoutCasting(Types.AGENT, placesConcerned)), fDL,
			false); 
		if(! normalFilter) {agents.addAll(placesConcerned);}
		return agents;
		

	}

	// @Override
	// public Iterator<IAgent> getNeighboursOf(final ILocation source, final Double distance, final IAgentFilter filter)
	// throws GamaRuntimeException {
	// // We compute the neighbouring cells of the "source" location
	// Iterator<IAgent> placesConcerned = getPlaces().getNeighboursOf(scope, source, distance, filter);
	// // If we only accept cells from this topology, no need to look for other agents
	// if ( filter.filterSpecies(getPlaces().getCellSpecies()) ) { return placesConcerned; }
	// // Otherwise, we return all the agents that intersect the geometry formed by the shapes of the cells (incl. the
	// // cells themselves) and that are accepted by the filter
	// return getAgentsIn(GamaGeometryType.geometriesToGeometry(scope, new GamaList(placesConcerned)), filter, false);
	// }

	@Override
	public void dispose() {
		// GuiUtils.debug("GridTopology.dispose");
		super.dispose();
		getPlaces().dispose();
	}

	@Override
	public List<GamaSpatialPath>
		KpathsBetween(final IScope scope, final IShape source, final IShape target, final int k) {
		// TODO for the moment, returns only 1 shortest path.... need to fix it!
		return super.KpathsBetween(scope, source, target, k);
	}

	@Override
	public List<GamaSpatialPath> KpathsBetween(final IScope scope, final ILocation source, final ILocation target,
		final int k) {
		// TODO for the moment, returns only 1 shortest path.... need to fix it!
		return super.KpathsBetween(scope, source, target, k);
	}

}
