/**
 * Created by drogoul, 26 nov. 2011
 * 
 */
package msi.gama.util.graph;

import msi.gama.kernel.exceptions.GamaRuntimeException;
import org.jgrapht.WeightedGraph;

public class _Edge<V> {

	/**
	 * 
	 */
	protected final GamaGraph<V, ?> graph;
	private double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;
	private Object source, target;

	public _Edge(final GamaGraph<V, ?> gamaGraph, final Object edge, final Object source,
		final Object target) throws GamaRuntimeException {
		graph = gamaGraph;
		init(edge, source, target);
	}

	protected void init(final Object edge, final Object source, final Object target)
		throws GamaRuntimeException {
		buildSource(edge, source);
		buildTarget(edge, target);
	}

	protected void buildSource(final Object edge, final Object source) {
		this.source = source;
		graph.getVertex(source).addOutEdge(edge);
	}

	protected void buildTarget(final Object edge, final Object target) {
		this.target = target;
		graph.getVertex(target).addInEdge(edge);
	}

	public void removeFromVerticesAs(final Object edge) {
		graph.getVertex(source).removeOutEdge(edge);
		graph.getVertex(target).removeInEdge(edge);
	}

	public void setWeight(final double w) {
		weight = w;
	}

	public double getWeight(final Object storedObject) {
		// Systématique ??
		Double na = graph.getVertexWeight(source);
		Double nb = graph.getVertexWeight(target);
		// if ( na == null || nb == null ) { return weight; }
		return weight * (na + nb) / 2;
	}

	public Object getSource() {
		return source;
	}

	public Object getTarget() {
		return target;
	}
}