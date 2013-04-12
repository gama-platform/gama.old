/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.graph;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph._Edge;
import com.vividsolutions.jts.geom.Coordinate;

public class _SpatialEdge extends _Edge<IShape> {

	public _SpatialEdge(final GamaSpatialGraph graph, final Object edge, final Object source,
		final Object target) throws GamaRuntimeException {
		super(graph, edge, source, target);
	}

	@Override
	protected void init(final Object edge, final Object source, final Object target)
		throws GamaRuntimeException {
		if ( !(edge instanceof IShape) ) { throw new GamaRuntimeException(StringUtils.toGaml(edge) +
			" is not a geometry"); }
		super.init(edge, source, target);
	}

	@Override
	protected void buildSource(final Object edge, final Object source) {
		Object s = source;
		IShape g = (IShape) edge;
		if ( s == null ) {
			Coordinate c1 = g.getGeometry().getInnerGeometry().getCoordinates()[0];
			s = findVertexWithCoordinates(c1);
		}
		super.buildSource(edge, s);
	}

	@Override
	protected void buildTarget(final Object edge, final Object target) {
		Object s = target;
		IShape g = (IShape) edge;
		if ( s == null ) {
			Coordinate[] points = g.getGeometry().getInnerGeometry().getCoordinates();
			Coordinate c1 = points[points.length - 1];
			s = findVertexWithCoordinates(c1);
		}
		super.buildTarget(edge, s);
	}

	private Object findVertexWithCoordinates(final Coordinate c) {
		System.out.println("graph : " + graph);
		IShape vertex = ((GamaSpatialGraph) graph).getBuiltVertex(c);
		if ( vertex != null ) { return vertex; }
		vertex = new GamaPoint(c);
		graph.addVertex(vertex); 
		((GamaSpatialGraph) graph).addBuiltVertex(vertex);
		return vertex;
	}

	@Override
	public double getWeight(final Object storedObject) {
		double w = super.getWeight(storedObject);
		if ( storedObject instanceof IShape ) {
			w *= ((IShape) storedObject).getInnerGeometry().getLength(); // A voir...
		}
		return w;
	}
}