/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.graph._SpatialEdge.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.graph;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph._Edge;

public class _SpatialEdge extends _Edge<IShape, IShape> {

	public _SpatialEdge(final GamaSpatialGraph graph, final Object edge, final Object source, final Object target)
			throws GamaRuntimeException {
		super(graph, edge, source, target);
	}

	@Override
	protected void init(final IScope scope, final Object edge, final Object source, final Object target)
			throws GamaRuntimeException {
		if (!(edge instanceof IShape)) { throw GamaRuntimeException
				.error(StringUtils.toGaml(edge, false) + " is not a geometry", scope); }
		super.init(scope, edge, source, target);
	}

	@Override
	protected void buildSource(final Object edge, final Object source) {
		Object s = source;
		final IShape g = (IShape) edge;
		if (s == null) {
			final Coordinate c1 = g.getGeometry().getInnerGeometry().getCoordinates()[0];
			s = findVertexWithCoordinates(c1);
		}
		super.buildSource(edge, s);
	}

	@Override
	protected void buildTarget(final Object edge, final Object target) {
		Object s = target;
		final IShape g = (IShape) edge;
		if (s == null) {
			final Coordinate[] points = g.getGeometry().getInnerGeometry().getCoordinates();
			final Coordinate c1 = points[points.length - 1];
			s = findVertexWithCoordinates(c1);
		}
		super.buildTarget(edge, s);
	}

	private Object findVertexWithCoordinates(final Coordinate c) {
		IShape vertex = ((GamaSpatialGraph) graph).getBuiltVertex(c);
		if (vertex != null) { return vertex; }
		vertex = new GamaPoint(c);
		graph.addVertex(vertex);
		((GamaSpatialGraph) graph).addBuiltVertex(vertex);
		return vertex;
	}

}