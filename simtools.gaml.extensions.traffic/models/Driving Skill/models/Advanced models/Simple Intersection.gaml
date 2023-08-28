/**
* Name: Mix Drive City
* Description: Vehicles driving in a road graph
* Author: Duc Pham and Patrick Taillandier
* Tags: gis, shapefile, graph, agent_movement, skill, transport
*/

model simple_intersection


global {
	float size_environment <- 1#km;
	
	geometry shape <- envelope(size_environment);
	
	//the typical step for the advanced driving skill
	float step <- 0.5 #s;
	
	//use only for display purpose
	float lane_width <- 2.0;
	
	//number of cars
	int num_cars <- 300;
	
	float proba_block_node_car <- 1.0; 
	
	//graph used for the shortest path computation
	graph road_network;
	
	init {
		create intersection with: (location: {10,size_environment/2});
		create intersection with: (location: {size_environment/2,size_environment/2});
		create intersection with: (location: {size_environment / 2 + 30,size_environment/2}, is_traffic_signal: true);
		create intersection with: (location: {size_environment - 10,size_environment/2});
	
		create intersection with: (location: {size_environment/2, 10});
		create intersection with: (location: {size_environment/2,size_environment - 10});
		
		create road with:(num_lanes:1, maxspeed: 50#km/#h, shape:line([intersection[0],intersection[1]]));
		create road with:(num_lanes:1, maxspeed: 50#km/#h, shape:line([intersection[1],intersection[2]]));
		create road with:(num_lanes:1, maxspeed: 50#km/#h, shape:line([intersection[2],intersection[3]]));
		create road with:(num_lanes:1, maxspeed: 50#km/#h, shape:line([intersection[4],intersection[1]]));
		create road with:(num_lanes:1, maxspeed: 50#km/#h, shape:line([intersection[1],intersection[5]]));
		
	
		//build the graph from the roads and intersections
		road_network <- as_driving_graph(road,intersection);
		
		//for traffic light, initialize their counter value (synchronization of traffic lights)
		ask intersection where each.is_traffic_signal {
			do initialize;
		}
		
	}
	
	reflex add_car {
		create car with: (location: intersection[0].location, target: intersection[3]);
		create car with: (location: intersection[4].location, target: intersection[5]);
	}
}


//road species
species road skills: [road_skill]{
	string type;
	string oneway;
	
	aspect base_ligne {
		draw shape color: #white end_arrow:5; 
	}
	
} 

//intersection species
species intersection skills: [intersection_skill] {
	bool is_traffic_signal;
	float time_to_change <- 60#s ;
	float counter <- rnd(time_to_change);
	
	//take into consideration the roads coming from both direction (for traffic light)
	list<road> ways1;
	list<road> ways2;
	
	//if the traffic light is green
	bool is_green;
	rgb color <- #yellow;

	//initialize the traffic light
	action initialize {
		do compute_crossing;
		stop << [];
		if (flip(0.5)) {
			do to_green;
		} else {
			do to_red;
		}
	}

	action compute_crossing {
		if (length(roads_in) >= 2) {
			road rd0 <- road(roads_in[0]);
			list<point> pts <- rd0.shape.points;
			float ref_angle <- last(pts) direction_to rd0.location;
			loop rd over: roads_in {
				list<point> pts2 <- road(rd).shape.points;
				float angle_dest <- last(pts2) direction_to rd.location;
				float ang <- abs(angle_dest - ref_angle);
				if (ang > 45 and ang < 135) or (ang > 225 and ang < 315) {
					ways2 << road(rd);
				}
			}
		}

		loop rd over: roads_in {
			if not (rd in ways2) {
				ways1 << road(rd);
			}
		}
	}

	//shift the traffic light to green
	action to_green {
		stop[0] <- ways2;
		color <- #green;
		is_green <- true;
	}

	//shift the traffic light to red
	action to_red {
		stop[0] <- ways1;
		color <- #red;
		is_green <- false;
	}

	//update the state of the traffic light
	reflex dynamic_node when: is_traffic_signal {
		counter <- counter + step;
		if (counter >= time_to_change) {
			counter <- 0.0;
			if is_green {
				do to_red;
			} else {
				do to_green;
			}
		}
	}

	aspect base {
		if color != #yellow {
			draw circle(10) color: color at:{location.x, location.y+20};			
		}
	}
}


species car skills: [driving] {
	rgb color <- rnd_color(255);
	intersection target;
	
	init {
		vehicle_length <- 3.8 #m;
		//car occupies 2 lanes
		num_lanes_occupied <-1;
		max_speed <-150 #km / #h;
				
		proba_block_node <- proba_block_node_car;
		proba_respect_priorities <- 1.0;
		proba_respect_stops <- [1.0];
		proba_use_linked_road <- 0.0;

		lane_change_limit <- 2;
		linked_lane_limit <- 0;
		
	}
	//choose a random target and compute the path to it
	reflex choose_path when: final_target = nil {
		do compute_path graph: road_network target: target; 
	}
	reflex move when: final_target != nil {
		do drive;
		//if arrived at target, kill it and create a new car
		if (final_target = nil) {
			do unregister;
			do die;
		}
	}
	
	// Just use for display purpose
	// Shifts the position of the vehicle perpendicularly to the road,
	// in order to visualize different lanes
	point compute_position {
		if (current_road != nil) {
			float dist <- (road(current_road).num_lanes - current_lane -
				mean(range(num_lanes_occupied - 1)) - 0.5) * lane_width;
			if violating_oneway {
				dist <- -dist;
			}
		 	point shift_pt <- {cos(heading + 90) * dist, sin(heading + 90) * dist};	
		
			return location + shift_pt;
		} else {
			return {0, 0};
		}
	}
	
	
	aspect default {
		if (current_road != nil) {
			point pos <- compute_position();
				draw rectangle(vehicle_length*4, lane_width * num_lanes_occupied*4) 
				at: pos color: color rotate: heading border: #black;
			draw triangle(lane_width * num_lanes_occupied) 
				at: pos color: #white rotate: heading + 90 ;
		}
	}

}


experiment simple_intersection  type: gui {

	output synchronized: true {
		display city type: 3d background: #black axes: false{
			species road aspect: base_ligne;
			species intersection aspect: base;
			species car ;
		}
	}
}
