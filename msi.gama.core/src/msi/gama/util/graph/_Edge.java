/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.graph;

import msi.gama.runtime.exceptions.GamaRuntimeException;
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