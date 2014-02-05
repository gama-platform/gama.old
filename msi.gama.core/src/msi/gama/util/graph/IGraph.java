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
package msi.gama.util.graph;

import java.util.*;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;
import msi.gama.util.path.IPath;
import msi.gaml.types.IType;
import org.jgrapht.*;

/**
 * Written by drogoul
 * Modified on 24 nov. 2011
 * 
 * An interface for the different kinds of graphs encountered in GAML
 * 
 */
@vars({ @var(name = "spanning_tree", type = IType.LIST), @var(name = "circuit", type = IType.PATH),
	@var(name = "connected", type = IType.BOOL), @var(name = "edges", type = IType.LIST),
	@var(name = "vertices", type = IType.LIST) })
public interface IGraph<V, E> extends IContainer<V, E>, WeightedGraph<V, E>, DirectedGraph<V, E>,
	UndirectedGraph<V, E>, IGraphEventProvider {

	public abstract double getVertexWeight(final Object v);

	public abstract Double getWeightOf(final Object v);

	public abstract void setVertexWeight(final Object v, final double weight);

	void setWeights(Map<?, Double> weights);

	public Collection _internalEdgeSet();

	public Collection _internalNodesSet();

	public Map<E, _Edge<V>> _internalEdgeMap();

	public Map<V, _Vertex<E>> _internalVertexMap();

	@getter("edges")
	public abstract IList<E> getEdges();

	@getter("vertices")
	public abstract IList<V> getVertices();

	@getter("spanning_tree")
	public abstract IList<E> getSpanningTree();

	@getter("circuit")
	public abstract IPath<V, E, IGraph<V, E>> getCircuit();

	@getter("connected")
	public abstract Boolean getConnected();

	public abstract boolean isDirected();

	public abstract void setDirected(final boolean b);

	public abstract Object addEdge(Object p);

	public abstract void setOptimizerType(String optiType);

	public int getVersion();

	public void setVersion(int version);

	public void incVersion();

	// FIXME Patrick: To check
	// public abstract IPath<V,E> computeShortestPathBetween(final Object source, final Object target);
	// public abstract IList<IShape> computeBestRouteBetween(final Object source, final Object target);

	public abstract IPath<V, E, IGraph<V, E>> computeShortestPathBetween(final V source, final V target);

	public abstract IList<E> computeBestRouteBetween(final V source, final V target);

	public double computeWeight(final IPath<V, E, ? extends IGraph<V, E>> gamaPath);

	public double computeTotalWeight();
	
	public boolean isSaveComputedShortestPaths() ;

	public void setSaveComputedShortestPaths(boolean saveComputedShortestPaths) ;

	public abstract List<IPath<V, E, IGraph<V, E>>> computeKShortestPathsBetween(V source, V target, int k);
	
	public abstract IList<IList<E>> computeKBestRoutesBetween(final V source, final V target,int k);

}