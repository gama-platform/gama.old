/**
 *  RoadTrafficComplex
 *  Author: patricktaillandier
 *  Description: 
 */
 
model RoadTrafficComplex
 
global {   
	file shape_file_roads  <- file("../includes/ManhattanRoads.shp") ;
	geometry shape <- envelope(shape_file_roads);
	int nbGoalsAchived <- 0;
	graph the_graph;  
	map<point,node> nodes_loc ;
	
	 
	init {  
		create road from: shape_file_roads with:[lanes::int(read("LANE_NB"))] {
			geom_display <- shape + (2 * lanes);
			point pt_s <- first(shape.points);
			point pt_t <- last(shape.points);
			do registerNode(pt_s,false);
			do registerNode(pt_t,true);
			maxspeed <- lanes = 1 ? 30.0 : 50.0;
		}	
		the_graph <-  directed(as_edge_graph(road)) ;
		create people number: 500 { 
			speed <- 15.0 ;
			location <- first(one_of(road).shape.points);
		}   
	}
	
} 

species node skills: [road_node]{
}
species road skills: [road] { 
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
			create node with:[location::pt] {
				nd <- self;
				nodes_loc[pt] <- self;
			}
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
	
species people skills: [driving] { 
	rgb color <- rgb(rnd(255), rnd(255), rnd(255)) ;
	point the_target <- nil ; 
	point the_final_target <- nil;
	float acceleration_max <- 5/3.6;
	path the_path <- nil;
	int index_path; 
	float vehicle_length <- 3.0;
	list<point> targets <- [];
	float speed_coeff <- 1.2 - (rnd(400) / 1000);
	
	int lane_choice (road la_route) {
		if (la_route.lanes = 1) {return 0;}
		else{
			int cv <- 0;
			int nb <- length(people);
			loop i from: 0 to: la_route.lanes - 1{
				int nb_l <- length(la_route.agents_on[i]);
				if (nb_l < nb) {
					nb <- nb_l; 
					cv <- i; 
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
		node noeud  <- node(route.source_node);
		list<people> people_on_lane <- route.agents_on[lane] - self;
		loop ag over:  people_on_lane{
			people pp <- people (ag);
			if (is_ready and ((self distance_to pp) < (2 * vehicle_length))) {
				is_ready <- false;	
			}
		}
		if (is_ready) {
			float angle_ref <- float(angle_between(noeud.location, current_road.location, route.location));
			
			loop ag over: noeud.roads_in {
				road rd <- road(ag);
				if (rd != current_road and is_ready) {
					float angle <- float(angle_between(noeud.location, current_road.location, rd.location));
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
			remaining_time <- (follow_driving_complex (path: the_path, target: the_target,speed: speed,time: remaining_time)) with_precision 2; 
			if (location = the_final_target) {the_final_target <- nil;}
			if (remaining_time > 0.0 and the_final_target != nil and (location = the_target ) ) {
				road new_road <- road(the_path.edges[index_path + 1]); 
				int lane <- lane_choice(new_road); 
				list<people> people_on_road <- new_road.agents_on[lane]; 
				if (is_ready_next_road(new_road, lane)) {
					index_path <- index_path + 1;
					road last <- (road(current_road));
					remove self from: last.agents_on[current_lane];
					ask new_road {do register(myself,0);}
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
		draw rectangle(vehicle_length, 1) at: loc depth: 1 rotate:  heading color: color;
		draw rectangle(vehicle_length / 2.0, 0.75) at: loc depth: 1.5 rotate:  heading color: color;
	} 
	
	point calcul_loc {
		if (current_road = nil) {
			return location;
		} else {
			int val <-road(current_road).lanes - current_lane ; 
			if (val = 0) {
				return location;
			} else {
				return (location + {cos(heading + 90) * val, sin(heading + 90) * val});
			}
		}
	}
	
} 

experiment experiment_2D type: gui {
	parameter "Shapefile for the roads:" var: shape_file_roads category: "GIS" ;
	
	output {
		display city_display refresh_every: 1 {
			species road aspect: base ;
			species people aspect: base;
		}
		monitor nbGoalsAchived value: nbGoalsAchived refresh_every: 1 ;
	}
}

experiment experiment_3D type: gui {
	output {
		display carte_principale type: opengl ambient_light: 100{
			species road aspect: base3D refresh: false;
			species people aspect: base3D ; 
		}
	}
}

