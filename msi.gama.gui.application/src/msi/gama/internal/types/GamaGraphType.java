/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.types;

import java.util.Map;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.*;
import msi.gama.util.graph.*;
import org.jgrapht.Graphs;

@type(value = IType.GRAPH_STR, id = IType.GRAPH, wraps = { IGraph.class })
public class GamaGraphType extends GamaType<IGraph> {

	@Override
	public IGraph cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		// param = true : spatial.
		if ( obj instanceof IGraph ) { return (IGraph) obj; }
		boolean spatial = param != null && Cast.asBool(param);
		if ( obj == null ) { return null; }
		if ( obj instanceof GamaList ) { return from(scope, (GamaList) obj, spatial);
		// List of agents, geometries...
		}
		if ( obj instanceof GamaMap ) { return from(scope, (GamaMap) obj, spatial); }
		// TODO Matrix, Pair ?
		return null;
	}

	public static IGraph from(final IScope scope, final GamaMap obj, final boolean spatial) {
		GamaGraph result =
			spatial ? new GamaSpatialGraph(scope, new GamaList(), false, false, null)
				: new GamaGraph(scope, new GamaList(), false, false);
		GamaPair p = new GamaPair(null, null);
		for ( Map.Entry k : obj.entrySet() ) {
			p.key = k.getKey();
			p.value = k.getValue();
			result.addEdge(p);
		}
		return result;
	}

	public static IGraph from(final IScope scope, final GamaList obj, final boolean spatial) {
		return spatial ? new GamaSpatialGraph(scope, obj, false, false, null) : new GamaGraph(
			scope, obj, false, false);
	}

	@Override
	public IGraph getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(NONE);
	}

	public static IGraph asDirectedGraph(final IGraph source) {
		source.setDirected(true);
		return source; // TODO Clone ?
	}

	public static IGraph asUndirectedGraph(final IGraph source) {
		source.setDirected(false);
		return source; // TODO Clone ?
	}

	public static GamaSpatialGraph asSpatialGraph(final IScope scope, final GamaGraph source) {
		if ( source instanceof GamaSpatialGraph ) { return (GamaSpatialGraph) source; }
		GamaSpatialGraph destination =
			new GamaSpatialGraph(scope, GamaList.EMPTY_LIST, true, false, null);
		Graphs.addGraph(destination, source);
		return destination;
	}

	public static IGraph asRegularGraph(final IScope scope, final GamaGraph source) {
		if ( source.getClass() == GamaGraph.class ) { return source; }
		GamaGraph destination = new GamaGraph(scope, GamaList.EMPTY_LIST, true, false);
		Graphs.addGraph(destination, source);
		return destination;
	}

}
