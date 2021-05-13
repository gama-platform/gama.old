/***
* Name: pedestrian_complex_environment
* Author: Patrick Taillandier
* Description: show how to use the pedestrian skill for complex envorinment - require to generate pedestrian paths before - see model "Generate Pedestrian path.gaml" 
* Tags: pedestrian, gis, shapefile, graph, agent_movement, skill, transport
***/

model pedestrian_complex_environment

global {
	
	file wall_shapefile <- file("../includes/walls.shp");
	
	shape_file free_spaces_shape_file <- shape_file("../includes/free spaces.shp");
	shape_file open_area_shape_file <- shape_file("../includes/open area.shp");
	shape_file pedestrian_paths_shape_file <- shape_file("../includes/pedestrian paths.shp");

	
	graph network;
	
	geometry shape <- envelope(wall_shapefile);
	
	bool display_free_space <- false parameter: true;
	bool display_force <- false parameter: true;
	bool display_target <- false parameter: true;
	bool display_circle_min_dist <- true parameter: true;
	
	float P_shoulder_length <- 0.45 parameter: true;
	float P_proba_detour <- 0.5 parameter: true ;
	bool P_avoid_other <- true parameter: true ;
	float P_obstacle_consideration_distance <- 3.0 parameter: true ;
	float P_pedestrian_consideration_distance <- 3.0 parameter: true ;
	float P_minimal_distance <- 0.0 parameter: true;
	float P_tolerance_target <- 0.1 parameter: true;
	bool P_use_geometry_target <- true parameter: true;
	
	float P_A_pedestrian_SFM parameter: true <- 0.16 category: "SFM" ;
	float P_A_obstacles_SFM parameter: true <- 1.9 category: "SFM" ;
	float P_B_pedestrian_SFM parameter: true <- 0.1 category: "SFM" ;
	float P_B_obstacles_SFM parameter: true <- 1.0 category: "SFM" ;
	float P_relaxion_SFM parameter: true <- 0.5 category: "SFM" ;
	float P_gama_SFM parameter: true <- 0.35 category: "SFM" ;
	float P_lambda_SFM <- 0.1 parameter: true category: "SFM" ;
	
	
	float step <- 0.1;
	int nb_people <- 100;

	geometry open_area ;
	
	init {
		open_area <- first(open_area_shape_file.contents);
		create wall from:wall_shapefile;
		create pedestrian_path from: pedestrian_paths_shape_file {
			list<geometry> fs <- free_spaces_shape_file overlapping self;
			free_space <- fs first_with (each covers shape); 
		}
		

		network <- as_edge_graph(pedestrian_path);
		
		ask pedestrian_path {
			do build_intersection_areas pedestrian_graph: network;
		}
	
		create people number:nb_people{
			location <- any_location_in(one_of(open_area));
			obstacle_consideration_distance <-P_obstacle_consideration_distance;
			pedestrian_consideration_distance <-P_pedestrian_consideration_distance;
			shoulder_length <- P_shoulder_length;
			avoid_other <- P_avoid_other;
			proba_detour <- P_proba_detour;
			minimal_distance <- P_minimal_distance;
			A_pedestrians_SFM <- P_A_pedestrian_SFM;
			A_obstacles_SFM <- P_A_obstacles_SFM;
			B_pedestrians_SFM <- P_B_pedestrian_SFM;
			B_obstacles_SFM <- P_B_obstacles_SFM;
			relaxion_SFM <- P_relaxion_SFM;
			gama_SFM <- P_gama_SFM;
			lambda_SFM <- P_lambda_SFM;
			use_geometry_target <- P_use_geometry_target;
			tolerance_target <- P_tolerance_target;
			
			pedestrian_species <- [people];
			obstacle_species<-[wall];
			
		}	
	}
	
	reflex stop when: empty(people) {
		do pause;
	}
	
}

species pedestrian_path skills: [pedestrian_road]{
	aspect default {
		draw shape  color: #gray;
	}
	aspect free_area_aspect {
		if(display_free_space and free_space != nil) {
			draw free_space color: #lightpink border: #black;
		}
		
	}
}

species wall {
	geometry free_space;
	float high <- rnd(10.0, 20.0);
	
	aspect demo {
		draw shape border: #black depth: high texture: ["../includes/top.png","../includes/texture5.jpg"];
	}
	
	aspect default {
		draw shape + (P_shoulder_length/2.0) color: #gray border: #black;
	}
}

species people skills: [pedestrian]{
	rgb color <- rnd_color(255);
	float speed <- gauss(5,1.5) #km/#h min: 2 #km/#h;

	reflex move  {
		if (final_target = nil) {
			do compute_virtual_path pedestrian_graph:network final_target: any_location_in(open_area) ;
		}
		do walk ;
	}	
	
	aspect default {
		
		if display_circle_min_dist and minimal_distance > 0 {
			draw circle(minimal_distance).contour color: color;
		}
		
		draw triangle(shoulder_length) color: color rotate: heading + 90.0;
		
		if display_target and current_target != nil {
			draw line([location,current_target]) color: color;
		}
		if  display_force {
			loop op over: forces.keys {
				if (species(agent(op)) = wall ) {
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


experiment normal_sim type: gui {
	float minimum_cycle_duration <- 0.05;
		output {
		display map type: opengl{
			species wall refresh: false;
			species pedestrian_path aspect:free_area_aspect transparency: 0.5 ;
			species pedestrian_path refresh: false;
			species people;
		}
	}
}
