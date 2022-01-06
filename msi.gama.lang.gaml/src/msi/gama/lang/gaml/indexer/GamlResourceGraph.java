/*******************************************************************************************************
 *
 * GamlResourceGraph.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.indexer;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.emptyIterator;
import static org.jgrapht.Graphs.predecessorListOf;
import static org.jgrapht.Graphs.successorListOf;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphSpecificsStrategy;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.google.common.collect.Maps;

import msi.gama.util.GamaMapFactory;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamlResourceGraph.
 */
public class GamlResourceGraph {

	static {
		DEBUG.ON();
	}

	/**
	 * The Class Graph.
	 */
	static class Graph extends AbstractBaseGraph<URI, LabeledEdge> {

		/**
		 * Instantiates a new graph.
		 */
		public Graph() {
			super(null, null,
					new DefaultGraphType.Builder().directed().allowMultipleEdges(false).allowSelfLoops(false)
							.weighted(false).allowCycles(true).build(),
					new DefaultGraphSpecificsStrategy<URI, LabeledEdge>());
		}

	}

	/** The regular imports. */
	Graph imports = new Graph();

	/**
	 * Reset.
	 */
	void reset() {
		imports = new Graph();
	}

	/**
	 * The Class Edge.
	 */
	static class LabeledEdge {

		/** The label. */
		String label;

		/** The target. */
		final URI target;

		/**
		 * Instantiates a new edge.
		 *
		 * @param l
		 *            the l
		 * @param target
		 *            the target
		 */
		LabeledEdge(final String l, final URI target) {
			this.label = l;
			this.target = target;
		}

	}

	/**
	 * Contains vertex.
	 *
	 * @param uri
	 *            the uri
	 * @return true, if successful
	 */
	public boolean containsVertex(final URI uri) {
		return imports.containsVertex(uri);
	}

	/**
	 * Predecessors of.
	 *
	 * @param newURI
	 *            the new URI
	 * @return the sets the
	 */
	public Set<URI> predecessorsOf(final URI uri) {
		return newHashSet(imports.containsVertex(uri) ? predecessorListOf(imports, uri) : EMPTY_LIST);
	}

	/**
	 * Successors of.
	 *
	 * @param newURI
	 *            the new URI
	 * @return the sets the
	 */
	public Set<URI> successorsOf(final URI uri) {
		return newHashSet(imports.containsVertex(uri) ? successorListOf(imports, uri) : EMPTY_LIST);
	}

	/**
	 * Adds the edge.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param label
	 *            the label
	 */
	public void addEdge(final URI from, final URI to, final String label) {
		imports.addVertex(from);
		imports.addVertex(to);
		imports.removeEdge(from, to);
		imports.addEdge(from, to, new LabeledEdge(label, to));
	}

	/**
	 * Breadth first iterator without.
	 *
	 * @param uri
	 *            the uri
	 * @return the iterator
	 */
	public Iterator<URI> breadthFirstIteratorWithout(final URI uri) {
		Iterator<URI> regular =
				imports.containsVertex(uri) ? new BreadthFirstIterator<>(imports, uri) : emptyIterator();
		// to eliminate the uri
		if (regular.hasNext()) { regular.next(); }
		return regular;
	}

	/**
	 * Removes the all edges.
	 *
	 * @param edges
	 *            the edges
	 */
	public void removeAllEdges(final URI source, final Map<URI, String> edges) {
		for (URI uri : edges.keySet()) { imports.removeAllEdges(source, uri); }
		// TransitiveReduction.INSTANCE.reduce(regularImports);
	}

	/**
	 * Outgoing edges of.
	 *
	 * @param uri
	 *            the uri
	 * @return the map
	 */
	public Map<URI, String> outgoingEdgesOf(final URI uri) {
		if (!containsVertex(uri)) return Collections.EMPTY_MAP;
		Map<URI, String> result = GamaMapFactory.createOrdered();
		if (imports.containsVertex(uri)) {
			for (LabeledEdge o : imports.outgoingEdgesOf(uri)) { result.put(o.target, o.label); }
		}
		return result;
	}

	/**
	 * Sorted imports of.
	 *
	 * @param uri
	 *            the uri
	 * @return the map
	 */
	@SuppressWarnings ("null")
	public Map<URI, String> sortedDepthFirstSearchWithLabels(final URI uri) {
		Map<URI, String> result = Maps.newLinkedHashMap();
		searchImports(uri, null, result);
		result.remove(uri);
		return result;
	}

	/**
	 * All imports of. A simple depth first visit that keeps track of the labels
	 *
	 * @param uri
	 *            the uri
	 * @param currentLabel
	 *            the current label
	 * @param result
	 *            the result
	 */
	private void searchImports(final URI uri, final String currentLabel, final Map<URI, String> result) {
		if (!result.containsKey(uri)) {
			result.put(uri, currentLabel);
			if (imports.containsVertex(uri)) {
				for (LabeledEdge edge : imports.outgoingEdgesOf(uri)) {
					searchImports(edge.target, edge.label == null ? currentLabel : edge.label, result);
				}
			}
		} else {
			// if already there we re-insert it to keep the last occurence
			result.remove(uri);
			result.put(uri, currentLabel);
		}

	}
}
