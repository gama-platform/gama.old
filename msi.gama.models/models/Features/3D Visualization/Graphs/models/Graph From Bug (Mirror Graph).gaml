model Graph

import '../includes/Common Bug Species.gaml'

/**
 *  SpatialGraph
 *  Author: Arnaud Grignard
 *  Description: From the reference model "bug.gaml" a spatial graph is created. 
 *  We create a species node that mirrors the species bug and then a spatial graph is creating.
 *  The spatial graph is created by making node inherit from graph_node; The species node should then 
 *  define its own related_to method to decide wether or not a node is related to another one.  
 */
global {
	int distance parameter: 'Distance' min: 1 <- 25 category: 'Model';
	int startAnimation parameter: 'Start Animation ' min: 1 <- 25 category: 'Animation View';
	int timeAnim <- 0;
	reflex updateAnimation {
		if (time > startAnimation) {
			timeAnim <- int(time - startAnimation);
		}

	}

}

species node_agent mirrors: list(bug) parent: graph_node edge_species: edge_agent {
	point location <- target.location update: target.location;
	bool related_to (node_agent other) {
		using topology(target) {
			return (target.location distance_to other.target.location) < distance;
		}

	}

	aspect base {
		draw sphere(2) color: rgb('green');
	}

	aspect dynamic {
		int degree <- (my_graph) degree_of (self);
		draw sphere(1 + (degree / 5.0)) color: rgb('blue');
	}

}

species edge_agent parent: base_edge {
	rgb color;
	aspect base {
		draw shape color: rgb("green");
	}

	aspect dynamic {
		shape <- line([{ self.source.location.x, self.source.location.y, self.source.location.z }, { self.target.location.x, self.target.location.y, self.target.location.z }]);
		float val <- 255.0 * (shape.perimeter / distance);
		color <- hsb(val, 1.0, 1.0);
		draw shape + 0.1 color: color border: color;
	}

}

experiment AdvancedView type: gui {
	output {
		display graph_plus_bug_layered type: opengl ambient_light: 10 diffuse_light:100{ 
		  species bug aspect: base;
		  species node_agent aspect: base position: { 0, 0, 0.2 };
		  species edge_agent aspect: base position: { 0, 0, 0.2 };
		  species node_agent aspect: dynamic position: { 0, 0, 0.4 };
		  species edge_agent aspect: dynamic position: { 0, 0, 0.4 };
		}
	}
}


