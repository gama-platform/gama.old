model Network
// Proposed by Patrick Taillandier

global {
	file shape_file_in <- file('../includes/gis/Reseau_TC.shp') ;
	graph the_graph; 
	geometry shape <- envelope(shape_file_in);
	init {    
		create road from: shape_file_in ;
		the_graph <- as_edge_graph(list(road));
		 
		create goal number: 1 {
			location <- any_location_in (one_of(road));
		}
		create people number: 100 {
			target <- one_of (goal) ;
			location <- any_location_in (one_of(road));
		} 
	}
}
entities {
	species road  {
		float speed_coef ;
		aspect default {
			draw shape color: rgb('black') ;
		}
	} 
	species goal {
		aspect default {
			draw circle(100) color: rgb('red');
		}
	}
	species people skills: [moving] {
		goal target;
		path my_path; 
	
		aspect default {
			draw circle(100) color: rgb('green');
		}
		reflex movement {
			do goto on:the_graph target:target speed:1;
		}
	}
}

experiment goto_network type: gui {
	output {
		display objects_display {
			species road aspect: default ;
			species people aspect: default ;
			species goal aspect: default ;
		}
	}
}
