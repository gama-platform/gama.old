/***
* Name: pedestrian_simple_environment
* Author: Patrick Taillandier
* Description: 
* Tags: pedestrian, agent_movement, skill, transport
***/

model pedestrian_simple_environment

global {
	float environment_size <- 50.0 parameter: true;
	float margin <- 2.0;
	int nb_obstacles <- 0 parameter: true;
	int nb_people <- 100;
	string scenario <- "frontal crossing" among: ["big crowd", "frontal crossing", "perpendicular crossing"] ;
		
	bool display_free_space <- false parameter: true;
	bool display_force <- false parameter: true;
	bool display_circle_min_dist <- true parameter: true;
	
	float P_shoulder_length <- 0.45 parameter: true;
	float P_proba_detour <- 1.0 parameter: true ;
	bool P_avoid_other <- true parameter: true ;
	float P_obstacle_consideration_distance <- 5.0 parameter: true ;
	float P_pedestrian_consideration_distance <- 5.0 parameter: true ;
	float P_tolerance_waypoint <- 0.1 parameter: true;
	bool P_use_geometry_waypoint <- true parameter: true;
	
	string P_model_type <- "simple" among: ["simple", "advanced"] parameter: true ; 
	
	float P_A_pedestrian_SFM_advanced parameter: true <- 25.0 category: "SFM advanced" ;
	float P_A_obstacles_SFM_advanced parameter: true <- 25.0 category: "SFM advanced" ;
	float P_B_pedestrian_SFM_advanced parameter: true <- 0.5 category: "SFM advanced" ;
	float P_B_obstacles_SFM_advanced parameter: true <- 0.1 category: "SFM advanced" ;
	float P_relaxion_SFM_advanced  parameter: true <- 0.1 category: "SFM advanced" ;
	float P_gama_SFM_advanced parameter: true <- 0.35 category: "SFM advanced" ;
	float P_lambda_SFM_advanced <- 0.1 parameter: true category: "SFM advanced" ;
	float P_minimal_distance_advanced <- 0.5 parameter: true category: "SFM advanced" ;
	
	
	float P_n_prime_SFM_simple parameter: true <- 3.0 category: "SFM simple" ;
	float P_n_SFM_simple parameter: true <- 2.0 category: "SFM simple" ;
	float P_lambda_SFM_simple <- 2.0 parameter: true category: "SFM simple" ;
	float P_gama_SFM_simple parameter: true <- 0.35 category: "SFM simple" ;
	float P_relaxion_SFM_simple parameter: true <- 0.54 category: "SFM simple" ;
	float P_A_pedestrian_SFM_simple parameter: true <- 4.5category: "SFM simple" ;
	
	geometry shape <- square(environment_size);
	geometry free_space <- copy(shape);
	geometry left_space;
	geometry right_space;
	geometry bottom_space; 
	geometry top_space;
	
	float step <- 0.1;
	
	init {
		left_space <- polygon([{0,0}, {0, environment_size}, {environment_size/10, environment_size}, {environment_size/10,0}]);
		right_space <- polygon([{environment_size,0}, {environment_size, environment_size}, {9 * environment_size/10, environment_size}, {9 * environment_size/10,0}]);
		bottom_space <- polygon([{0, environment_size}, {0, 9 * environment_size/10}, {environment_size,9*  environment_size/10}, {environment_size, environment_size}]);	
		top_space <- polygon([{0, 0}, {0, environment_size/10}, {environment_size, environment_size/10}, {environment_size, 0.0}]);	
		
		create obstacle number:nb_obstacles {
			location <- any_location_in(square(8*environment_size/10) at_location {environment_size/2, environment_size/2} scaled_by 0.8);
			shape <- sphere(1+rnd(environment_size/50.0));
			free_space <- free_space - shape;
		}
		
		create people number: nb_people {
			obstacle_consideration_distance <- P_obstacle_consideration_distance;
			obstacle_consideration_distance <- P_obstacle_consideration_distance;
			pedestrian_consideration_distance <- P_pedestrian_consideration_distance;
			shoulder_length <- P_shoulder_length;
			avoid_other <- P_avoid_other;
			proba_detour <- P_proba_detour;
			use_geometry_waypoint <- P_use_geometry_waypoint;
			tolerance_waypoint <- P_tolerance_waypoint;
			
			pedestrian_model <- P_model_type;
			if (pedestrian_model = "simple") {
				A_pedestrians_SFM <- P_A_pedestrian_SFM_simple;
				relaxion_SFM <- P_relaxion_SFM_simple;
				gama_SFM <- P_gama_SFM_simple;
				lambda_SFM <- P_lambda_SFM_simple;
				n_prime_SFM <- P_n_prime_SFM_simple;
				n_SFM <- P_n_SFM_simple;
			} else {
				A_pedestrians_SFM <- P_A_pedestrian_SFM_advanced;
				A_obstacles_SFM <- P_A_obstacles_SFM_advanced;
				B_pedestrians_SFM <- P_B_pedestrian_SFM_advanced;
				B_obstacles_SFM <- P_B_obstacles_SFM_advanced;
				relaxion_SFM <- P_relaxion_SFM_advanced;
				gama_SFM <- P_gama_SFM_advanced;
				lambda_SFM <- P_lambda_SFM_advanced;
				minimal_distance <- P_minimal_distance_advanced;
			}
			
			pedestrian_species <- [people];
			obstacle_species<-[obstacle];
			switch scenario {
				match "frontal crossing" {
					int id <- int(self);
					location <- any_location_in(even(id) ? left_space : right_space);
					current_target <- closest_points_with(location, even(id) ? right_space : left_space)[1];
				} match "perpendicular crossing" {
					int id <- int(self);
					location <- any_location_in(even(id) ? left_space : bottom_space);
					current_target <- closest_points_with(location, (even(id) ? right_space : top_space))[1];
				} match "big crowd" {
					location <- any_location_in(free_space);
					current_target <- any_location_in(world.shape.contour);
				}
			}
		}
	}
	
	reflex end_simulation when: empty(people) {
		do pause;
	}
}

