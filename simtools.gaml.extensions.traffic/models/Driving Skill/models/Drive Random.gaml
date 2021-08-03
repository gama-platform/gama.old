/**
* Name: Drive Random
* Description: Vehicles driving randomly in a road graph
* Author: Duc Pham
* Tags: gis, shapefile, graph, agent_movement, skill, transport
*/

model DriveRandom

import "Traffic.gaml"

global {
	float traffic_light_interval parameter: 'Traffic light interval' init: 60#s;
	float step <- 0.1#s;
    graph full_road_graph;
   
	string map_name;
	file shp_roads <- file("../includes/" + map_name + "/roads.shp");
	file shp_nodes <- file("../includes/" + map_name + "/nodes.shp");

	geometry shape <- envelope(shp_roads) + 50;
	
	int num_cars;
	int num_motorbikes;

	init {
		create road from: shp_roads {
			num_lanes <- 5;
			
//			// Create another road in the opposite direction
//			create road with: [shape::polyline(reverse(shape.points))] {
//				num_lanes <- 5;
//				maxspeed <- myself.maxspeed;
//				linked_road <- myself;
//				myself.linked_road <- self;
//			}
		}
		
		create intersection from: shp_nodes
				with: [is_traffic_signal::(read("type") = "traffic_signals")] {
			time_to_change <- traffic_light_interval;
		}
		
		// Create a graph representing the road network, with road lengths as weights
		map edge_weights <- road as_map (each::each.shape.perimeter);
		full_road_graph <- as_driving_graph(road, intersection) with_weights edge_weights;
		
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
		road_graph <- full_road_graph;
		location <- one_of(intersection where empty(each.stop)).location;
		right_side_driving <- true;
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

		lane_change_limit <- 1;		
		linked_lane_limit <- 2;
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

	output {
		display city type: java2D background: #gray synchronized: true {
			species road aspect: base;
			species car_random aspect: base;
			species motorbike_random aspect: base;
			species intersection aspect: base;
		}
	}
}
