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
	int number_of_agents parameter: 'Number of Agents' min: 1 <- 30 category: 'Initialization';
	int width_and_height_of_environment parameter: 'Dimensions' min: 100 <- 500 category: 'Initialization';
	int distance parameter: 'distance ' min: 1 <- 100;
	int degreeMax <- 1;
	geometry shape <- square(width_and_height_of_environment);
	init {
		create node_agent number: number_of_agents {
			set degree <- 0;
			set location <- { rnd(width_and_height_of_environment), rnd(width_and_height_of_environment), rnd(width_and_height_of_environment) };
		}

	}

	reflex updateDegreeMax {
		degreeMax <- 1;
		ask node_agent {
			if ((my_graph) degree_of (self) > degreeMax) {
				degreeMax <- (my_graph) degree_of (self);
			}

		}

	}

}

species node_agent parent: graph_node edge_species: edge_agent skills: [moving] {
	int degree <- 1;
	bool related_to (node_agent other) {
		using topology(self) {
			return (self.location distance_to other.location) < distance;
		}

	}

	reflex move {
		do wander_3D;
	}

	float radius <- 1.0;
	aspect default {
		draw sphere(radius) color: rgb("blue");
	}

	aspect dynamic {
		degree <- (my_graph) degree_of (self);
		draw sphere(((((degree + 1) ^ 1.4) / (degreeMax)) * radius) * 5) color: hsb(degree / (degreeMax + 1), 0.5, 1.0);
	}

}

species edge_agent parent: base_edge {
	rgb color;
	aspect base {
		draw shape color: rgb('black');
	}

}

experiment Display type: gui {
	output {
		display WanderingSphere type: opengl ambient_light: 100 { species node_agent aspect: dynamic;
		species edge_agent aspect: base;
		}
	}

}
