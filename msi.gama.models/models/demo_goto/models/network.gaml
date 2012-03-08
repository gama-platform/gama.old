model Network
// Proposed by Patrick Taillandier

global {
	file shape_file_in <- file('../includes/gis/roads.shp') ;
	graph the_graph;
	init {    
		create species: road from: shape_file_in ;
		set the_graph <- as_edge_graph(list(road));
		
		create species: but number: 1 {
			let my_road type: road <- one_of (list(road));
			set location <- any_location_in (my_road.shape);
		}
		create species: people number: 1 {
			set goal <- one_of (but as list) ;
			let my_road type: road <- one_of (list(road));
			set location <- any_location_in (my_road.shape);
		} 
	}
}
environment bounds: shape_file_in ; 
entities {
	species road  {
		float speed_coef ;
		aspect default {
			draw shape: geometry color: 'black' ;
		}
	}
	species but {
		aspect default {
			draw shape: circle color: 'red' size: 10 ;
		}
	}
	species people skills: [moving] {
		but goal;
		path my_path; 
	
		aspect default {
			draw shape: circle color: 'green' size: 10 ;
		}
		reflex {
			do goto on:the_graph target:goal.location speed:1;
		}
	}
}
output {
	display objects_display {
		species road aspect: default ;
		species people aspect: default ;
		species but aspect: default ;
	}
}
