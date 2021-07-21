/**
* Name: Following Paths
* Description: Demonstrate the use of drive action to make drivers follow certain paths
* Author: Duc Pham
* Tags: driving skill, graph, agent_movement, skill, transport
*/

model FollowingPaths

import "Traffic.gaml"

global {
	float step <- 0.1 #s;
	
	string map_name <- "gama_logo";
	file shp_roads <- file("../includes/" + map_name + "/roads.shp");
	file shp_nodes <- file("../includes/" + map_name + "/nodes.shp");

	geometry shape <- envelope(shp_roads) + 50; 
	
	graph full_road_graph;
	
	init {
		create road from: shp_roads {
			num_lanes <- 1;
		}
		create intersection from: shp_nodes;
		
		// Create a graph representing the road network, with road lengths as weights
		map edge_weights <- road as_map (each::each.shape.perimeter);
		full_road_graph <- as_driving_graph(road, intersection) with_weights edge_weights;
		
		create vehicle_following_path number: 1;
	}
}

species vehicle_following_path parent: base_vehicle {
	list<path> paths;
	int path_idx <- -1;
	
	init {
		vehicle_length <- 3.0;
		max_speed <- 120 #km / #h;
		max_acceleration <- 1 #m / #s;
		location <- intersection[0].location;
		// Compute paths that will lead the vehicle from node 0 to node 1,
		// node 1 to node 2,... and node N back to node 0
		loop i over: range(0, length(intersection) - 1) {
			int src <- i;
			int dst <- i != length(intersection) - 1 ? i + 1 : 0;
			add path_between(full_road_graph, intersection[src], intersection[dst]) 
				to: paths;
		}

	}

	reflex select_next_path when: final_target = nil {
		path_idx <- mod(path_idx + 1, length(paths));
		current_path <- paths[path_idx];
	}
	
	reflex commute when: current_path != nil {
		do drive;
	}
	
	reflex emit_particles when: every(0.3#s) and path_idx in [0, 2, 4] {
		rgb clr;
		if path_idx = 0 {
			clr <- rgb(239,181,79);
		} else if path_idx = 2 {
			clr <- rgb(210,103,58);
		} else {
			clr <- rgb(55,112,161);
		}
		
		float angle;
		float spread_angle <- 90.0;
		angle <- heading + rnd(180 - spread_angle / 2, 180 + spread_angle / 2);

		if (angle > 360) {
			angle <- angle - 360;
		} else if (angle < 0) {
			angle <- 360 + angle;
		}
		create particle {
			location <- myself.location;
			color <- clr;
			heading <- angle;
		}
	}
}

species particle {
	rgb color;
	float speed <- 1.0;
	float decel <- 0.01;
	float heading;

	float lifespan <- 150 #s;
	float time_left;
	
	init {
		time_left <- lifespan;
	}
	
	reflex move when: speed > 0 {
		float dist <- speed * step;
		location <- {
			location.x + cos(heading) * dist, 
			location.y + sin(heading) * dist
		};
		speed <- speed - decel;
	}
	
	reflex fade when: time_left > 0 {
		color <- rgb(color.red, color.green, color.blue, time_left / lifespan);
		time_left <- time_left - step;
	}
	
	reflex die when: time_left <= 0 {
		do die;
	}
	
	aspect base {
		draw circle(1.5) color: color;
	}
}

experiment exp type: gui {
	output {
		display city type: java2D background: #gray synchronized: true {
			species road aspect: base;
			species vehicle_following_path aspect: base;
			species intersection aspect: base;
			species particle aspect: base;
		}
	}
}