/**
 *  circulationTesting
 *  Author: lvminh
 *  Description: 
 */

model circulationTesting

global {
	file shape_file_bounds <- file('../includes/danang.bounds.shp');
	file shape_file_roadlines  <- file('../includes/danang.roadlines.shp');
	file shape_file_creatingareas <- file('../includes/danang.creatingareas.shp');
	file shape_file_points  <- file('../includes/danang.targetpoints.shp');
	float bus_length <- 12.0;
	float bus_width <- 2.5;
	float moto_length <- 3.0; 
	float moto_width <- 1;
	graph the_graph;
	geometry creating_area_geometry;
	list background_species <- [species(roadwidth), species(creatingarea)];
	init{
		create roadline from: shape_file_roadlines;
		set the_graph <- as_edge_graph(list(roadline));
		loop rd over: roadline as list {
			create roadwidth {
				set shape <- rd.shape buffer 10;
			}	
		}
		create creatingarea from: shape_file_creatingareas;
		create tagerpoint from: shape_file_points;
		set creating_area_geometry <- (list(creatingarea) at 0);
		create wheelvehicle number: 1{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set target <- first(list(tagerpoint));
			set speed <- 2 + rnd(6);
			set obstacle_species <- [species(self)];
			set background_species <- background_species;
			set considering_range <- 30;
			set color <- rgb('yellow');
			set creating_area_geometry <- creating_area_geometry - (shape buffer 2);
		}
	}
}

environment bounds: shape_file_bounds;

entities {
	species roadline{
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species roadwidth{
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species creatingarea{
		rgb color <- rgb('blue');
		string zone_name <- "";
		int priority <- 0;
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species tagerpoint{
		rgb color <- rgb('red');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species wheelvehicle skills: [driving]{
		float length <- bus_length;
		float width <- bus_width;
		rgb color <- rgb('yellow');
		point target <- nil ;
		/* for driving skill */
		reflex move_with_vehicleGoto when: target != nil{
			do vehicleGoto target: target on: the_graph speed: speed;
			/* *
			switch target { 
				match location {
					let temp_targets <- list(targetpoint) - target;
					set target <- one_of(temp_targets);
				}
			}
			/* */
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
}

experiment circulationTesting type: gui {
	output{
		display city_display_with_opengl refresh_every: 1 type: opengl{
			species roadline aspect: base transparency: 0.5;
			species creatingarea aspect: base transparency: 0.5;
			species tagerpoint aspect: base;
			species wheelvehicle aspect: base;
		}
		display city_display_normal refresh_every: 1{
			species roadline aspect: base transparency: 0.5;
			species creatingarea aspect: base transparency: 0.5;
			species tagerpoint aspect: base;
			species wheelvehicle aspect: base;
			/* *
			species roadwidth aspect: base transparency: 0.5;
			
			
			
			species wheelvehicle aspect: base;
			species motopoints aspect: base transparency: 0.5;
			/* */
		}
	}
}
