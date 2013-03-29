model Network
// Proposed by Patrick Taillandier

global {
	file shape_file_in <- file('../includes/gis/roads.shp') ;
	//file shape_file_in <- file('../includes/gis/Reseau_Rouen_limit.shp') ;
	//file shape_file_in <- file('../includes/gis/Reseau_TC.shp') ;
	graph the_graph; 
	init {    
		create road from: shape_file_in ;
		set the_graph <- as_edge_graph(list(road));
		 
		create goal number: 1 {
			let my_road type: road <- one_of (list(road));
			set location <- any_location_in (my_road.shape);
		}
		create people number: 1000 {
			set target <- one_of (goal as list) ;
			let my_road type: road <- one_of (list(road));
			set location <- any_location_in (my_road.shape);
		} 
		write "nb edges : " + length(road);
		write "nb nodes : " + length(the_graph.vertices);
		
	}
}
environment bounds:  shape_file_in ; 
entities {
	species road  {
		float speed_coef ;
		aspect default {
			draw geometry: shape color: rgb('black') ;
		}
	} 
	species goal {
		aspect default {
			draw circle(10) color: rgb('red');
		}
	}
	species people skills: [moving] {
		goal target;
		path my_path; 
	
		aspect default {
			draw circle(10) color: rgb('green');
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
