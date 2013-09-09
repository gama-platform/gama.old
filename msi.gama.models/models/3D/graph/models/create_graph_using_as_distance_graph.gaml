model Graph


global {
	graph myGraph;
	float distance parameter: 'Distance' min: 1.0 <- 10.0 category: 'Model';
	
	init{
		create node number:500;
	}
	
	reflex updateGraph {
		ask edge as list {
			do die;
		}
		set myGraph <- as_distance_graph(list(node), map(["distance"::distance, "species"::edge]));
	}	
}

entities {
	species node  {
		aspect base {
			draw sphere(1) color: rgb('green');
		}
	}

	species edge {
		aspect base {
			draw shape color: rgb('blue');
		}
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
