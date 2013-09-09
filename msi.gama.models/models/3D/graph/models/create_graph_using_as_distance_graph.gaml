model Graph


global {
	graph myGraph;
	float distance min: 1.0 <- 10.0;
	
	init{
		create node number:500;
	}
	
	reflex updateGraph when: cycle = 1{
		ask edge  {
			do die;
		}
		myGraph <- as_distance_graph(node, map(["distance"::distance, "species"::edge]));
	}	
}

entities {
	species node skills:[moving] {
		reflex move {
			do wander;
		}
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
	parameter 'Distance' var: distance category: 'Model';
	output {			
	    display graph_view type: opengl ambient_light: 0.2 {
			species node aspect: base;
			species edge aspect: base;
		}
	}
}
