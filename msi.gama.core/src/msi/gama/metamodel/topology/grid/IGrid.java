/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.grid.IGrid.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ISpatialIndex;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.GamaSpatialPath;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;

/**
 * Interface IGrid.
 *
 * @author Alexis Drogoul
 * @since 13 mai 2013
 *
 */
public interface IGrid extends IMatrix<IShape>, ISpatialIndex, IDiffusionTarget {

	List<IAgent> getAgents();

	Boolean isHexagon();

	Boolean isHorizontalOrientation();

	void setCellSpecies(final IPopulation<? extends IAgent> pop);

	IAgent getAgentAt(final ILocation c);

	GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException;

	GamaSpatialPath computeShortestPathBetweenWeighted(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final Map<IAgent, Object> on) throws GamaRuntimeException;

	// public abstract Iterator<IAgent> getNeighborsOf(final IScope scope, final
	// ILocation shape, final Double
	// distance,
	// IAgentFilter filter);

	Set<IAgent> getNeighborsOf(final IScope scope, final IShape shape, final Double distance, IAgentFilter filter);

	int manhattanDistanceBetween(final IShape g1, final IShape g2);

	IShape getPlaceAt(final ILocation c);

	int[] getDisplayData();

	double[] getGridValue();

	/**
	 * Computes and returns a double array by applying the expression to each of the agents of the grid
	 *
	 * @param scope
	 *            the current scope
	 * @param expr
	 *            cannot be null
	 * @return a double array the size of the grid
	 */
	double[] getGridValueOf(IScope scope, IExpression expr);

	boolean isTorus();

	INeighborhood getNeighborhood();

	IShape getEnvironmentFrame();

	int getX(IShape geometry);

	int getY(IShape geometry);

	@Override
	void dispose();

	boolean usesIndiviualShapes();

	/**
	 * @return
	 */
	boolean usesNeighborsCache();

	String optimizer();

	/**
	 * @return
	 */
	ISpecies getCellSpecies();

}
