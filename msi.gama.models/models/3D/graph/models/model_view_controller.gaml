model Graph

import 'bug.gaml'
global {
	graph myGraph;
	float distance parameter: 'Distance' min: 1.0 <- 10.0 category: 'Model';
	reflex updateGraph {
		ask edge as list {
			do die;
		}
		set myGraph <- as_distance_graph(list(node), map(["distance"::distance, "species"::edge]));
	}
}

entities {
	species node mirrors: list(bug) {
		point location <- target.location value: target.location;
		aspect base {
			draw sphere(1) color: rgb('green');
		}

		aspect dynamic {
			int degree <- myGraph degree_of (self);
			draw sphere(1 + (degree / 5.0)) color: rgb('blue');
		}

	}

	species edge {
		rgb color;
		aspect base {
			draw shape color: rgb('blue');
		}

		aspect dynamic {
			float val <- 255.0 * (shape.perimeter / distance);
			set color <- color hsb_to_rgb ([val, 1.0, 1.0]);
			draw shape + 0.1 color: color border: color;
		}

	}

}

experiment basicGraph type: gui {
	output {
		
	  display referencemodel type:opengl ambient_light:0.2 {
	    	species bug aspect:base;
	  }
	  		
	    display advanced_view type: opengl ambient_light: 0.2 {
			species bug aspect: base;
			species node aspect: base z: 0.2;
			species edge aspect: base z: 0.2;
			species node aspect: dynamic z: 0.4;
			species edge aspect: dynamic z: 0.4; 
		}
		
		display MVC type:opengl ambient_light:0.2 draw_env:false{
	    	species bug aspect:base z:0 position: {0.25,0,0} ;
	        species node aspect: base z:0 position: {0,150,0};
	        species edge aspect: base z:0 position: {0,150,0};
	        
	        species node aspect: dynamic z:0 position: {250,150,0};
	        species edge aspect: dynamic z:0 position: {250,150,0};
	  }
	}

}
