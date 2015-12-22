model graph3D

/**
 *  graph3D
 * 
 *  Author: Arnaud Grignard
 * 
 *  Description: Create and update a 3D Graph. Each node is represented by a sphere 
 *  with a size and a color that evolves according to its degree.
 */
global {
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 200 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 100 <- 500 category: 'Initialization';
	int distance parameter: 'distance ' min: 1 <- 100;
	int degreeMax <- 1;
	geometry shape <- square(width_and_height_of_environment);
	graph my_graph;
	init {
		create node_agent number: number_of_agents {
			location <- { rnd(width_and_height_of_environment), rnd(width_and_height_of_environment), rnd(width_and_height_of_environment) };
		}
		do degreeMax_computation;
		ask node_agent {
			do compute_degree;
		}
	}
	
	reflex updateDegreeMax {
		do degreeMax_computation;
	}

	action degreeMax_computation {
		my_graph <- node_agent as_distance_graph(distance);
		degreeMax <- 1;
		ask node_agent {
			if ((my_graph) degree_of (self) > degreeMax) {
				degreeMax <- (my_graph) degree_of (self);
			}
		}
	}
}

species node_agent skills: [moving] {
	int degree;
	float radius;
	rgb color ;
	float speed <- 5.0;
	reflex move {
		do wander_3D z_max: width_and_height_of_environment;
		do compute_degree;
	}
	action compute_degree {
		degree <- my_graph = nil ? 0 : (my_graph) degree_of (self);
		radius <- ((((degree + 1) ^ 1.4) / (degreeMax))) * 5;
		color <- hsb(0.66,degree / (degreeMax + 1), 0.5);
	}

    aspect base {
		draw sphere(10) color:°black;
	}
	
	aspect dynamic {
		draw sphere(radius) color: color;
	}

}

experiment Display type: gui {
	output {
		display WanderingSphere type: opengl ambient_light: 100 { 
			species node_agent aspect: dynamic;
			graphics "edges" {
				if (my_graph != nil) {
					loop eg over: my_graph.edges {
						geometry edge_geom <- geometry(eg);
						float val <- 255 * edge_geom.perimeter / distance; 
						draw line(edge_geom.points, 0.5)  color: rgb(val,val,val);
					}
				}
				
			}
		}
	}
}


experiment SimpleDisplay type: gui {
	output {
		display WanderingSphere type: opengl ambient_light: 100 { 
			species node_agent aspect: base;
			graphics "edges" {
				if (my_graph != nil) {
					loop eg over: my_graph.edges {
						geometry edge_geom <- geometry(eg);
						float val <- 255 * edge_geom.perimeter / distance; 
						draw line(edge_geom.points) color:°black;
					}
				}
				
			}
		}
	}
}
