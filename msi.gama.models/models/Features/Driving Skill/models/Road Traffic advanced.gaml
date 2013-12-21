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
	geometry shape <- envelope(shape_file_bounds);
	
	int nbGoalsAchived <- 0;
	graph the_graph;  
	map<point,node> nodes_loc ;
	int nb_people <- simple_data ? 20 : 300;
	 
	init {  
		create node from: shape_file_nodes with:[is_traffic_signal::(int(read("SIGNAL")) = 1)];
		create road from: shape_file_roads with:[lanes::int(read("LANE_NB"))] {
			shape <- polyline(reverse(shape.points));
			geom_display <- shape + (2 * lanes);
			point pt_s <- first(shape.points);
			point pt_t <- last(shape.points);
			do registerNode(pt_s,false);
			do registerNode(pt_t,true);
			maxspeed <- lanes = 1 ? 30.0 : (lanes = 2 ? 50.0 : 70.0);
		}	
		map general_speed_map <- road as_map (each::(each.shape.perimeter * (3600.0 / (each.maxspeed * 1000.0))));
		the_graph <-  directed(as_edge_graph(road))  with_weights general_speed_map;
		create people number: nb_people { 
			speed <- 30 °km /°h ;
			right_side_driving <- true;
			proba_lane_change_up <- 0.5;
			proba_lane_change_down <- 1.0;
			location <- first(one_of(node).shape.points);
		}	
	}
	
} 
species node skills: [skill_road_node] {
	bool is_traffic_signal;
	bool is_green <- flip(0.5);
	int time_to_change <- 100;
	int counter <- rnd (time_to_change) ;
	
	reflex dynamic when: is_traffic_signal {
		counter <- counter + 1;
		if (counter >= time_to_change) { 
			counter <- 0;
			is_green <- not is_green;
		} 
	}
	
	aspect base {
		if (is_traffic_signal) {	
			draw circle(5) color: is_green ? rgb("green") : rgb("red");
		}
	}
	
	aspect base3D {
		if (is_traffic_signal) {	
			draw box(2,2,10) color:rgb("black");
			draw sphere(5) at: {location.x,location.y,12} color: is_green ? rgb("green") : rgb("red");
		}
	}
}

species road skills: [skill_road] { 
	geometry geom_display;
	
	init {
		loop i from: 0 to: lanes - 1 {
			add [] to: agents_on;
		}
	}
	aspect base {    
		draw shape color: rgb("black") ;
	} 
	aspect base3D {    
		draw geom_display color: rgb("gray") ;
	} 
	
	action registerNode(point pt, bool target_pt) {
		node nd <- nodes_loc[pt];
		if (nd = nil) {
			nd <- node first_with (each.location = pt);
			nodes_loc[pt] <- nd;
		}
		
		if (target_pt) {
			nd.roads_in << self;
			target_node <- nd;
		} else {
			nd.roads_out << self;
			source_node <- nd;
		}
	}
}
	
