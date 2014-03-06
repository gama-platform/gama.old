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

import msi.gama.metamodel.topology.graph.FloydWarshallShortestPathsGAMA;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.util.*;
import msi.gama.util.path.IPath;
import msi.gaml.statements.AbstractContainerStatement.GraphObjectToAdd;
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
public interface IGraph<Node, Edge> extends IModifiableContainer<Node, Edge, GamaPair<Node, Node>, GraphObjectToAdd>,
	IAddressableContainer<Node, Edge, GamaPair<Node, Node>, List<Edge>>, WeightedGraph<Node, Edge>, DirectedGraph<Node, Edge>,
	UndirectedGraph<Node, Edge>, IGraphEventProvider {

	public abstract double getVertexWeight(final Object v);

	public abstract Double getWeightOf(final Object v);

	public abstract void setVertexWeight(final Object v, final double weight);

	void setWeights(Map<?, Double> weights);

	public Collection _internalEdgeSet();

	public Collection _internalNodesSet();

	public Map<Edge, _Edge<Node, Edge>> _internalEdgeMap();

	public Map<Node, _Vertex<Node, Edge>> _internalVertexMap();

	@getter("edges")
	public abstract IList<Edge> getEdges();

	@getter("vertices")
	public abstract IList<Node> getVertices();

	@getter("spanning_tree")
	public abstract IList<Edge> getSpanningTree();

	@getter("circuit")
	public abstract IPath<Node, Edge, IGraph<Node, Edge>> getCircuit();

	@getter("connected")
	public abstract Boolean getConnected();

	public abstract boolean isDirected();

	public abstract void setDirected(final boolean b);

	public abstract Object addEdge(Object p);

	public abstract void setOptimizerType(String optiType);
	
	public FloydWarshallShortestPathsGAMA<Node, Edge> getOptimizer();

	public void setOptimizer(FloydWarshallShortestPathsGAMA<Node, Edge> optimizer);

	public int getVersion();

	public void setVersion(int version);

	public void incVersion();

	// FIXME Patrick: To check
	// public abstract IPath<V,E> computeShortestPathBetween(final Object source, final Object target);
	// public abstract IList<IShape> computeBestRouteBetween(final Object source, final Object target);

	public abstract IPath<Node, Edge, IGraph<Node, Edge>> computeShortestPathBetween(final Node source, final Node target);

	public abstract IList<Edge> computeBestRouteBetween(final Node source, final Node target);

	public double computeWeight(final IPath<Node, Edge, ? extends IGraph<Node, Edge>> gamaPath);

	public double computeTotalWeight();
	
	public boolean isSaveComputedShortestPaths() ;

	public void setSaveComputedShortestPaths(boolean saveComputedShortestPaths) ;

	public abstract List<IPath<Node, Edge, IGraph<Node, Edge>>> computeKShortestPathsBetween(Node source, Node target, int k);
	
	public abstract IList<IList<Edge>> computeKBestRoutesBetween(final Node source, final Node target,int k);

}