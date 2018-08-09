/*********************************************************************************************
 *
 * 'GraphUtilsGraphStream.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;

/**
 * Graph utilities for the use of the graphstream library.
 * 
 * @author Samuel Thiriot
 * 
 */
@SuppressWarnings ({ "rawtypes" })
public class GraphUtilsGraphStream {

	/**
	 * Preprocess a gama object before exportation. Filters gama objects that have no meaning out of gama; notably GAMA
	 * colors are translated to RGB values.
	 * 
	 * @param gamaValue
	 * @return
	 */
	public static Object preprocessGamaValue(final Object gamaValue) {

		if (gamaValue instanceof GamaColor) {
			// colors can't remain as GAMA colors; let's encode them as RGB java
			final GamaColor gamaColor = (GamaColor) gamaValue;
			return gamaColor.getRGB();
		}

		return gamaValue;
	}

	/**
	 * Takes a gama graph as an input, returns a graphstream graph as close as possible. Preserves double links (multi
	 * graph).
	 * 
	 * @param gamaGraph
	 * @return
	 */
	public static Graph getGraphstreamGraphFromGamaGraph(final IGraph gamaGraph) {

		final Graph g = new MultiGraph("tmpGraph", true, false);

		final Map<Object, Node> gamaNode2graphStreamNode =
				new HashMap<>(gamaGraph._internalNodesSet().size());

		// add nodes
		for (final Object v : gamaGraph._internalVertexMap().keySet()) {
			// final _Vertex vertex = (_Vertex) gamaGraph._internalVertexMap().get(v);

			final Node n = g.addNode(v.toString());

			gamaNode2graphStreamNode.put(v, n);

			if (v instanceof IAgent) {
				final IAgent a = (IAgent) v;
				for (final Object key : a.getAttributes().keySet()) {

					final Object value = preprocessGamaValue(a.getAttributes().get(key));

					// standard attribute
					n.setAttribute(key.toString(), value.toString());

				}
			}

			if (v instanceof IShape) {
				final IShape sh = (IShape) v;

				n.setAttribute("x", sh.getLocation().getX());
				n.setAttribute("y", sh.getLocation().getY());
				n.setAttribute("z", sh.getLocation().getZ());

			}

		}

		// add edges
		for (final Object edgeObj : gamaGraph._internalEdgeMap().keySet()) {
			final _Edge edge = (_Edge) gamaGraph._internalEdgeMap().get(edgeObj);

			try {
				final Edge e = g.addEdge(edgeObj.toString(), gamaNode2graphStreamNode.get(edge.getSource()),
						gamaNode2graphStreamNode.get(edge.getTarget()), gamaGraph.isDirected() // till
																								// now,
																								// directionality
																								// of
																								// an
																								// edge
																								// depends
																								// on
																								// the
																								// whole
																								// gama
																								// graph
				);
				if (edgeObj instanceof IAgent) {
					final IAgent a = (IAgent) edgeObj;
					for (final Object key : a.getAttributes().keySet()) {
						final Object value = preprocessGamaValue(a.getAttributes().get(key));
						e.setAttribute(key.toString(), value.toString());
					}
				}
			} catch (final EdgeRejectedException e) {
				GAMA.reportError(GAMA.getRuntimeScope(),
						GamaRuntimeException.warning(
								"an edge was rejected during the transformation, probably because it was a double one",
								GAMA.getRuntimeScope()),
						true);
			} catch (final IdAlreadyInUseException e) {
				GAMA.reportError(GAMA.getRuntimeScope(),
						GamaRuntimeException.warning(
								"an edge was rejected during the transformation, probably because it was a double one",
								GAMA.getRuntimeScope()),
						true);
			}

		}

		// some basic tests for integrity
		if (gamaGraph.getVertices().size() != g.getNodeCount()) {
			GAMA.reportError(GAMA.getRuntimeScope(),
					GamaRuntimeException.warning("The exportation ran without error, but an integrity test failed: "
							+ "the number of vertices is not correct(" + g.getNodeCount() + " instead of "
							+ gamaGraph.getVertices().size() + ")", GAMA.getRuntimeScope()),
					true);
		}
		if (gamaGraph.getEdges().size() != g.getEdgeCount()) {
			GAMA.reportError(GAMA.getRuntimeScope(),
					GamaRuntimeException.warning("The exportation ran without error, but an integrity test failed: "
							+ "the number of edges is not correct(" + g.getEdgeCount() + " instead of "
							+ gamaGraph.getEdges().size() + ")", GAMA.getRuntimeScope()),
					true);
		}

		return g;
	}
}
