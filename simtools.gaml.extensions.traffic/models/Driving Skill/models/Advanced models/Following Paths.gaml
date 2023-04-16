/**
* Name: Following Paths
* Description: Vehicles moving between two intersection using the same path.
			   Note that all vehicles slow down when reaching the end of the path, because at that point they don't know which road to choose next,
			   and thus cannot determine their leading vehicle.
* Author: Duc Pham
* Tags: driving skill, graph, agent_movement, skill, transport
*/

model FollowingPaths

import "Traffic.gaml"

global {
	float step <- 0.1 #s;
	
	string map_name <- "rouen";
	file shp_roads <- file("../../includes/" + map_name + "/roads.shp");
	file shp_nodes <- file("../../includes/" + map_name + "/nodes.shp");

	geometry shape <- envelope(shp_roads) + 50; 
	
	graph road_network;
	
	init {
		create my_road from: shp_roads with: [num_lanes::int(read("lanes"))] {
			// Create another road in the opposite direction
			create my_road {
				num_lanes <- myself.num_lanes;
				shape <- polyline(reverse(myself.shape.points));
				maxspeed <- myself.maxspeed;
				linked_road <- myself;
				myself.linked_road <- self;
			}
		}
		
		create my_intersection from: shp_nodes
				with: [is_traffic_signal::(read("type") = "traffic_signals")] {
			time_to_change <- 30#s;
		}
		
		// Create a graph representing the road network, with road lengths as weights
		map edge_weights <- my_road as_map (each::each.shape.perimeter);
		road_network <- as_driving_graph(my_road, my_intersection) with_weights edge_weights;
		
		// Initialize the traffic lights
		ask my_intersection {
			do initialize;
		}
		create vehicle_following_path number: 100;
	}
}

species vehicle_following_path parent: base_vehicle {
	init {
		vehicle_length <- 1.9 #m;
		max_speed <- 100 #km / #h;
		max_acceleration <- 3.5;
	}

	reflex select_next_path when: current_path = nil {
		// A path that forms a cycle
		list<my_intersection> dst_nodes <- [my_intersection[98], my_intersection[100], my_intersection[137], my_intersection[98]];
		do compute_path graph: road_network nodes: dst_nodes;
	}
	
	reflex commute when: current_path != nil {
		do drive;
	}
}

experiment city type: gui {
	output synchronized: true {
		display map type: 3d background: #gray {
			species my_road aspect: base;
			species vehicle_following_path aspect: base;
			species my_intersection aspect: base;
		}
	}
}
