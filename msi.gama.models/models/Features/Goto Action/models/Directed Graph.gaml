/**
 *  simplemodel
 *  Author: patrick
 *  Description: 
 */

model simplemodel

global {
	file road_file <- file("../includes/gis/roads.shp");
	geometry shape <- envelope(road_file);
	graph the_graph; 
	
	init {
		create road from: road_file with:[direction::int(read("DIRECTION"))] {
			switch direction {
				match 0 {color <- rgb("green");}
				match 1 {color <- rgb("red");
					//inversion of the road geometry
					shape <- polyline(reverse(shape.points));
				}
				match 2 {color <- rgb("blue");
					//bidirectional: creation of the inverse road
					create road {
						shape <- polyline(reverse(myself.shape.points));
						direction <- 2;
						color <- rgb("blue");
					}
				} 
			}
		}
		the_graph <- directed(as_edge_graph(road)) ;
		create people number: 1000 {
			target <- any_location_in(one_of (road)) ;
			location <- any_location_in (one_of(road));
			source <- location;
		} 
	}
}

species road {
	int direction;
	rgb color;
	aspect geom {
		draw shape color: color;
	}
}
	
species people skills: [moving] {
	point target;
	path my_path; 
	point source;
	string r_s;
	string r_t; 
	aspect circle {
		draw circle(10) color: rgb('green');
	}
	reflex movement {
		my_path <- self goto (on:the_graph, target:target, speed:10, return_path: true);
		if (target = location) {			
			target <- any_location_in(one_of (road)) ;
			source <- location;
		}
	}
}

experiment simplemodel type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
		display map {
			species road aspect: geom;
			species people aspect: circle;
		}
	}
}
