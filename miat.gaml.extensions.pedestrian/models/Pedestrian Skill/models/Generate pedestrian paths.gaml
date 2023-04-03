/***
* Name: generate_pedestrian_path
* Author: Patrick Taillandier
* Description: Show how to create pedestrian path and associated free space
* Tags: * Tags: pedestrian, gis, shapefile, graph, agent_movement, skill, transport
***/

model generate_pedestrian_path

global {
	
	file wall_shapefile <- file("../includes/walls.shp");
	
	
	geometry shape <- envelope(wall_shapefile);
	bool display_free_space <- false parameter: true;
	float P_shoulder_length <- 0.45 parameter: true;
	
	float simplification_dist <- 0.5; //simplification distance for the final geometries
	
	bool add_points_open_area <- true;//add points to open areas
 	bool random_densification <- false;//random densification (if true, use random points to fill open areas; if false, use uniform points), 
 	float min_dist_open_area <- 0.1;//min distance to considered an area as open area, 
 	float density_open_area <- 0.01; //density of points in the open areas (float)
 	bool clean_network <-  true; 
	float tol_cliping <- 1.0; //tolerance for the cliping in triangulation (float; distance), 
	float tol_triangulation <- 0.1; //tolerance for the triangulation 
	float min_dist_obstacles_filtering <- 0.0;// minimal distance to obstacles to keep a path (float; if 0.0, no filtering), 
	
	
	geometry open_area ;
	
	init {
		open_area <- copy(shape);
		create wall from:wall_shapefile {
			open_area <- open_area -(shape buffer (P_shoulder_length/2.0));
		}
		list<geometry> generated_lines <- generate_pedestrian_network([],[open_area],add_points_open_area,random_densification,min_dist_open_area,density_open_area,clean_network,tol_cliping,tol_triangulation,min_dist_obstacles_filtering,simplification_dist);
		
		create pedestrian_path from: generated_lines  {
			do initialize bounds:[open_area] distance: min(10.0,(wall closest_to self) distance_to self) masked_by: [wall] distance_extremity: 1.0;
		}
		save pedestrian_path to: "../includes/pedestrian paths.shp" format:"shp";
		save open_area to: "../includes/open area.shp" format:"shp";
		save pedestrian_path collect each.free_space to: "../includes/free spaces.shp" format:"shp";
	}
}

species pedestrian_path skills: [pedestrian_road]{
	rgb color <- rnd_color(255);
	aspect default {
		draw shape  color: color;
	}
	aspect free_area_aspect {
		if(display_free_space and free_space != nil) {
			draw free_space color: #cyan border: #black;
		}
	}
}

species wall {
	aspect default {
		draw shape + (P_shoulder_length/2.0) color: #gray border: #black;
	}
}

experiment normal_sim type: gui {
		output {
		display map type: 3d{
			species wall refresh: false;
			graphics "open_area" {
				draw open_area color: #lightpink;
			}
			species pedestrian_path aspect:free_area_aspect transparency: 0.5 ;
			species pedestrian_path refresh: false;
		}
	}
}
