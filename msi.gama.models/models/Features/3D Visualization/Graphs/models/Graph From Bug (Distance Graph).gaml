model SpatialGraph

/**
 *  SpatialGraph
 *  Author: Arnaud Grignard
 *  Description: From the reference model "bug.gaml" a spatial graph is created. 
 *  We create a species node that mirrors the species bug and then a spatial graph is creating 
 *  using as_distance_graph operator. 
 */

import '../includes/Common Bug Species.gaml'
global { 
	graph myGraph;
	float distance parameter: 'Distance' min: 1.0 <- 10.0 category: 'Model';
	reflex updateGraph {
		ask edge_agent {
			do die;
		}
		myGraph <- as_distance_graph(node_agent, ["distance"::distance, "species"::edge_agent]);
	}
}

species node_agent mirrors: list(bug) {
	point location <- target.location update: target.location;
	aspect base {
		draw sphere(1.1) color: rgb('green'); 
	}
}

species edge_agent {
	aspect base {
		draw shape color: rgb('green');
	}
}

experiment spatialGraph type: gui {
	output {	
	 display graph_view type: opengl {
	 	    species bug aspect:base;
			species node_agent aspect: base position:{0,0,0.1};
			species edge_agent aspect: base position:{0,0,0.1};
		}
	}
}
