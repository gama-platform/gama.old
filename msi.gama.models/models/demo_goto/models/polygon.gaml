/**
 *  testTriangulation
 *  Author: patricktaillandier
 *  Description: 
 */

model testTriangulation
global {
	file shape_file_in <- file('../includes/gis/squareHole.shp') ;
	graph the_graph;
	
	init {    
		create species: object from: shape_file_in ;
		let the_object type: object <- first(object);
		let triangles type: list of: geometry <- triangulate(the_object);
		loop trig over: triangles {
			create species: triangle {
				set shape <- trig;
			}
		}
		let skeletons type: list of: geometry <- skeletonize(the_object);
		let skeletons_split type: list of: geometry <- split_lines(skeletons);
		loop sk over: skeletons_split {
			create species: skeleton {
				set shape <- sk;
			}
		}
		set the_graph <- as_edge_graph(list(skeleton));
		create species: but number: 1 {
			set location <- any_location_in (one_of(skeleton as list));
		}
		create species: people number: 100 {
			set goal <- one_of (but as list) ;
			set location value:any_location_in (one_of(skeleton as list));
		} 
	}
}
environment bounds: shape_file_in ; 
entities {
	species object  {
		aspect default {
			draw shape: geometry color: 'gray' ;
		}
	}
	species triangle  {
		rgb color <- rgb([150 +rnd(100),150 + rnd(100),150 + rnd(100)]);
		aspect default {
			draw shape: geometry color: color ;
		}
	}
	species skeleton  {
		aspect default {
			draw shape: geometry color: 'black' ;
		}
	}
	species but {
		aspect default {
			draw shape: circle color: 'red' size: 3 ;
		}
	}
	species people skills: [moving] {
		but goal;
		path my_path; 
	
		aspect default {
			draw shape: circle color: 'green' size: 3 ;
		}
		reflex {
			do goto on:the_graph target:goal.location speed:1;
		}
	}
}
output {
	display objects_display {
		species object aspect: default ;
		species triangle aspect: default ;
		species skeleton aspect: default ;
		species people aspect: default ;
		species but aspect: default ;
	}
}
