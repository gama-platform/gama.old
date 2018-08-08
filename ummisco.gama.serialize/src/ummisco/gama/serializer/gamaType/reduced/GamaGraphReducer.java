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

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IReference;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.types.GamaGraphType;
import msi.gaml.types.GamaPairType;
import msi.gaml.types.IType;
import ummisco.gama.serializer.gamaType.reference.ReferenceGraph;

@SuppressWarnings({ "rawtypes" })
public class GamaGraphReducer {
	private final IType nodeTypeGraphReducer;
	private final IType edgeTypeGraphReducer;

	//private GamaMap valuesGraphReducer;
	private GamaList edgesGraphReducer;
	private GamaMap edgesWeightsGraphReducer;
	private final boolean spatial;
	private final boolean directed;

	public GamaGraphReducer(final IScope scope, final GamaGraph g) {
		spatial = g instanceof GamaSpatialGraph;
		directed = g.isDirected();
		
		nodeTypeGraphReducer = g.getGamlType().getKeyType();
		edgeTypeGraphReducer = g.getGamlType().getContentType();

		// Map of keys = pair(source,target), values = edge
		// valuesGraphReducer = g.mapValue(scope, nodeTypeGraphReducer, edgeTypeGraphReducer, false);
		edgesGraphReducer = (GamaList) GamaListFactory.create(scope, edgeTypeGraphReducer, new ArrayList(g.edgeSet()));

		
//		edgesWeightsGraphReducer = new GamaMap<>(valuesGraphReducer.capacity(), edgeTypeGraphReducer, new GamaPairType());
		edgesWeightsGraphReducer = new GamaMap<>(edgesGraphReducer.size(), edgeTypeGraphReducer, new GamaPairType());
		
// 		for (final Object edge : valuesGraphReducer.values()) {
		for (final Object edge : edgesGraphReducer) {
			// edgesWeightsGraphReducer.put(k.getKey(), new EdgeReducer(k.getValue(), g.getWeightOf(k.getValue())));
			edgesWeightsGraphReducer.put(edge, g.getWeightOf(edge));
		}

	}

//	public GamaMap getValuesGraphReducer() {return valuesGraphReducer; }
	public GamaList getEdgesGraphReducer() {return edgesGraphReducer; }
	public GamaMap getWeightsGraphReducer() {return edgesWeightsGraphReducer; }
	
//	public void setValuesGraphReducer(GamaMap m) { valuesGraphReducer = m; }
	public void setEdgesGraphReducer(GamaList m) { edgesGraphReducer = m; }	
	public void setEdgesWeightsGraphReducer(GamaMap w) { edgesWeightsGraphReducer = w; }
	
	
	public GamaGraph constructObject(final IScope scope) {		
//		GamaGraph graph = (GamaGraph) GamaGraphType.from(scope, valuesGraphReducer, spatial);
		GamaGraph graph;
//		if(IReference.isReference(valuesGraphReducer) || IReference.isReference(edgesWeightsGraphReducer)) {
		if(IReference.isReference(edgesGraphReducer) || IReference.isReference(edgesWeightsGraphReducer)) {
			graph = new ReferenceGraph(this);
		} else {		
//			graph = (GamaGraph) GamaGraphType.from(scope, valuesGraphReducer.getValues(), 
			graph = (GamaGraph) GamaGraphType.from(scope, edgesGraphReducer, 					
				true, directed, spatial, nodeTypeGraphReducer,
				edgeTypeGraphReducer) ;
		
			graph.setWeights(edgesWeightsGraphReducer);
			//for (final Object el : edgesWeightsGraphReducer.entrySet()) {
			//	Map.Entry entry = (Map.Entry) el;
			//	graph.setEdgeWeight(e, weight);
			//}
		}
		return graph;
	}

	public void unreferenceReducer(SimulationAgent sim) {	
//		valuesGraphReducer = (GamaMap)IReference.getObjectWithoutReference(valuesGraphReducer,sim);
		edgesGraphReducer = (GamaList)IReference.getObjectWithoutReference(edgesGraphReducer,sim);		
		edgesWeightsGraphReducer = (GamaMap)IReference.getObjectWithoutReference(edgesWeightsGraphReducer,sim);
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
