/**
* Name: Drive Random
* Description: Vehicles driving randomly in a road graph
* Author: Duc Pham
* Tags: gis, shapefile, graph, agent_movement, skill, transport
*/

model DriveRandom

import "Traffic.gaml"

global {
	float seed <- 42.0;
	float traffic_light_interval parameter: 'Traffic light interval' init: 60#s;
	float step <- 0.2#s;

	string map_name;
	file shp_roads <- file("../../includes/" + map_name + "/roads.shp");
	file shp_nodes <- file("../../includes/" + map_name + "/nodes.shp");

	geometry shape <- envelope(shp_roads) + 50;
	
	int num_cars;
	int num_motorbikes;

	graph road_network;
	list<intersection> non_deadend_nodes;

	init {
		create road from: shp_roads {
			num_lanes <- rnd(4, 6);
			// Create another road in the opposite direction
			create road {
				num_lanes <- myself.num_lanes;
				shape <- polyline(reverse(myself.shape.points));
				maxspeed <- myself.maxspeed;
				linked_road <- myself;
				myself.linked_road <- self;
			}
		}
		
		create intersection from: shp_nodes
				with: [is_traffic_signal::(read("type") = "traffic_signals")] {
			time_to_change <- traffic_light_interval;
		}
		
		// Create a graph representing the road network, with road lengths as weights
		map edge_weights <- road as_map (each::each.shape.perimeter);
		road_network <- as_driving_graph(road, intersection) with_weights edge_weights;
		
		non_deadend_nodes <- intersection where !empty(each.roads_out);
		// Initialize the traffic lights
		ask intersection {
			do initialize;
		}
		
		create motorbike_random number: num_motorbikes;
		create car_random number: num_cars;
	}
}

species vehicle_random parent: base_vehicle {
	init {
		road_graph <- road_network;
		location <- one_of(non_deadend_nodes).location;
		right_side_driving <- true;
	}

	// Move the vehicle to a random node when it reaches a deadend
	reflex relocate when: next_road = nil and distance_to_current_target = 0.0 {
		do unregister;
		location <- one_of(non_deadend_nodes).location;
	}
	
	reflex commute {
		do drive_random graph: road_graph;
	}
}

species motorbike_random parent: vehicle_random {
	init {
		vehicle_length <- 1.9 #m;
		num_lanes_occupied <- 1;
		max_speed <- (50 + rnd(20)) #km / #h;

		proba_block_node <- 0.0;
		proba_respect_priorities <- 1.0;
		proba_respect_stops <- [1.0];
		proba_use_linked_road <- 0.5;

		lane_change_limit <- 2;		
		linked_lane_limit <- 1;
	}
}

species car_random parent: vehicle_random {
	init {
		vehicle_length <- 3.8 #m;
		num_lanes_occupied <- 2;
		max_speed <- (60 + rnd(10)) #km / #h;
				
		proba_block_node <- 0.0;
		proba_respect_priorities <- 1.0;
		proba_respect_stops <- [1.0];
		proba_use_linked_road <- 0.0;

		lane_change_limit <- 2;			
		linked_lane_limit <- 0;
	}
}

experiment ring type: gui {
	action _init_{ 
		create simulation with:[
			map_name::"ring",
			num_cars::50,
			num_motorbikes::100
		];
	}

	output synchronized: true {
		display map type: 3d background: #gray {
			species road aspect: base;
			species car_random aspect: base;
			species motorbike_random aspect: base;
			species intersection aspect: base;
		}
	}
}


experiment city type: gui {
	action _init_{
		create simulation with:[
			map_name::"rouen",
			num_cars::100,
			num_motorbikes::200
		];
	}

	output synchronized: true {
		display map type: 3d background: #gray {
			species road aspect: base;
			species car_random aspect: base;
			species motorbike_random aspect: base;
			species intersection aspect: base;
		}
	}
}
