/*******************************************************************************************************
 *
 * IGrid.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
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

	/**
	 * Gets the agents.
	 *
	 * @return the agents
	 */
	IList<IAgent> getAgents();

	/**
	 * Checks if is hexagon.
	 *
	 * @return the boolean
	 */
	Boolean isHexagon();

	/**
	 * Checks if is horizontal orientation.
	 *
	 * @return the boolean
	 */
	Boolean isHorizontalOrientation();

	/**
	 * Sets the cell species.
	 *
	 * @param pop
	 *            the new cell species
	 */
	void setCellSpecies(final IPopulation<? extends IAgent> pop);

	/**
	 * Gets the agent at.
	 *
	 * @param c
	 *            the c
	 * @return the agent at
	 */
	IAgent getAgentAt(final GamaPoint c);

	/**
	 * Compute shortest path between.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param topo
	 *            the topo
	 * @param on
	 *            the on
	 * @return the gama spatial path
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException;

	/**
	 * Compute shortest path between weighted.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param topo
	 *            the topo
	 * @param on
	 *            the on
	 * @return the gama spatial path
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	GamaSpatialPath computeShortestPathBetweenWeighted(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final Map<IAgent, Object> on) throws GamaRuntimeException;

	// public abstract Iterator<IAgent> getNeighborsOf(final IScope scope, final
	// GamaPoint shape, final Double
	// distance,
	// IAgentFilter filter);

	/**
	 * Gets the neighbors of.
	 *
	 * @param scope
	 *            the scope
	 * @param shape
	 *            the shape
	 * @param distance
	 *            the distance
	 * @param filter
	 *            the filter
	 * @return the neighbors of
	 */
	Set<IAgent> getNeighborsOf(final IScope scope, final IShape shape, final Double distance, IAgentFilter filter);

	/**
	 * Manhattan distance between.
	 *
	 * @param g1
	 *            the g 1
	 * @param g2
	 *            the g 2
	 * @return the int
	 */
	int manhattanDistanceBetween(final IShape g1, final IShape g2);

	/**
	 * Gets the place at.
	 *
	 * @param c
	 *            the c
	 * @return the place at
	 */
	IShape getPlaceAt(final GamaPoint c);

	/**
	 * Gets the display data.
	 *
	 * @return the display data
	 */
	int[] getDisplayData();

	/**
	 * Gets the grid value.
	 *
	 * @return the grid value
	 */
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

	/**
	 * Checks if is torus.
	 *
	 * @return true, if is torus
	 */
	boolean isTorus();

	/**
	 * Gets the neighborhood.
	 *
	 * @return the neighborhood
	 */
	INeighborhood getNeighborhood();

	/**
	 * Gets the environment frame.
	 *
	 * @return the environment frame
	 */
	IShape getEnvironmentFrame();

	/**
	 * Gets the x.
	 *
	 * @param geometry
	 *            the geometry
	 * @return the x
	 */
	int getX(IShape geometry);

	/**
	 * Gets the y.
	 *
	 * @param geometry
	 *            the geometry
	 * @return the y
	 */
	int getY(IShape geometry);

	/**
	 * Dispose.
	 */
	@Override
	void dispose();

	/**
	 * Uses indiviual shapes.
	 *
	 * @return true, if successful
	 */
	boolean usesIndiviualShapes();

	/**
	 * @return
	 */
	boolean usesNeighborsCache();

	/**
	 * Optimizer.
	 *
	 * @return the string
	 */
	String optimizer();

	/**
	 * @return
	 */
	ISpecies getCellSpecies();

}
