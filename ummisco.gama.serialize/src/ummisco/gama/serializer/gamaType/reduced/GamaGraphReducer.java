/*********************************************************************************************
 *
 * 'GamaGraphReducer.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.types.GamaGraphType;
import msi.gaml.types.GamaPairType;
import msi.gaml.types.IType;

@SuppressWarnings({ "rawtypes" })
public class GamaGraphReducer {
	private final IType nodeTypeGraphReducer;
	private final IType edgeTypeGraphReducer;

	private final GamaMap valuesGraphReducer;
	private final GamaMap edgesWeightsGraphReducer;
	private final boolean spatial;
	private final boolean directed;

	public GamaGraphReducer(final IScope scope, final GamaGraph g) {
		spatial = g instanceof GamaSpatialGraph;
		directed = g.isDirected();
		
		nodeTypeGraphReducer = g.getGamlType().getKeyType();
		edgeTypeGraphReducer = g.getGamlType().getContentType();

		// Map of keys = pair(source,target), values = edge
		valuesGraphReducer = g.mapValue(scope, nodeTypeGraphReducer, edgeTypeGraphReducer, false);
		
		edgesWeightsGraphReducer = new GamaMap<>(valuesGraphReducer.capacity(), edgeTypeGraphReducer, new GamaPairType());
		for (final Object edge : valuesGraphReducer.values()) {
			// edgesWeightsGraphReducer.put(k.getKey(), new EdgeReducer(k.getValue(), g.getWeightOf(k.getValue())));
			edgesWeightsGraphReducer.put(edge, g.getWeightOf(edge));
		}

	}

	public GamaGraph constructObject(final IScope scope) {
//		GamaGraph graph = (GamaGraph) GamaGraphType.from(scope, valuesGraphReducer, spatial);
		
		GamaGraph graph = (GamaGraph) GamaGraphType.from(scope, valuesGraphReducer.getValues(), 
				true, directed, spatial, nodeTypeGraphReducer,
				edgeTypeGraphReducer) ;
		
		graph.setWeights(edgesWeightsGraphReducer);
		//for (final Object el : edgesWeightsGraphReducer.entrySet()) {
		//	Map.Entry entry = (Map.Entry) el;
		//	graph.setEdgeWeight(e, weight);
		//}
		return graph;
	}
}

/*class EdgeReducer {
	private Object edge;
	private double weight;
	
	public EdgeReducer(Object _o, double _w) {
		edge = _o;
		weight = _w;
	}
}*/
