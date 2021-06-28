/*******************************************************************************************************
 *
 * msi.gama.util.graph._Edge.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;


import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class _Edge<V, E> extends GraphObject<GamaGraph<V, E>, V, E> {

	/**
	 * 
	 */
	// protected final GamaGraph<V, ?> graph;
	// private double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;
	private Object source, target;

	public _Edge(final GamaGraph<V, E> gamaGraph, final Object edge, final Object source, final Object target)
			throws GamaRuntimeException {
		this(gamaGraph, edge, source, target, DefaultDirectedWeightedGraph.DEFAULT_EDGE_WEIGHT);
	}

	public _Edge(final GamaGraph<V, E> gamaGraph, final Object edge, final Object source, final Object target,
			final double weight) throws GamaRuntimeException {
		super(gamaGraph, weight);
		init(graph.getScope(), edge, source, target);
	}

	protected void init(final IScope scope, final Object edge, final Object source, final Object target)
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
		_Vertex<V,E> s = graph.getVertex(source);
		if (s != null)s.removeOutEdge(edge);
		_Vertex<V,E> t = graph.getVertex(target);
		if (t != null)t.removeInEdge(edge);
	}

	@Override
	public double getWeight() {
		// Syst�matique ??
		// Double na = graph.getVertexWeight(source);
		// Double nb = graph.getVertexWeight(target);
		return weight;// * (na + nb) / 2;
	}

	public Object getSource() {
		return source;
	}

	public Object getOther(final Object extremity) {
		return extremity == source ? target : source;
	}

	public Object getTarget() {
		return target;
	}

	@Override
	public String toString() {
		return new StringBuffer().append(source.toString()).append(" -> ").append(target.toString()).toString();
	}

	@Override
	public boolean isEdge() {
		return true;
	}
}