/*******************************************************************************************************
 *
 * LayoutCircle.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.layout;

import java.util.Collections;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Spatial;

/**
 * The Class LayoutCircle.
 */
public class LayoutCircle {

	/** The graph. */
	private final IGraph<IShape, IShape> graph;

	/** The envelope geometry. */
	private final IShape envelopeGeometry;

	/**
	 * Instantiates a new layout circle.
	 *
	 * @param graph the graph
	 * @param envelopeGeometry the envelope geometry
	 */
	public LayoutCircle(final IGraph<IShape, IShape> graph, final IShape envelopeGeometry) {
		this.graph = graph;
		this.envelopeGeometry = envelopeGeometry;
	}

	/**
	 * Apply layout.
	 *
	 * @param scope the scope
	 * @param shuffle the shuffle
	 */
	public void applyLayout(final IScope scope, final boolean shuffle) {

		final double radius = envelopeGeometry.getCentroid().euclidianDistanceTo(Spatial.Punctal
				._closest_point_to(envelopeGeometry.getCentroid(), envelopeGeometry.getExteriorRing(scope)));

		// Optimize node ordering
		final List<IShape> orderedNodes = this.minimizeEdgeLength(graph, shuffle);

		int i = 0;
		for (final IShape v : orderedNodes) {
			final double angle = 360 * i++ / (double) graph.vertexSet().size();
			final double x = Maths.cos(angle) * radius + envelopeGeometry.getCentroid().x;
			final double y = Maths.sin(angle) * radius + envelopeGeometry.getCentroid().x;
			v.setLocation(new GamaPoint(x, y));
		}

	}

	/**
	 * Minimize edge length.
	 *
	 * @param graph the graph
	 * @param shuffle the shuffle
	 * @return the list
	 */
	private List<IShape> minimizeEdgeLength(final IGraph<IShape, IShape> graph, final boolean shuffle) {
		/*
		 * List<IShape> orderedNode = graph.vertexSet().stream().sorted((v1,v2) -> graph.degreeOf(v1) <
		 * graph.degreeOf(v2) ? 1 : (v1.getAgent().getIndex() < v2.getAgent().getIndex() ? -1 : 1))
		 * .toList();
		 */

		// Not find a simple to implement algorithm

		final List<IShape> nodes = graph.getVertices();
		if (shuffle) {
			Collections.shuffle(nodes);
		}
		return nodes;
	}

}
