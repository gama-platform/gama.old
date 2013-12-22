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
	int nb_people <- simple_data ? 20 : 500;
	 
	init {  
		create node from: shape_file_nodes with:[is_traffic_signal::(int(read("SIGNAL")) = 1)];
		ask node where each.is_traffic_signal {
			stop[0] <- flip(0.5);
		}
		create road from: shape_file_roads with:[lanes::int(read("LANE_NB"))] {
			shape <- polyline(reverse(shape.points));
			geom_display <- shape + (2 * lanes);
			maxspeed <- lanes = 1 ? 30.0 : (lanes = 2 ? 50.0 : 70.0);
		}	
		map general_speed_map <- road as_map (each::(each.shape.perimeter * (3600.0 / (each.maxspeed * 1000.0))));
		the_graph <-  (as_driving_graph(road, node))  with_weights general_speed_map;
		create people number: nb_people { 
			speed <- 30 °km /°h ;
			right_side_driving <- true;
			proba_lane_change_up <- 0.1;
			proba_lane_change_down <- 0.2;
			location <- first(one_of(node).shape.points);
			security_distance_coeff <- 5/9 * 3.6 * (1.5 - rnd(1000) / 1000);  
			proba_respect_priorities <- 1.0 - rnd(200/1000);
			proba_respect_stops <- [1.0];
		}	
	}
	
} 
species node skills: [skill_road_node] {
	bool is_traffic_signal;
	list<bool> stop <- [false];
	int time_to_change <- 100;
	int counter <- rnd (time_to_change) ;
	
	reflex dynamic when: is_traffic_signal {
		counter <- counter + 1;
		if (counter >= time_to_change) { 
			counter <- 0;
			stop[0] <- not stop[0];
		} 
	}
	
	aspect base {
		if (is_traffic_signal) {	
			draw circle(5) color: stop[0] ? rgb("red") : rgb("green");
		}
	}
	
	aspect base3D {
		if (is_traffic_signal) {	
			draw box(1,1,10) color:rgb("black");
			draw sphere(5) at: {location.x,location.y,12} color: stop[0] ? rgb("red") : rgb("green");
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
	
	float speed_choice (road the_road) {
		float speed <- min([real_speed + acceleration_max, speed_coeff * (road(the_road).maxspeed/3.6)]);
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
				if (lane >= 0) {
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
			species road aspect: base3D refresh: true;
			species node aspect: base3D;
			species people aspect: base3D ; 
		}
	}
}

