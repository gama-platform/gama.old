package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.types.GamaGraphType;
import msi.gaml.types.IType;

public class GamaGraphReducer {
	private IType keyTypeGraphReducer;	
	private IType contentTypeGraphReducer;
	
	private GamaMap valuesGraphReducer;
	private boolean spatial;
	
	public GamaGraphReducer(IScope scope, GamaGraph g)
	{		
		spatial = (g instanceof GamaSpatialGraph);
		keyTypeGraphReducer = g.getType().getKeyType();
		contentTypeGraphReducer = g.getType().getContentType();
		
		valuesGraphReducer = g.mapValue(scope, keyTypeGraphReducer, contentTypeGraphReducer, false);
	}
	
	public GamaGraph constructObject(IScope scope)
	{
		return (GamaGraph) GamaGraphType.from(scope, valuesGraphReducer, spatial);
	}
}
