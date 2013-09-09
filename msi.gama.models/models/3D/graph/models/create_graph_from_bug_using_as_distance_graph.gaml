model Graph

import 'bug.gaml'
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
		draw sphere(1) color: rgb('green'); 
	}
}

species edge {
	aspect base {
		draw shape color: rgb('blue');
	}
}

experiment basicGraph type: gui {
	output {	
	 display graph_view type: opengl ambient_light: 0.2 {
			species node aspect: base;
			species edge aspect: base;
		}
	}
}
