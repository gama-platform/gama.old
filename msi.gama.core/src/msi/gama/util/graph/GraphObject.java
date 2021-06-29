/*******************************************************************************************************
 *
 * msi.gama.util.graph.GraphObject.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

/**
 * Class GraphObject.
 * 
 * @author drogoul
 * @since 12 janv. 2014
 * 
 */
public abstract class GraphObject<T extends IGraph<V, E>, V, E> {

	protected final T graph;
	protected double weight = DefaultDirectedWeightedGraph.DEFAULT_EDGE_WEIGHT;

	GraphObject(final T g, final double w) {
		graph = g;
		weight = w;
	}

	public void setWeight(final double w) {
		weight = w;
	}

	public abstract double getWeight();

	public boolean isNode() {
		return false;
	}

	public boolean isEdge() {
		return false;
	}
}
