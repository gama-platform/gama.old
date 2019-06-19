/*******************************************************************************************************
 *
 * msi.gama.util.graph.GraphUtilsPrefuse.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package prefuse.util;

import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.IGraph;
import msi.gama.util.graph._Edge;
import prefuse.data.Graph;
import prefuse.data.Node;

@SuppressWarnings ({ "rawtypes" })
public class GraphUtilsPrefuse {

	public static final String PREFUSE_ATTRIBUTE_GAMA_OBJECT = "go";

	public static Graph getPrefuseGraphFromGamaGraphForVisu(final IScope scope, final GamaGraph<?, ?> graph) {

		System.err.println("translation of the graph to a prefuse graph...");

		final Graph g = new Graph();

		g.addColumn(PREFUSE_ATTRIBUTE_GAMA_OBJECT, IShape.class);

		final Map<Object, Node> gamaVertex2prefuseNode = new HashMap<>(graph._internalVertexMap().size());

		// retrieve nodes
		for (final Object content : graph._internalVertexMap().keySet()) {
			if (content instanceof IShape) {
				final IShape shContent = (IShape) content;

				final Node prefuseNode = g.addNode();
				prefuseNode.set(PREFUSE_ATTRIBUTE_GAMA_OBJECT, shContent);

				gamaVertex2prefuseNode.put(content, prefuseNode);

			} else {
				System.err.println("Warning, not using " + content);
			}
		}

		// retrieve edges
		for (final Object o : graph._internalEdgeSet()) {

			final _Edge<?, ?> edge = (_Edge) o;

			g.addEdge(gamaVertex2prefuseNode.get(edge.getSource()), gamaVertex2prefuseNode.get(edge.getTarget()));

		}

		// basic verification
		if (graph._internalVertexMap().size() != g.getNodeCount()) {
			throw GamaRuntimeException.error(
					"error during the translation of a Gama graph to a prefuse graph: the number of nodes is not the same.",
					scope);
		}

		return g;

	}

	public static Graph getPrefuseGraphFromGamaGraph(final IScope scope, final IGraph<?, ?> graph) {
		return getPrefuseGraphFromGamaGraphForVisu(scope, (GamaGraph) graph);

	}
}
