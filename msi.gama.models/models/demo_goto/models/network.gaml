model Network
// Proposed by Patrick Taillandier


global {
	var shape_file_in type: string init: '../includes/gis/roads.shp' ;
	var the_graph type: graph;
	init {    
		create species: road from: shape_file_in ;
		set the_graph value: as_edge_graph(list(road) collect (each.shape));
		
		create species: but number: 1 {
			let my_road type: road value: one_of (list(road));
			set location value:any_location_in (my_road.shape);
		}
		create species: people number: 100 {
			set goal value: one_of (but as list) ;
			let my_road type: road value: one_of (list(road));
			set location value:any_location_in (my_road.shape);
		}
	}
}
environment bounds: shape_file_in ; 
entities {
	species road  {
		var speed_coef type: float ;
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
		var goal type: but ;
		var my_path type: path;
	
		aspect default {
			draw shape: circle color: 'green' size: 10 ;
		}
		reflex {
			let followed_path type: path value: self.goto [on::the_graph, target::goal.location, speed::1];
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
