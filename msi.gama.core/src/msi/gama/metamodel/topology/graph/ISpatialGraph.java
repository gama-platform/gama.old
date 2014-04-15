/*********************************************************************************************
 * 
 *
 * 'ISpatialGraph.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.metamodel.topology.graph;

import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;

/**
 * The class ISpatialGraph.
 * 
 * @author drogoul
 * @since 3 f�vr. 2012
 * 
 */
public interface ISpatialGraph extends IGraph<IShape, IShape> {

	public abstract ITopology getTopology(IScope scope);

	public abstract void invalidateTopology();

	@Override
	public abstract IList<IShape> getVertices();

	@Override
	public abstract IList<IShape> getEdges();

}
