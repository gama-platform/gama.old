package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.metamodel.topology.graph.GamaSpatialGraph;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gaml.types.GamaGraphType;
import msi.gaml.types.IType;

public class GamaGraphReducer {
	private IType contentType;
	private IList values;
	private boolean spatial;
	
	public GamaGraphReducer(IScope scope, GamaGraph g)
	{		
		spatial = (g instanceof GamaSpatialGraph);
		contentType = g.getType().getContentType();
		values = g.listValue(scope, contentType, true);
	}
	
	public GamaGraph constructObject(IScope scope)
	{
//		public static IGraph from(final IScope scope, final GamaMap<?, ?> obj, final boolean spatial) {
//		public static IGraph from(final IScope scope, final IList obj, final boolean spatial) {

		return (GamaGraph) GamaGraphType.from(scope, values, true)	;

	}
}
