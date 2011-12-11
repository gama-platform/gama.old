/**
 * Created by drogoul, 26 nov. 2011
 * 
 */
package msi.gama.util.graph;

import msi.gama.interfaces.IGeometry;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

public class _SpatialEdge extends _Edge<IGeometry> {

	public _SpatialEdge(final GamaSpatialGraph graph, final Object edge, final Object source,
		final Object target) throws GamaRuntimeException {
		super(graph, edge, source, target);
	}

	@Override
	protected void init(final Object edge, final Object source, final Object target)
		throws GamaRuntimeException {
		if ( !(edge instanceof IGeometry) ) {
			// storedObject = (IGeometry) edge;
			// } else {
			throw new GamaRuntimeException(Cast.toGaml(edge) + " is not a geometry");
		}
		super.init(edge, source, target);
	}

	@Override
	protected void buildSource(final Object edge, final Object source) {
		Object s = source;
		IGeometry g = (IGeometry) edge;
		if ( s == null ) {
			GamaPoint c1 = g.getGeometry().getPoints().get(0);
			s = findVertexWithCoordinates(c1);
		}
		super.buildSource(edge, s);
	}

	@Override
	protected void buildTarget(final Object edge, final Object target) {
		Object s = target;
		IGeometry g = (IGeometry) edge;
		if ( s == null ) {
			GamaPoint c1 = g.getGeometry().getPoints().last();
			s = findVertexWithCoordinates(c1);
		}
		super.buildTarget(edge, s);
	}

	private Object findVertexWithCoordinates(final GamaPoint c) {
		for ( Object vertex : graph.vertexSet() ) {
			// _SpatialVertex internal = getVertex(vertex);
			if ( vertex instanceof IGeometry && ((IGeometry) vertex).getLocation().equals(c) ) { return vertex; }
		}
		IGeometry vertex = new GamaPoint(c);
		graph.addVertex(vertex);
		return vertex;
	}

	@Override
	public double getWeight(final Object storedObject) {
		double w = super.getWeight(storedObject);
		if ( storedObject instanceof IGeometry ) {
			w *= ((IGeometry) storedObject).getInnerGeometry().getLength(); // A voir...
		}
		return w;
	}
}