package ummisco.gama.serializer.gamaType.reduced;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gama.util.IReference;
import msi.gama.util.graph.IGraph;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.GamaSpatialPath;
import msi.gama.util.path.PathFactory;
import ummisco.gama.serializer.gamaType.reference.ReferencePath;

public class GamaPathReducer {
	
	IGraph<Object,Object> g;
	Object start;
	Object target;
	GamaList<Object> edges;
	boolean spatial;
	
	public GamaPathReducer(final GamaPath p) {
		g = p.getGraph();
		start = p.getStartVertex();
		target = p .getEndVertex();
		edges = (GamaList<Object>) p.getEdgeList();
		spatial = p instanceof GamaSpatialPath;
	}
	
	public void unreferenceReducer(SimulationAgent sim) {
		g = (IGraph<Object, Object>) IReference.getObjectWithoutReference(g,sim);
		start = IReference.getObjectWithoutReference(start,sim);
		target = IReference.getObjectWithoutReference(target,sim);
		edges = (GamaList) IReference.getObjectWithoutReference(edges,sim);		
	}
	
	public GamaPath constructObject(final IScope scope) {		

		GamaPath path = null;
		if(IReference.isReference(g) || IReference.isReference(start) || 
				IReference.isReference(target) || IReference.isReference(edges)) {
			path = new ReferencePath(this);
		} else {		
			path = PathFactory.newInstance(g, start,  target, edges);	
		}
		return path;		
	}
}
