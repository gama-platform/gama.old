/*******************************************************************************************************
 *
 * _Edge.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;


import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class _Edge.
 *
 * @param <V> the value type
 * @param <E> the element type
 */
public class _Edge<V, E> extends GraphObject<GamaGraph<V, E>, V, E> {

	/**
	 * 
	 */
	// protected final GamaGraph<V, ?> graph;
	// private double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;
	private Object source, target;

	/**
	 * Instantiates a new edge.
	 *
	 * @param gamaGraph the gama graph
	 * @param edge the edge
	 * @param source the source
	 * @param target the target
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public _Edge(final GamaGraph<V, E> gamaGraph, final Object edge, final Object source, final Object target)
			throws GamaRuntimeException {
		this(gamaGraph, edge, source, target, DefaultDirectedWeightedGraph.DEFAULT_EDGE_WEIGHT);
	}

	/**
	 * Instantiates a new edge.
	 *
	 * @param gamaGraph the gama graph
	 * @param edge the edge
	 * @param source the source
	 * @param target the target
	 * @param weight the weight
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public _Edge(final GamaGraph<V, E> gamaGraph, final Object edge, final Object source, final Object target,
			final double weight) throws GamaRuntimeException {
		super(gamaGraph, weight);
		init(graph.getScope(), edge, source, target);
	}

	/**
	 * Inits the.
	 *
	 * @param scope the scope
	 * @param edge the edge
	 * @param source the source
	 * @param target the target
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	protected void init(final IScope scope, final Object edge, final Object source, final Object target)
			throws GamaRuntimeException {
		buildSource(edge, source);
		buildTarget(edge, target);
	}

	/**
	 * Builds the source.
	 *
	 * @param edge the edge
	 * @param source the source
	 */
	protected void buildSource(final Object edge, final Object source) {
		this.source = source;
		graph.getVertex(source).addOutEdge(edge);
	}

	/**
	 * Builds the target.
	 *
	 * @param edge the edge
	 * @param target the target
	 */
	protected void buildTarget(final Object edge, final Object target) {
		this.target = target;
		graph.getVertex(target).addInEdge(edge);
	}

	/**
	 * Removes the from vertices as.
	 *
	 * @param edge the edge
	 */
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

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Gets the other.
	 *
	 * @param extremity the extremity
	 * @return the other
	 */
	public Object getOther(final Object extremity) {
		return extremity == source ? target : source;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
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