/*******************************************************************************************************
 *
 * GamlResourceGraph.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.indexer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphSpecificsStrategy;
import org.jgrapht.graph.DefaultGraphType;

import com.google.common.collect.Iterables;
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
	class Imports extends AbstractBaseGraph<URI, LabeledEdge> {

		/**
		 * Instantiates a new graph.
		 */
		public Imports() {
			super(null, null,
					new DefaultGraphType.Builder().directed().allowMultipleEdges(false).allowSelfLoops(false)
							.weighted(false).allowCycles(true).build(),
					new DefaultGraphSpecificsStrategy<URI, LabeledEdge>());
		}

	}

	/** The regular imports. */
	Imports imports = new Imports();

	/**
	 * Reset.
	 */
	void reset() {
		imports = new Imports();
	}

	/**
	 * The Class Edge.
	 */
	class LabeledEdge {

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
		if (!imports.containsVertex(uri)) return Collections.EMPTY_SET;
		Set<URI> successors = null;
		Iterable<LabeledEdge> incoming = imports.iterables().incomingEdgesOf(uri);
		if (Iterables.isEmpty(incoming)) return Collections.EMPTY_SET;
		successors = new HashSet<>();
		for (LabeledEdge e : incoming) { successors.add(Graphs.getOppositeVertex(imports, e, uri)); }
		return successors;
	}

	/**
	 * Successors of.
	 *
	 * @param newURI
	 *            the new URI
	 * @return the sets the
	 */
	public Set<URI> successorsOf(final URI uri) {
		if (!imports.containsVertex(uri)) return Collections.EMPTY_SET;
		Set<URI> successors = null;
		Iterable<LabeledEdge> outgoing = imports.iterables().outgoingEdgesOf(uri);
		if (Iterables.isEmpty(outgoing)) return Collections.EMPTY_SET;
		successors = new HashSet<>();
		for (LabeledEdge e : outgoing) { successors.add(Graphs.getOppositeVertex(imports, e, uri)); }
		return successors;
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
	 * Removes the all edges.
	 *
	 * @param edges
	 *            the edges
	 */
	public void removeAllEdges(final URI source, final Map<URI, String> edges) {
		if (edges.isEmpty()) return;
		edges.forEach((uri, v) -> { imports.removeEdge(source, uri); });
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
