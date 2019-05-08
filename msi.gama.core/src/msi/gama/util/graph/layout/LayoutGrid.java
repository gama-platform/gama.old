package msi.gama.util.graph.layout;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gaml.operators.Graphs;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.types.Types;
import sun.awt.util.IdentityArrayList;

public class LayoutGrid {
	
	private IGraph<IShape, IShape> graph;
	
	private double coeffSq;
	
	private IShape envelopeGeometry;
	
	public LayoutGrid(IGraph<IShape, IShape> graph, IShape envelopeGeometry, double coeffSq) {
		this.graph = graph;
		this.envelopeGeometry = envelopeGeometry;
		this.coeffSq = coeffSq;
	}

	public void applyLayout(IScope scope) {
        
	    IList<IShape> places = Spatial.Transformations.toSquares(scope, envelopeGeometry, Maths.round(graph.getVertices().size() * coeffSq), false);
	    IShape currentV = null; int dmax = -1;
	    Map<IShape, Integer> degrees = new IdentityHashMap<>();
	    int nbV = graph.getVertices().size();
	    for (IShape v : graph.getVertices()) {
	    	int d = graph.degreeOf(v);
	    	degrees.put(v, d);
	    	if (d > dmax) {dmax = d; currentV = v;}
	    }
	    IShape center = Queries.overlapping(scope, places, envelopeGeometry.getLocation()).firstValue(scope);
	    places.remove(center);
	    currentV.setLocation(center.getLocation());
	    List<IShape> open = new IdentityArrayList();
	    List<IShape> remaining = new IdentityArrayList();
	    remaining.addAll(graph.getVertices());
	    remaining.remove(currentV);
	       
	    List<IShape> close = new IdentityArrayList();
	    close.add(currentV);
	    while (close.size() < nbV) {
	    	IList<IShape> neigh = Graphs.predecessorsOf(scope, graph, currentV);
	    	neigh.addAll(Graphs.predecessorsOf(scope, graph, currentV));
	    	neigh = Random.opShuffle(scope, neigh);
	    	for(IShape n : neigh) {
	    		if (remaining.contains(n)) {
		    		 center = Queries.closest_to(scope, places, currentV.getLocation());
		    		 places.remove(center);
		    		 n.setLocation(center.getLocation());
		    		 open.add(n);
		    		 remaining.remove(n);
	    		}
		    }
	    	if (remaining.isEmpty()) break;
	    	dmax = -1;
	    	java.util.Collections.shuffle(open, scope.getRandom().getGenerator());
	    	for (IShape v : open) {
		    	int d = degrees.get(v);
		    	if (d >= dmax) {dmax = d; currentV = v;}
		    }
	    	open.remove(currentV);
	    	close.add(currentV);
	    	if (open.isEmpty()) {
	    		IShape nV = null;
		    	dmax = 0;
		    	java.util.Collections.shuffle(remaining, scope.getRandom().getGenerator());
		    	
		    	for (IShape v : remaining) {
		    		int d = degrees.get(v);
			    	if (d > dmax) {dmax = d; nV = v;}
			    }
		    	remaining.remove(nV);
		    	open.add(nV);
		    	List<IShape> neigh2 = Graphs.predecessorsOf(scope, graph, nV);
		    	neigh2.addAll(Graphs.predecessorsOf(scope, graph, nV));
		    	neigh2.removeAll(close);
		    	neigh2.removeAll(open);
		    	if (! neigh2.isEmpty()) {
		    		IList<GamaPoint> pts = GamaListFactory.create(Types.POINT);
			    	for (IShape n : neigh2) pts.add(n.getCentroid());
			    	GamaPoint targetLoc = (GamaPoint) msi.gaml.operators.Containers.mean(scope, pts);
			    	 center = Queries.closest_to(scope, places, targetLoc.getLocation());
			    }
		    	else {
		    		center = places.anyValue(scope);	
		    	}
		    	places.remove(center);
	    		nV.setLocation(center.getLocation());
	    		
		    	
	    	}
	    	
	    }
	    
		
	
	}
	
}
