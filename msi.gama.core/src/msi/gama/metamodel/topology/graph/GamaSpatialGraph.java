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
package msi.gama.metamodel.topology.graph;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.*;
import msi.gaml.species.ISpecies;
import org.jgrapht.Graphs;
import org.jgrapht.alg.*;

public class GamaSpatialGraph extends GamaGraph<IShape, IShape> implements ISpatialGraph,
	IPopulationListener {

	private FloydWarshallStaticOptimizer pathFinder;
	/*
	 * Own topology of the graph. Lazily instantiated, and invalidated at each modification of the
	 * graph.
	 */
	private ITopology topology;
	private final boolean agentEdge;

	/**
	 * Determines the relationship among two polygons.
	 */
	public static interface VertexRelationship<T> {

		/**
		 * Determines if two vertex geometries are to be treated as related in any way.
		 * @param p1 a geometrical object
		 * @param p2 another geometrical object
		 */
		boolean related(T p1, T p2);

		boolean equivalent(T p1, T p2);

		// Double distance(T p1, T p2);

	}

	public GamaSpatialGraph(final IContainer edgesOrVertices, final boolean byEdge,
		final boolean directed, final VertexRelationship rel, final ISpecies edgesSpecies,
		final IScope scope) {
		super(edgesOrVertices, byEdge, directed, rel, edgesSpecies, scope);
		agentEdge =
			edgesSpecies != null || byEdge && edgesOrVertices != null &&
				edgesOrVertices.first(scope) instanceof IAgent;
		verbose = false;

	}

	public boolean isAgentEdge() {
		return agentEdge;
	}

	@Override
	public GamaSpatialGraph copy() {
		GamaSpatialGraph g =
			new GamaSpatialGraph(GamaList.EMPTY_LIST, true, directed, vertexRelation, edgeSpecies,
				scope);
		Graphs.addAllEdges(g, this, this.edgeSet());
		return g;
	}

	@Override
	public IPath computeShortestPathBetween(final Object source, final Object target) {
		return pathFromEdges(source, target, computeBestRouteBetween(source, target));
	}

	@Override
	public IList<IShape> computeBestRouteBetween(final Object source, final Object target) {
		switch (optimizerType) {
			case 1:
				if ( pathFinder == null ) {
					pathFinder = new FloydWarshallStaticOptimizer(this, verbose);
				}
				return new GamaList<IShape>(pathFinder.bestRouteBetween((ILocation) source,
					(ILocation) target));
			case 2:
				BellmanFordShortestPath p1 = new BellmanFordShortestPath(getProxyGraph(), source);
				return new GamaList<IShape>(p1.getPathEdgeList(target));
			case 3:
				DijkstraShortestPath<GamaShape, GamaShape> p2 =
					new DijkstraShortestPath(getProxyGraph(), source, target);
				return new GamaList<IShape>(p2.getPathEdgeList());
			case 4:
				AStarShortestPath p3 =
					new AStarShortestPath(this, (IShape) source, (IShape) target);
				return new GamaList<IShape>(p3.getPathEdgeList());
		}
		return new GamaList<IShape>();

	}

	@Override
	protected IPath pathFromEdges(final Object source, final Object target, final IList edges) {
		return new GamaPath(getTopology(), (IShape) source, (IShape) target, edges);
	}

	@Override
	protected void buildByVertices(final IContainer<?, IShape> list) {
		super.buildByVertices(list);
		for ( IShape o1 : list ) { // Try to create automatic edges
			if ( o1.getAgent() != null ) {
				o1.getAgent().setAttribute("attached_graph", this);
			}
			for ( IShape o2 : list ) {
				if ( o1 == o2 || vertexRelation.equivalent(o1, o2) ) {
					continue;
				}
				if ( vertexRelation.related(o1, o2) ) {
					addEdge(o1, o2, scope);
				}
			}
		}
	}

	@Override
	protected _SpatialEdge getEdge(final Object e) {
		return (_SpatialEdge) edgeMap.get(e);
	}

	@Override
	protected _SpatialVertex getVertex(final Object v) {
		return (_SpatialVertex) vertexMap.get(v);
	}

	@Override
	protected _SpatialEdge newEdge(final Object e, final Object v1, final Object v2)
		throws GamaRuntimeException {
		return new _SpatialEdge(this, e, v1, v2);
	}

	@Override
	protected _SpatialVertex newVertex(final Object v) throws GamaRuntimeException {
		return new _SpatialVertex(this, v);
	}

	public boolean addVertex(final IShape v) {
		boolean added = super.addVertex(v);
		if ( added && vertexRelation != null ) {
			for ( IShape o : vertexSet() ) {
				if ( vertexRelation.related(v, o) && !vertexRelation.equivalent(v, o) ) {
					addEdge(v, o);
				}
			}
		}
		return added;
	}

	/**
	 * @param gamaPath
	 * @return
	 */
	public double computeWeight(final IPath gamaPath) {
		double result = 0;
		List l = gamaPath.getEdgeList();
		for ( Object o : l ) {
			result += getEdgeWeight(o);
		}
		return result;
	}

	@Override
	public ITopology getTopology() {
		if ( topology == null ) {
			setTopology(new GraphTopology(this));
		}
		return topology;
	}

	@Override
	public void invalidateTopology() {
		// Nothing to do, actually, as the topology relies entirely on the graph to do computations
		// (I.e. no caches are being made)
	}

	protected void setTopology(final ITopology topology) {
		this.topology = topology;
	}

	@Override
	public IPath getCircuit() {
		return (IPath) super.getCircuit();
	}

	public void reInitPathFinder() {
		pathFinder = null;
	}

	@Override
	protected Object createNewEdgeObjectFromVertices(final Object v1, final Object v2) {
		if ( edgeSpecies == null && v1 instanceof IShape && v2 instanceof IShape ) { return new GamaDynamicLink(
			(IShape) v1, (IShape) v2); }
		Map<String, Object> map = new GamaMap();
		// IShape line = new GamaDynamicLink((IShape) v1, (IShape) v2);
		// GamaGeometryType.buildLine(((IShape) v1).getLocation(), ((IShape) v2).getLocation());
		IList initVal = new GamaList();
		map.put(IKeyword.SOURCE, v1);
		map.put(IKeyword.TARGET, v2);
		// map.put(IKeyword.SHAPE, line);
		initVal.add(map);
		return generateEdgeAgent(initVal);
	}

	@Override
	public void notifyAgentRemoved(final IPopulation pop, final IAgent agent) {
		this.removeVertex(agent);
	}

	@Override
	public void notifyAgentAdded(final IPopulation pop, final IAgent agent) {
		this.addVertex(agent);
	}

	@Override
	public void notifyAgentsAdded(final IPopulation pop, final Collection agents) {
		for ( Object o : agents ) {
			addVertex((IAgent) o);
		}
	}

	@Override
	public void notifyAgentsRemoved(final IPopulation pop, final Collection agents) {
		for ( Object o : agents ) {
			removeVertex(o);
		}
	}

	@Override
	public void notifyPopulationCleared(final IPopulation pop) {
		this.clear();
	}

}
