/**
 *  testTriangulation
 *  Author: patricktaillandier
 *  Description: 
 */ 

model polygon
global {
	file shape_file_in <- file('../includes/gis/squareHole.shp') ;
	graph the_graph;
	
	init {    
		create object from: shape_file_in ;
		let the_object type: object <- first(object);
		let triangles type: container of: geometry <- triangulate(the_object);
		loop trig over: triangles {
			create triangle {
				set shape <- trig;
			}
		}
		let skeletons type: list of: geometry <- skeletonize(the_object);
		let skeletons_split type: list of: geometry <- split_lines(skeletons);
		loop sk over: skeletons_split {
			create skeleton {
				set shape <- sk;
			}
		}
		set the_graph <- as_edge_graph(list(skeleton));
		create goal number: 1 {
			set location <- any_location_in (one_of(skeleton as list)); 
		}
		create people number: 100 {
			set target <- one_of (goal as list) ; 
			set location <- any_location_in (one_of(skeleton as list));
		} 
	}
}
environment bounds: shape_file_in ; 
entities {
	species object  {
		aspect default {
			draw geometry: shape color: rgb('gray') ;
		}
	}
	species triangle  {
		rgb color <- rgb([150 +rnd(100),150 + rnd(100),150 + rnd(100)]);
		aspect default {
			draw geometry: shape color: color ;
		}
	}
	species skeleton  {
		aspect default {
			draw geometry: shape buffer 0.2 color: rgb('red') ;
		}
	}
	species goal {
		aspect default {
			draw shape: circle color:rgb('red') size: 3 ;
		}
	}
	species people skills: [moving] {
		goal target;
		path my_path; 
	
		reflex goto {
			do goto on:the_graph target:target speed:1;
		}
		aspect default {
			draw shape: circle color: rgb('green') size: 3 ;
		}
	}
}

experiment goto_polygon type: gui {
	output {
		display objects_display {
			species object aspect: default ;
			species triangle aspect: default ;
			species skeleton aspect: default ;
			species people aspect: default ;
			species goal aspect: default ;
		}
	}
}

