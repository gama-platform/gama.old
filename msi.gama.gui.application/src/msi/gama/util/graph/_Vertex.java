/**
 * Created by drogoul, 26 nov. 2011
 * 
 */
package msi.gama.util.graph;

import java.util.*;
import org.jgrapht.WeightedGraph;
import org.jgrapht.util.ArrayUnenforcedSet;

public class _Vertex<V> {

	/**
	 * The graph to which this vertex belongs
	 */
	protected final GamaGraph<?, V> graph;

	/**
	 * @param gamaGraph
	 */
	_Vertex(final GamaGraph<?, V> gamaGraph) {
		graph = gamaGraph;
	}

	Set inEdges = new ArrayUnenforcedSet(1);
	Set outEdges = new ArrayUnenforcedSet(1);
	Double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;

	public Double getWeight(final Object storedObject) {
		return weight;
	}

	public void setWeight(final Double w) {
		weight = w;
	}

	public void addOutEdge(final Object e) {
		outEdges.add(e);
	}

	public void removeInEdge(final Object e) {
		inEdges.remove(e);
	}

	public void removeOutEdge(final Object e) {
		outEdges.remove(e);
	}

	public void addInEdge(final Object e) {
		inEdges.add(e);
	}

	public Object edgeTo(final Object v2) {
		for ( Object e : outEdges ) {
			_Edge<V> edge = (_Edge<V>) graph.edgeMap.get(e);
			if ( edge.getTarget() == v2 ) { return e; }
		}
		return null;
	}

	public Set edgesTo(final Object v2) {
		Set result = new HashSet();
		for ( Object e : outEdges ) {
			_Edge<V> edge = (_Edge<V>) graph.edgeMap.get(e);
			if ( edge.getTarget() == v2 ) {
				result.add(e);
			}
		}
		return result;
	}

	public Set getEdges() {
		Set result = new HashSet(inEdges);
		result.addAll(outEdges);
		return result;
	}

}