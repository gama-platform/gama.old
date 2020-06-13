/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.graph._SpatialVertex.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.graph;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph._Vertex;

public class _SpatialVertex extends _Vertex<IShape, IShape> {

	public _SpatialVertex(final GamaSpatialGraph graph, final Object vertex) throws GamaRuntimeException {
		super(graph);
		if (!(vertex instanceof IShape)) { throw GamaRuntimeException
				.error(StringUtils.toGaml(vertex, false) + " is not a geometry", graph.getScope()); }
	}

}