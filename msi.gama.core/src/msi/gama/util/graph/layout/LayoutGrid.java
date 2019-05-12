package msi.gama.util.graph.layout;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.xml.internal.bind.v2.runtime.Location;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
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
        
	    IList<IShape> places = null;
	    do {
	    	places = Spatial.Transformations.toSquares(scope, envelopeGeometry, Maths.round(graph.getVertices().size() * coeffSq), false);
	    } while (places.size() < graph.getVertices().size());
	    
	    IShape currentV = null; int dmax = -1;
	    Map<IShape, ILocation> locations = new IdentityHashMap<>();
		   
	    Map<IShape, Integer> degrees = new IdentityHashMap<>();
	    int nbV = graph.getVertices().size();
	    for (IShape v : graph.getVertices()) {
	    	int d = graph.degreeOf(v);
	    	degrees.put(v, d);
	    	if (d > dmax) {dmax = d; currentV = v;}
	    }
	    IShape center = Queries.overlapping(scope, places, envelopeGeometry.getLocation()).firstValue(scope);
	    places.remove(center);
	    //currentV.setLocation(center.getLocation());
	    locations.put(currentV, center.getLocation());
	    List<IShape> open = new IdentityArrayList();
	    List<IShape> remaining = new IdentityArrayList();
	    remaining.addAll(graph.getVertices());
	    remaining.remove(currentV);
	    
	       
	    List<IShape> close = new IdentityArrayList();
	    close.add(currentV);

    	  while (close.size() < nbV) {
    		  IList<IShape> neigh = null;
    		  neigh = Graphs.predecessorsOf(scope, graph, currentV);
    		  neigh.addAll(Graphs.successorsOf(scope, graph, currentV));
    		  neigh = Random.opShuffle(scope, neigh);
    		  for(IShape n : neigh) {
    			  if (remaining.contains(n)) {
		    		 center = Queries.closest_to(scope, places, currentV.getLocation());
		    		 places.remove(center);
		    		 locations.put(n, center.getLocation());
		    		// n.setLocation(center.getLocation());
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
		    	dmax = -1;
		    	java.util.Collections.shuffle(remaining, scope.getRandom().getGenerator());
		    	
		    	for (IShape v : remaining) {
		    		int d = degrees.get(v);
			    	if (d > dmax) {dmax = d; nV = v;}
			    }
		    	remaining.remove(nV);
		    	open.add(nV);
		    	Set<IShape> neigh2 = new HashSet(Graphs.predecessorsOf(scope, graph, nV));
		    	neigh2.addAll(Graphs.successorsOf(scope, graph, nV));
		    	if (! neigh2.isEmpty()) {
		    		IList<ILocation> pts = GamaListFactory.create(Types.POINT);
		    		for (IShape n : neigh2) 
		    			if (locations.containsKey(n)) pts.add(locations.get((IShape)n));
			    	GamaPoint targetLoc = (GamaPoint) msi.gaml.operators.Containers.mean(scope, pts);
			    	 center = places.size() > 0 ? Queries.closest_to(scope, places, targetLoc.getLocation()) : nV.getLocation();
			    }
		    	else {
		    		
		    		center = places.size() > 0 ? places.anyValue(scope) : nV.getLocation() ;	
		    	}
		    	places.remove(center);
		    	locations.put(nV, center.getLocation());
	    		
		    	
	    	}
	    	
	    }
	    for (IShape s : locations.keySet()) {
	    	s.setLocation(locations.get(s));
	    }
		
	
	}
	
}
