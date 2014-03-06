/**
 * Created by drogoul, 12 janv. 2014
 * 
 */
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
