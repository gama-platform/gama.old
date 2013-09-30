model SpatialGraph

/**
 *  SpatialGraph
 *  Author: Arnaud Grignard
 *  Description: From the reference model "bug.gaml" a spatial graph is created. 
 *  We create a species node that mirrors the species bug and then a spatial graph is creating 
 *  using as_distance_graph operator. 
 */

import 'Common Bug Species.gaml'
global { 
	graph myGraph;
	float distance parameter: 'Distance' min: 1.0 <- 10.0 category: 'Model';
	reflex updateGraph {
		ask edge {
			do die;
		}
		myGraph <- as_distance_graph(node, map(["distance"::distance, "species"::edge]));
	}
}

species node mirrors: list(bug) {
	point location <- target.location update: target.location;
	aspect base {
		draw sphere(1.1) color: rgb('green'); 
	}
}

species edge {
	aspect base {
		draw shape color: rgb('green');
	}
}

experiment spatialGraph type: gui {
	output {	
	 display graph_view type: opengl {
	 	    species bug aspect:base;
			species node aspect: base;
			species edge aspect: base;
		}
	}
}
