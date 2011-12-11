/**
 * Created by drogoul, 26 nov. 2011
 * 
 */
package msi.gama.util.graph;

import msi.gama.interfaces.IGeometry;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.Cast;

public class _SpatialVertex extends _Vertex<IGeometry> {

	public _SpatialVertex(final GamaGraph graph, final Object vertex) throws GamaRuntimeException {
		super(graph);
		if ( !(vertex instanceof IGeometry) ) { throw new GamaRuntimeException(Cast.toGaml(vertex) +
			" is not a geometry"); }
	}

}