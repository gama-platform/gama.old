/**
* Name: Traffic
* Description: define species for traffic simulation
* Author: Duc Pham
* Tags: driving skill
*/

model traffic

global {
	// This is for visualization purposes only, 
	// the width of a vehicle in specified using num_lanes_occupied
	float lane_width <- 0.7;  
}

species road skills: [skill_road] {
	rgb color <- #white;
	string oneway;

	aspect base {
		draw shape color: color;
	}
}

species intersection skills: [skill_road_node] {
	bool is_traffic_signal;
	float time_to_change <- 30#s;
	float counter <- rnd(time_to_change);
	list<road> ways1;
	list<road> ways2;
	bool is_green;
	rgb color_fire;

	action initialize {
		if (is_traffic_signal) {
			do compute_crossing;
			stop << [];
			if (flip(0.5)) {
				do to_green;
			} else {
				do to_red;
			}
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

	action to_green {
		stop[0] <- ways2;
		color_fire <- #green;
		is_green <- true;
	}

	action to_red {
		stop[0] <- ways1;
		color_fire <- #red;
		is_green <- false;
	}

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
		if (is_traffic_signal) {
			draw circle(1) color: color_fire;
		} else {
			draw circle(1) color: #black;
		}
	}
}

species base_vehicle skills: [advanced_driving] {
	rgb color <- rnd_color(255);
	graph road_graph;
	
	point compute_position {
		if (current_road != nil) {
			// The distance to shift the vehicle perpendicularly to the road
			float dist <- (road(current_road).lanes - current_lane -
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
	
	aspect base {
		if (current_road != nil) {
			point pos <- compute_position();
				
			draw rectangle(vehicle_length, lane_width * num_lanes_occupied) 
				at: pos color: color rotate: heading border: #black;
			draw triangle(lane_width * num_lanes_occupied) 
				at: pos color: #white rotate: heading + 90 border: #black;
		}
	}
}

species vehicle_random parent: base_vehicle {
	intersection init_node;

	reflex commute {
		do drive_random graph: road_graph;
	}
}

species motorbike_random parent: vehicle_random {
	init {
		vehicle_length <- 1.9 #m;
		num_lanes_occupied <- 1;
		max_speed <- (50 + rnd(20)) #km / #h;
	}
}

species car_random parent: vehicle_random {
	init {
		vehicle_length <- 3.8 #m;
		num_lanes_occupied <- 2;
		max_speed <- (60 + rnd(10)) #km / #h;
	}
}