species people skills: [advanced_driving] { 
	rgb color <- rgb(rnd(255), rnd(255), rnd(255)) ;
	point the_target <- nil ; 
	point the_final_target <- nil;
	float acceleration_max <- 5/3.6;
	path the_path <- nil;
	int index_path; 
	float vehicle_length <- 3.0;
	list<point> targets <- [];
	float speed_coeff <- 1.2 - (rnd(400) / 1000);
	
	int lane_choice (road the_road) {
		if (the_road.lanes = 1) {return 0;}
		else{
			int cv_tmp <- current_lane <= (the_road.lanes - 1)? current_lane : the_road.lanes - 1;
			int cv <- cv_tmp;
			int nb <- length(the_road.agents_on[cv_tmp]);
			loop i from: 0 to: the_road.lanes - 1{
				if (i != cv_tmp) {
					int nb_l <- length(the_road.agents_on[i]);
					if (nb_l < nb) {
						nb <- nb_l; 
						cv <- i; 
					}
				}
			}
			return cv;
		}
	}
	
	float speed_choice (road the_road) {
		float speed <- min([real_speed + acceleration_max, speed_coeff * (road(the_road).maxspeed/3.6)]);
		security_distance <- 1 + (speed * 5/9 * 3.6);  
		return speed;
	}
	
	path compute_path{
		path a_path <- nil;
		a_path <- the_graph path_between (location::the_final_target);	
		if (a_path != nil and not (empty(a_path.segments))) {
			targets <- [];
			loop edge over: a_path.edges {
				add last(agent(edge).shape.points) to: targets;
			}
			index_path <- 0;
			remove last(targets) from: targets;
			add the_final_target to: targets;
			road route <- road(a_path.edges[index_path]); 
			ask route {do register(myself,0);}
			the_target <-targets[index_path]; 
			return a_path;
		} else {
			return nil;
		}
		
	}
	
	reflex time_to_go when: the_final_target = nil {
		the_final_target <- first(one_of(road).shape.points);
		the_path <- compute_path();
		if (the_path = nil) {
			the_final_target <- nil;
		}
	}

	bool is_ready_next_road (road route, int lane) {
		bool is_ready <- true;
		node the_node  <- node(route.source_node);
		if (the_node.is_traffic_signal and not the_node.is_green) {
			is_ready <- false;
		}
		if (is_ready) {
			list<people> people_on_lane <- route.agents_on[lane] - self;
			loop ag over:  people_on_lane{
				people pp <- people (ag);
				if (is_ready and ((self distance_to pp) < (2 * vehicle_length))) {
					is_ready <- false;	
				}
			}
		
		}
		if (is_ready) {
			float angle_ref <- float(angle_between(the_node.location, current_road.location, route.location));
			
			loop ag over: the_node.roads_in {
				road rd <- road(ag);
				if (rd != current_road and is_ready) {
					float angle <- float(angle_between(the_node.location, current_road.location, rd.location));
					if (angle > angle_ref) { // right priority
						loop i from: 0 to: rd.lanes - 1 {
							loop agr over: rd.agents_on[i] {
								people pp <- people (agr);
								if (is_ready and (pp.real_speed with_precision 2 > 0.0) and ((self distance_to pp) < (security_distance + 2 * vehicle_length))) {
									is_ready <- false;
								}
							} 
						}	
					} 
				}
			}
		}
		return is_ready;
	}
	

	reflex move when: the_final_target != nil {
		float remaining_time <- 1.0;
		loop while: (remaining_time > 0.0 and the_final_target != nil) {
			float remaining_time_tmp <- remaining_time;
			speed <- speed_choice(road(current_road)); 
			remaining_time <- (advanced_follow_driving (path: the_path, target: the_target,speed: speed,time: remaining_time)) with_precision 2; 
			if (location = the_final_target) {the_final_target <- nil;}
			if (remaining_time > 0.0 and the_final_target != nil and (location = the_target ) ) {
				road new_road <- road(the_path.edges[index_path + 1]); 
				int lane <- lane_choice(new_road); 
				if (is_ready_next_road(new_road, lane)) {
					index_path <- index_path + 1;
					ask new_road {do register(myself,lane);}
					the_target <-targets[index_path]; 
				} else {remaining_time <- 0.0;}  
			}
		}  
	}
	aspect base { 
		draw triangle(8) color: color rotate:heading + 90;
	} 
	aspect base3D {
		point loc <- calcul_loc();
		draw rectangle(vehicle_length, 1.5) at: loc depth: 1 rotate:  heading color: color;
		draw rectangle(vehicle_length / 2.0, 1) at: loc depth: 1.5 rotate:  heading color: color;
	} 
	
	point calcul_loc {
		if (current_road = nil) {
			return location;
		} else {
			float val <-2.5 * (road(current_road).lanes /4 - current_lane) ; 
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
			species node aspect: base;
			species people aspect: base;
		}
	}
}

experiment experiment_3D type: gui {
	parameter "if true, simple data (simple track), if false complex one (Manhattan):" var: simple_data category: "GIS" ;
	output {
		display carte_principale type: opengl ambient_light: 100{
			species road aspect: base3D refresh: false;
			species node aspect: base3D;
			species people aspect: base3D ; 
		}
	}
}

