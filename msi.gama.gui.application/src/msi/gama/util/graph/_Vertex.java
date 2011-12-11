/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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