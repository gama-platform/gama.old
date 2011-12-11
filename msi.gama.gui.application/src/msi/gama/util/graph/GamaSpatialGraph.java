/**
 * Created by drogoul, 26 nov. 2011
 * 
 */
package msi.gama.util.graph;

import java.util.List;
import msi.gama.environment.ITopology;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import org.jgrapht.Graphs;

public class GamaSpatialGraph extends GamaGraph<IGeometry, IGeometry> {

	/**
	 * Determines the relationship among two polygons.
	 */
	public static interface VertexRelationship {

		/**
		 * Determines if two vertex geometries are to be treated as related in any way.
		 * @param p1 a geometrical object
		 * @param p2 another geometrical object
		 */
		boolean related(IScope scope, IGeometry p1, IGeometry p2);

		boolean equivalent(IGeometry p1, IGeometry p2);

	}

	protected VertexRelationship vertexRelation;

	public GamaSpatialGraph(final IScope scope, final IGamaContainer vertices,
		final boolean byEdge, final boolean directed, final VertexRelationship rel) {
		super(scope, vertices, byEdge, directed);
		vertexRelation = rel;
	}

	@Override
	protected Object createNewEdgeObjectFromVertices(final Object v1, final Object v2) {
		if ( v1 instanceof IGeometry && v2 instanceof IGeometry ) { return new GamaDynamicLink(
			(IGeometry) v1, (IGeometry) v2); }
		return super.createNewEdgeObjectFromVertices(v1, v2);
	}

	@Override
	public GamaSpatialGraph copy() {
		GamaSpatialGraph g =
			new GamaSpatialGraph(scope, GamaList.EMPTY_LIST, true, directed, vertexRelation);
		Graphs.addAllEdges(g, this, this.edgeSet());
		return g;
	}

	@Override
	public GamaPath computeShortestPathBetween(final ITopology topology, final Object source,
		final Object target) {
		return (GamaPath) super.computeShortestPathBetween(topology, source, target);
	}

	@Override
	protected GamaPath pathFromEdges(final ITopology topology, final Object source,
		final Object target, final List edges) {
		return new GamaPath(topology, (IGeometry) source, (IGeometry) target, edges);
	}

	@Override
	protected void buildByVertices(final IGamaContainer<?, IGeometry> list) {
		super.buildByVertices(list);
		for ( IGeometry o1 : list ) { // Try to create automatic edges
			for ( IGeometry o2 : list ) {
				if ( o1 == o2 || vertexRelation.equivalent(o1, o2) ) {
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

	public boolean addVertex(final IGeometry v) {
		boolean added = super.addVertex(v);
		if ( added && vertexRelation != null ) {
			for ( IGeometry o : vertexSet() ) {
				if ( vertexRelation.related(scope, v, o) && !vertexRelation.equivalent(v, o) ) {
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
	public double computeWeight(final GamaPath gamaPath) {
		double result = 0;
		List l = gamaPath.getEdgeList();
		for ( Object o : l ) {
			result += getEdgeWeight(o);
		}
		return result;
	}

}