species people skills: [pedestrian] schedules: shuffle(people) {
	rgb color <- rnd_color(255);
	float speed <- 3 #km/#h;
	bool avoid_other <- true;
	point current_target ;
	reflex move when: current_target != nil{
		if (nb_obstacles > 0) {
			do walk_to target: current_target bounds: free_space;
		} else {
			do walk_to target: current_target;
		}
		if (self distance_to current_target < 0.5) {
			do die;
		}
	}
	aspect default {
		if (display_circle_min_dist and minimal_distance > 0) {
				if not empty(people at_distance minimal_distance) {
					draw circle(minimal_distance) color: #red;
				}
				
		}
		draw triangle(shoulder_length) color: color rotate: heading + 90.0;
		
		if  display_force {
		
			loop op over: forces.keys {
				if (species(agent(op)) = obstacle ) {
					draw line([location, location + point(forces[op])]) color: #red end_arrow: 0.1;
				}
				else if ((agent(op)) = self ) {
					draw line([location, location + point(forces[op])]) color: #blue end_arrow: 0.1;
				} 
				else {
					draw line([location, location + point(forces[op])]) color: #green end_arrow: 0.1;
				}
			}
		}	
	}
}

species obstacle {
	aspect default {
		draw shape color: #gray border: #black;
	}
}
experiment big_crowd type: gui {
	float minimum_cycle_duration <- 0.02;
	action _init_ {
		create simulation with: [scenario :: "big crowd", nb_people::500];
	}
	output {
		display map  {
			species obstacle;
			species people;
		}
	}
}

experiment frontal_crossing type: gui {
	float minimum_cycle_duration <- 0.02;
	action _init_ {
		create simulation with: [scenario :: "frontal crossing", nb_people::100];
	}
	output {
		display map  {
			graphics "areas" transparency: 0.5{
				draw right_space color: #green border: #black;
				draw left_space color: #red border: #black;
			}
			species obstacle;
			species people;
		}
	}
}
experiment perpendicular_crossing type: gui {
	float minimum_cycle_duration <- 0.02;
	action _init_ {
		create simulation with: [scenario :: "perpendicular crossing", nb_people::100];
	}
	
	output {
		display map  {
			graphics "areas" transparency: 0.7{
				draw right_space color: #green border: #black;
				draw left_space color: #red border: #black;
				draw bottom_space color: #yellow border: #black;
				draw top_space color: #magenta border: #black;
			}
			species obstacle;
			species people;
		}
	}
}
