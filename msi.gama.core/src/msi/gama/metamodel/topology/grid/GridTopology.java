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

import java.util.Iterator;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaGridFile;
import msi.gama.util.path.GamaSpatialPath;

public class GridTopology extends AbstractTopology {

	public GridTopology(final IScope scope, final IGrid matrix) {
		super(scope, matrix.getEnvironmentFrame(), null);
		places = matrix;
	}

	@Override
	public void updateAgent(final IShape previous, final IShape agent) {

	}

	@Override
	public void initialize(final IPopulation pop) throws GamaRuntimeException {
		getPlaces().setCellSpecies(pop);
		((ISpatialIndex.Compound) getSpatialIndex()).add(getPlaces(), pop.getSpecies());
		super.initialize(pop);
		// if ( getPlaces().getGridValue() != null && !getPlaces().getGridValue().isEmpty() ) {
		// for ( final IAgent ag : pop ) {
		// ag.setAttribute("grid_value", getPlaces().getGridValue(ag));
		// }
		// getPlaces().clearGridValue();
		// }
	}

	@Override
	protected boolean canCreateAgents() {
		return true;
	}

	public GridTopology(final IScope scope, final IShape environment, final int rows, final int columns,
		final boolean isTorus, final boolean usesVN, final boolean isHexagon, final boolean useIndividualShapes)
		throws GamaRuntimeException {
		super(scope, environment, null);
		if ( isHexagon ) {
			places =
				new GamaSpatialMatrix(scope, environment, rows, columns, isTorus, usesVN, isHexagon,
					useIndividualShapes);
		} else {
			places = new GamaSpatialMatrix(scope, environment, rows, columns, isTorus, usesVN, useIndividualShapes);
		}
		// FIXME Not sure it needs to be set
		// root.setTorus(isTorus);

	}

	public GridTopology(final IScope scope, final IShape environment, final GamaGridFile file, final boolean isTorus,
		final boolean usesVN, final boolean useIndividualShapes) throws GamaRuntimeException {
		super(scope, environment, null);
		places = new GamaSpatialMatrix(scope, file, isTorus, usesVN, useIndividualShapes);
		// FIXME Not sure it needs to be set

		// root.setTorus(isTorus);
	}

	@Override
	public IAgent getAgentClosestTo(final IShape source, final IAgentFilter filter) {
		// We first grab the cell at the location closest to the centroid of the source
		final IAgent place = getPlaces().getAgentAt(source.getLocation());
		// If the filter accepts it, we return it
		if ( filter.accept(source, place) ) { return place; }
		// Otherwise we get the "normal" closest agent (in the spatial index)
		return super.getAgentClosestTo(source, filter);
	}

	@Override
	public IAgent getAgentClosestTo(final ILocation source, final IAgentFilter filter) {
		// We first grab the cell at the location closest to the centroid of the source
		final IAgent place = getPlaces().getAgentAt(source);
		// If the filter accepts it, we return it
		if ( filter.accept(source, place) ) { return place; }
		// Otherwise we get the "normal" closest agent (in the spatial index)
		return super.getAgentClosestTo(source, filter);
	}

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
	protected String _toGaml() {
		return IKeyword.TOPOLOGY + " (" + places.toGaml() + ")";
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.AbstractTopology#_copy()
	 */
	@Override
	protected ITopology _copy(final IScope scope) throws GamaRuntimeException {
		final IGrid grid = (IGrid) places;
		return new GridTopology(scope, environment, grid.getRows(scope), grid.getCols(scope), grid.isTorus(), grid
			.getNeighbourhood().isVN(), grid.isHexagon(), grid.usesIndiviualShapes());
	}

	@Override
	public IGrid getPlaces() {
		return (IGrid) super.getPlaces();
	}

	/**
	 * @throws GamaRuntimeException
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this);
	}

	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target)
		throws GamaRuntimeException {
		return getPlaces().computeShortestPathBetween(scope, source, target, this);
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final ILocation p) {
		return getPlaces().getPlaceAt(p) != null;

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
	public Double distanceBetween(final IScope scope, final IShape source, final IShape target) {
		if ( !isValidGeometry(source) || !isValidGeometry(target) ) { return Double.MAX_VALUE; }
		// TODO null or Double.MAX_VALUE ?
		return (double) getPlaces().manhattanDistanceBetween(source, target);
	}

	@Override
	public Double distanceBetween(final IScope scope, final ILocation source, final ILocation target) {
		if ( !isValidLocation(source) || !isValidLocation(target) ) { return Double.MAX_VALUE; }
		// TODO null or Double.MAX_VALUE ?
		return (double) getPlaces().manhattanDistanceBetween(source, target);
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IScope scope, final IShape source, final IShape target) {
		// TODO compute from the path
		return null;
	}

	@Override
	public Iterator<IAgent> getNeighboursOf(final IShape source, final Double distance, final IAgentFilter filter)
		throws GamaRuntimeException {
		// GuiUtils.debug("GridTopology.getNeighboursOf");
		return getPlaces().getNeighboursOf(scope, this, source, distance); // AgentFilter ?
	}

	@Override
	public Iterator<IAgent> getNeighboursOf(final ILocation source, final Double distance, final IAgentFilter filter)
		throws GamaRuntimeException {
		// GuiUtils.debug("GridTopology.getNeighboursOf");
		return getPlaces().getNeighboursOf(scope, this, source, distance); // AgentFilter ?
	}

	@Override
	public void dispose() {
		super.dispose();
		getPlaces().dispose();
	}

}
