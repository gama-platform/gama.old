/**
 * Created by drogoul, 13 mai 2013
 * 
 */
package msi.gama.metamodel.topology.grid;

import java.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.GamaSpatialPath;

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

	public abstract void diffuseVariable(final IScope scope, final String name, final double value, final short type,
		final double prop, final double variation, final ILocation location, final double range);

	public abstract IAgent getAgentAt(final ILocation c);

	public abstract GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source,
		final IShape target, final ITopology topo) throws GamaRuntimeException;

	public abstract Iterator<IAgent> getNeighboursOf(final IScope scope, final ITopology t, final ILocation shape,
		final Double distance);

	public abstract Iterator<IAgent> getNeighboursOf(final IScope scope, final ITopology t, final IShape shape,
		final Double distance);

	public abstract int manhattanDistanceBetween(final IShape g1, final IShape g2);

	public abstract IShape getPlaceAt(final ILocation c);

	public abstract int[] getDisplayData();
	
	public abstract double[] getGridValue();

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

}
