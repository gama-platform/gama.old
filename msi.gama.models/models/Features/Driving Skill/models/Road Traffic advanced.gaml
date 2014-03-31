/**
 *  RoadTrafficComplex
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficComplex
 
global {   
	bool simple_data <- false;
	file shape_file_roads  <- simple_data ? file("../includes/RoadCircleLanes.shp"): file("../includes/ManhattanRoads.shp") ;
	file shape_file_nodes  <- simple_data ? file("../includes/NodeCircleLanes.shp") : file("../includes/ManhattanNodes.shp");
	file shape_file_bounds <- simple_data ? file("../includes/BoundsLaneRoad.shp") :file("../includes/ManhattanRoads.shp");
	geometry shape <- envelope(shape_file_bounds) + 50.0;
	
	graph road_network;  
	int nb_people <- simple_data ? 20 : 500;
	 
	init {  
		create intersection from: shape_file_nodes with:[is_traffic_signal::(read("type") = "traffic_signals")];
		create road from: shape_file_roads with:[lanes::int(read("lanes")), oneway::string(read("oneway"))] {
			geom_display <- shape + (2.5 * lanes);
			maxspeed <- (lanes = 1 ? 30.0 : (lanes = 2 ? 50.0 : 70.0)) °km/°h;
			switch oneway {
				match "no" {
					create road {
						lanes <- max([1, int (myself.lanes / 2.0)]);
						shape <- polyline(reverse(myself.shape.points));
						maxspeed <- myself.maxspeed;
						geom_display  <- myself.geom_display;
						linked_road <- myself;
						myself.linked_road <- self;
					}
					lanes <- int(lanes /2.0 + 0.5);
				}
				match "-1" {
					shape <- polyline(reverse(shape.points));
				}	
			}
		}	
		map general_speed_map <- road as_map (each::(each.shape.perimeter / each.maxspeed));
		road_network <-  (as_driving_graph(road, intersection))  with_weights general_speed_map;
		create people number: nb_people { 
			max_speed <- 160 °km/°h;
			vehicle_length <- 5.0 °m;
			right_side_driving <- true;
			proba_lane_change_up <- 0.1 + (rnd(500) / 500);
			proba_lane_change_down <- 0.5+ (rnd(500) / 500);
			location <- one_of(intersection where empty(each.stop)).location;
			security_distance_coeff <- 5/9 * 3.6 * (1.5 - rnd(1000) / 1000);  
			proba_respect_priorities <- 1.0 - rnd(200/1000);
			proba_respect_stops <- [1.0];
			proba_block_node <- 0.0;
			proba_use_linked_road <- 0.0;
			max_acceleration <- 5/3.6;
			speed_coeff <- 1.2 - (rnd(400) / 1000);
			threshold_stucked <-int ( (1 + rnd(5))°mn);
			proba_breakdown <- 0.00001;
			
		}	
	}
	
} 
species intersection skills: [skill_road_node] {
	bool is_traffic_signal;
	list<list> stop <- [];
	int time_to_change <- 100;
	int counter <- rnd (time_to_change) ;
	list<road> ways1;
	list<road> ways2;
	bool is_green;
	rgb color_fire;
	
	init {
		if (is_traffic_signal) {
			do compute_crossing;
			stop<< [];
			if (flip(0.5)) {
				do to_green;
			} else {
				do to_red;
			}	
		}
	}
	
	action compute_crossing{
		if  (length(roads_in) >= 2) {
			road rd0 <- road(roads_in[0]);
			list<point> pts <- rd0.shape.points;						
			float ref_angle <-  float( last(pts) direction_to rd0.location);
			loop rd over: roads_in {
				list<point> pts2 <- road(rd).shape.points;						
				float angle_dest <-  float( last(pts2) direction_to rd.location);
				float ang <- abs(angle_dest - ref_angle);
				if (ang > 45 and ang < 135) or  (ang > 225 and ang < 315) {
					ways2<< road(rd);
				}
			}
		}
		loop rd over: roads_in {
			if not(rd in ways2) {
				ways1 << road(rd);
			}
		}
	}
	
	action to_green {
		stop[0] <- ways2 ;
		color_fire <- rgb("green");
		is_green <- true;
	}
	
	action to_red {
		stop[0] <- ways1;
		color_fire <- rgb("red");
		is_green <- false;
	}
	reflex dynamic_node when: is_traffic_signal  {
		counter <- counter + 1;
		if (counter >= time_to_change) { 
			counter <- 0;
			if is_green {do to_red;}
			else {do to_green;}
		} 
	}
	
	aspect base {
		if (is_traffic_signal) {	
			draw circle(5) color: color_fire;
		}
	}
	
	aspect base3D {
		if (is_traffic_signal) {	
			draw box(1,1,10) color:rgb("black");
			draw sphere(5) at: {location.x,location.y,12} color: color_fire;
		}
	}
}

species road skills: [skill_road] { 
	geometry geom_display;
	string oneway;
	aspect base {    
		draw shape color: rgb("gray") end_arrow: 10;
	} 
	aspect base3D {    
		draw geom_display color: rgb("gray") ;
	} 
}
	
species people skills: [advanced_driving] { 
	rgb color <- rgb(rnd(255), rnd(255), rnd(255)) ;
	int counter_stucked <- 0;
	int threshold_stucked;
	bool breakdown <- false;
	float proba_breakdown ;
	intersection target;
	
	reflex breakdown when: flip(proba_breakdown){
		breakdown <- true;
		max_speed <- 1 °km/°h;
	}
	
	reflex time_to_go when: final_target = nil {
		target <- one_of(intersection where not each.is_traffic_signal);
		current_path <- compute_path(graph: road_network, target: target );
		if (current_path = nil ) {
			final_target <- nil;
		}
	}
	reflex move when: current_path != nil and final_target != nil {
		do drive;
		if real_speed < 5°km/°h {
			counter_stucked<- counter_stucked + 1;
			if (counter_stucked mod threshold_stucked = 0) {
				proba_use_linked_road <- min([1.0,proba_use_linked_road + 0.1]);
			}
		} else {
			counter_stucked<- 0;
			proba_use_linked_road <- 0.0;
		}
	}

	aspect base { 
		draw breakdown ? square(15) : triangle(15) color: color rotate:heading + 90;
	} 
	aspect base3D {
		point loc <- calcul_loc();
		draw box(vehicle_length, 1,1) at: loc rotate:  heading color: color;
		
		draw triangle(0.5) depth: 1.5 at: loc rotate:  heading + 90 color: color;
		
		if (breakdown) {
			draw circle(2) at: loc color: rgb("red");
		}
	} 
	
	point calcul_loc {
		if (current_road = nil) {
			return location;
		} else {
			float val <- (road(current_road).lanes - current_lane) + 0.5;
			val <- on_linked_road ? val * - 1 : val;
			if (val = 0) {
				return location; 
			} else {
				return (location + {cos(heading + 90) * val, sin(heading + 90) * val});
			}
		}
	}
	
} 

experiment experiment_2D type: gui {
	parameter "if true, simple data (simple track), if false complex one (Manhattan):" var: simple_data category: "GIS" ;
	output {
		display city_display refresh_every: 1 {
			species road aspect: base ;
			species intersection aspect: base;
			species people aspect: base;
		}
	}
}

experiment experiment_3D type: gui {
	parameter "if true, simple data (simple track), if false complex one (Manhattan):" var: simple_data category: "GIS" ;
	output {
		display carte_principale type: opengl ambient_light: 100{
			species road aspect: base3D refresh: true;
			species intersection aspect: base3D;
			species people aspect: base3D ; 
		}
	}
}

