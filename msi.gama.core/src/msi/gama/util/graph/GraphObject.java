/*********************************************************************************************
 *
 * 'GraphObject.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph;

import org.jgrapht.WeightedGraph;

/**
 * Class GraphObject.
 * 
 * @author drogoul
 * @since 12 janv. 2014
 * 
 */
public abstract class GraphObject<T extends IGraph<V, E>, V, E> {

	protected final T graph;
	protected double weight = WeightedGraph.DEFAULT_EDGE_WEIGHT;

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
