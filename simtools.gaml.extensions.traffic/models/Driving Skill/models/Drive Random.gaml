/**
* Name: Drive Random
* Description: Vehicles driving randomly in a road graph
* Author: minhduc0711
* Tags: gis, shapefile, graph, agent_movement, skill, transport
*/

model ring

import "Traffic.gaml"

global {
	float traffic_light_interval parameter: 'Traffic light interval' init: 30#s;
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
			
			switch oneway {
				match "no" {
					create road {
						num_lanes <- 5;
						shape <- polyline(reverse(myself.shape.points));
						maxspeed <- myself.maxspeed;
						linked_road <- myself;
						myself.linked_road <- self;
					}
				}

				match "-1" {
					shape <- polyline(reverse(shape.points));
				}

			}
		}
		
		create intersection from: shp_nodes
				with: [is_traffic_signal::(read("type") = "traffic_signals")] {
			time_to_change <- traffic_light_interval;
		}
		map general_speed_map <- road as_map (each::(each.shape.perimeter / each.maxspeed));
		
		full_road_graph <- as_driving_graph(road, intersection);
		ask intersection {
			do initialize;
		}
		
		create motorbike_random number: num_motorbikes {
			road_graph <- full_road_graph;

			right_side_driving <- true;
			
			proba_block_node <- 0.0;
			proba_respect_priorities <- 1.0;
			proba_respect_stops <- [1.0];
			proba_use_linked_road <- 0.1;
			
			linked_lane_limit <- 2;
			lane_change_limit <- 1;
			
			location <- one_of(intersection where empty(each.stop)).location;
		}
		
		create car_random number: num_cars {
			road_graph <- full_road_graph;
	
			right_side_driving <- true;
			
			proba_block_node <- 0.0;
			proba_respect_priorities <- 1.0;
			proba_respect_stops <- [1.0];
			proba_use_linked_road <- 0.0;

			lane_change_limit <- 2;			
			linked_lane_limit <- 0;

			location <- one_of(intersection where empty(each.stop)).location;
		}
	}
}

experiment ring type: gui {
	action _init_{ 
		create simulation with:[
			map_name::"ring",
			num_cars::20,
			num_motorbikes::70
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

// TODO: bad shape files (some intersections have no outgoing roads)
//experiment rouen type: gui {
//	action _init_{ 
//		create simulation with:[
//			map_name::"rouen",
//			num_cars::100,
//			num_motorbikes::250
//		];
//	}
//
//	output {
//		display city type: java2D background: #gray synchronized: true {
//			species road aspect: base;
//			species car_random aspect: base;
//			species motorbike_random aspect: base;
//			species intersection aspect: base;
//		}
//	}
//}