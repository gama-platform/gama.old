/**
 * Created by drogoul, 3 févr. 2012
 * 
 */
package msi.gama.metamodel.topology.graph;

import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;

/**
 * The class ISpatialGraph.
 * 
 * @author drogoul
 * @since 3 févr. 2012
 * 
 */
public interface ISpatialGraph extends IGraph<IShape, IShape> {

	public abstract ITopology getTopology();

	public abstract void invalidateTopology();

	@Override
	public abstract IList<IShape> getVertices();

	@Override
	public abstract IList<IShape> getEdges();

	@Override
	public abstract IPath computeShortestPathBetween(final Object source, final Object target);
	
	public abstract IList<IShape> computeBestRouteBetween(final Object source, final Object target);	
	
	@Override
	public abstract IPath getCircuit();
}
