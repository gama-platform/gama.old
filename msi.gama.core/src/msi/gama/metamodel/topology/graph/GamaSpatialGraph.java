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

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.*;
import msi.gaml.compilation.ScheduledAction;
import msi.gaml.species.ISpecies;
import org.jgrapht.Graphs;

import com.vividsolutions.jts.geom.Coordinate;

public class GamaSpatialGraph extends GamaGraph<IShape, IShape> implements ISpatialGraph,
	IPopulation.Listener {

	/*
	 * Own topology of the graph. Lazily instantiated, and invalidated at each modification of the
	 * graph.
	 */
	private ITopology topology;
	private  GamaMap<Integer, IShape> verticesBuilt ; // only used for optimization purpose of spatial graph
	// building.


	/**
	 * Determines the relationship among two polygons.
	 */
	public static interface VertexRelationship<T> {

		/**
		 * @param scope TODO
		 *            Determines if two vertex geometries are to be treated as related in any way.
		 * @param p1 a geometrical object
		 * @param p2 another geometrical object
		 */
		boolean related(IScope scope, T p1, T p2);

		boolean equivalent(IScope scope, T p1, T p2);

		// Double distance(T p1, T p2);

	} 

	public GamaSpatialGraph(final IContainer edgesOrVertices, final boolean byEdge,
		final boolean directed, final VertexRelationship rel, final ISpecies edgesSpecies,
		final IScope scope) {
		super();
		verticesBuilt  = new GamaMap<Integer, IShape>();
		init(edgesOrVertices, byEdge, directed,rel, edgesSpecies, scope);
		
	} 

	@Override
	public GamaSpatialGraph copy(IScope scope) {
		GamaSpatialGraph g =
			new GamaSpatialGraph(GamaList.EMPTY_LIST, true, directed, vertexRelation, edgeSpecies,
				scope);
		Graphs.addAllEdges(g, this, this.edgeSet());
		return g;
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
				if ( vertexRelation.equivalent(scope, o1, o2) ) {
					continue;
				}
				if ( vertexRelation.related(scope, o1, o2) ) {
					addEdge(o1, o2);
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
				if ( !vertexRelation.equivalent(scope, v, o) && vertexRelation.related(scope, v, o) ) {
					addEdge(v, o);
				}
			}
		}
		return added;
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

	private void refreshEdges() {
		Set<? extends IShape> vSet = vertexSet();
		boolean related, already;
		for ( IShape s1 : vSet ) {
			for ( IShape s2 : vSet ) {
				if ( vertexRelation.equivalent(scope, s1, s2) ) {
					continue;
				}
				already = this.containsEdge(s1, s2);
				if ( (related = vertexRelation.related(scope, s1, s2)) && !already ) {
					addEdge(s1, s2);
				} else if ( already && !related ) {
					removeEdge(s1, s2);

				}
			}
		}
	}

	@Override
	protected Object generateEdgeObject(final Object v1, final Object v2) {
		if ( v1 instanceof IShape && v2 instanceof IShape ) { return new GamaDynamicLink(
			(IShape) v1, (IShape) v2); }
		return super.generateEdgeObject(v1, v2);
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
		removeAllVertices(vertexSet());
	}

	public void postRefreshManagementAction(final IScope scope) {
		scope.getWorldScope().getScheduler().insertEndAction(new ScheduledAction() {

			@Override
			public void execute(final IScope scope) throws GamaRuntimeException {
				GamaSpatialGraph.this.refreshEdges();
			}
		});
	}
	
	@Override
	public Set<IShape> vertexSet() {
		return vertexMap.keySet();
	}
	
	public void addBuiltVertex(final IShape vertex) {
		verticesBuilt.put(vertex.getLocation().hashCode(), vertex);
	}

	
	public IShape getBuiltVertex(final Coordinate vertex) {
		return verticesBuilt.get(vertex.hashCode());
	}


}
