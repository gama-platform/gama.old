/**
 *  testTriangulation
 *  Author: patricktaillandier
 *  Description: 
 */ 

model polygon
global {
	file shape_file_in <- file('../includes/gis/squareHole.shp') ;
	graph the_graph;
	
	geometry shape <- envelope(shape_file_in);
	
	init {    
		create object from: shape_file_in ;
		object the_object <- first(object);
		list<geometry> triangles <- list(triangulate(the_object));
		loop trig over: triangles {
			create triangle_obj {
				shape <- trig;
			}
		}
		list<geometry> skeletons <- list(skeletonize(the_object));
		list<geometry> skeletons_split  <- split_lines(skeletons);
		loop sk over: skeletons_split {
			create skeleton {
				shape <- sk;
			}
		}
		 the_graph <- as_edge_graph(skeleton);
		create goal  {
			 location <- any_location_in (one_of(skeleton)); 
		}
		create people number: 100 {
			 target <- one_of (goal) ; 
			 location <- any_location_in (one_of(skeleton));
		} 
	}
}

species object  {
	aspect default {
		draw shape color: rgb('gray') ;
	}
}

species triangle_obj  {
	rgb color <- rgb(150 +rnd(100),150 + rnd(100),150 + rnd(100));
	aspect default {
		draw shape color: color ; 
	}
}

species skeleton  {
	aspect default {
		draw shape + 0.2 color: rgb('red') ;
	}
}
	
species goal {
	aspect default {
		draw circle(3) color:rgb('red');
	}
}

species people skills: [moving] {
	goal target;
	path my_path; 
	
	reflex goto {
		do goto on:the_graph target:target speed:1;
	}
	aspect default {
		draw circle(3) color: rgb('green');
	}
}

experiment goto_polygon type: gui {
	output {
		display objects_display {
			species object aspect: default ;
			species triangle_obj aspect: default ;
			species skeleton aspect: default ;
			species people aspect: default ;
			species goal aspect: default ;
		}
	}
}

