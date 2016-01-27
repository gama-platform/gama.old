/*********************************************************************************************
 * 
 * 
 * 'IGrid.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
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
public interface IGrid extends IMatrix<IShape>, ISpatialIndex {

	public static final short DIFFUSION = 0;
	public static final short GRADIENT = 1;

	public abstract List<IAgent> getAgents();

	public abstract Boolean isHexagon();

	public abstract void setCellSpecies(final IPopulation pop);

	public abstract void diffuseVariable_deprecated(final IScope scope, final String name, final double value, final short type,
		final double prop, final double variation, final ILocation location, final double range, Object candidates);
	// this was once used for "Signal" statement (deprecated since GAMA 1.8). It will have to be removed soon.
	public abstract void diffuseVariable(final IScope scope, boolean method_diffu, boolean is_gradient, double[][] mat_diffu, 
			double[][] mask, String var_diffu, IPopulation pop);

	public abstract IAgent getAgentAt(final ILocation c);

	public abstract GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source,
		final IShape target, final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException;

	// public abstract Iterator<IAgent> getNeighboursOf(final IScope scope, final ILocation shape, final Double
	// distance,
	// IAgentFilter filter);

	public abstract Set<IAgent> getNeighboursOf(final IScope scope, final IShape shape, final Double distance,
		IAgentFilter filter);

	public abstract int manhattanDistanceBetween(final IShape g1, final IShape g2);

	public abstract IShape getPlaceAt(final ILocation c);

	public abstract int[] getDisplayData();

	public abstract double[] getGridValue();

	/**
	 * Computes and returns a double array by applying the expression to each of the agents of the grid
	 * @param scope the current scope
	 * @param expr cannot be null
	 * @return a double array the size of the grid
	 */
	public abstract double[] getGridValueOf(IScope scope, IExpression expr);

	public abstract boolean isTorus();

	public abstract INeighbourhood getNeighbourhood();

	public abstract IShape getEnvironmentFrame();

	public abstract int getX(IShape geometry);

	public abstract int getY(IShape geometry);

	public abstract void dispose();

	public abstract boolean usesIndiviualShapes();

	/**
	 * @return
	 */
	public abstract boolean usesNeighboursCache();

	/**
	 * @return
	 */
	public abstract ISpecies getCellSpecies();

}
