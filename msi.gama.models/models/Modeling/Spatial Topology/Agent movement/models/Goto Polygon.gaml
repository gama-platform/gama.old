/**
* Name:  Movement on a Graph created by Polygons
* Author:  Patrick Taillandier
* Description: Model to show how to create a graph using a polygon shapefile by skeletonizing it, and creating roads using the skeleton. 
* 	All of the agents will use this graph to go to the same targeted location.
* Tags: graph, agent_movement, shapefile, skill, shortest_path
*/

model polygon
global {
	//Import of the shapefile containing the different polygons
	file shape_file_in <- file('../includes/gis/squareHole.shp') ;
	graph the_graph;
	
	geometry shape <- envelope(shape_file_in);
	
	init {    
		create object from: shape_file_in ;
		object the_object <- first(object);
		
		//triangulation of the object to get the different triangles of the polygons
		list<geometry> triangles <- list(triangulate(the_object, 0.01));
		
		loop trig over: triangles {
			create triangle_obj {
				shape <- trig;
			}
		}
		
		//creation of a list of skeleton from the object 
		list<geometry> skeletons <- list(skeletonize(the_object, 0.01));
		
		//Split of the skeletons list according to their intersection points
		list<geometry> skeletons_split  <- split_lines(skeletons);
		loop sk over: skeletons_split {
			create skeleton {
				shape <- sk;
			}
		}
		
		//Creation of the graph using the edges resulting of the splitted skeleton
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
		draw shape color: #gray ;
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
		draw shape + 0.2 color: #red ;
	}
}
	
species goal {
	aspect default {
		draw circle(3) color:#red;
	}
}

species people skills: [moving] {
	goal target;
	path my_path; 
	
	reflex goto {
		do goto on:the_graph target:target speed:1.0;
	}
	aspect default {
		draw circle(3) color: #green;
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

