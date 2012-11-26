/**
 *  modelRandomTarget
 *  Author: lvminh
 *  Description: 
 */

model modelRandomTarget

global {
	file shape_file_roadlines  <- file('../includes/hanoi.bachkhoa.roadlines.shp');
	file shape_file_points  <- file('../includes/hanoi.bachkhoa.targetpoints.shp');
	file shape_file_bounds <- file('../includes/hanoi.bachkhoa.bounds.shp');
	file shape_file_busstations <- file('../includes/hanoi.bachkhoa.busstations.prj.shp');
	file shape_file_motorpoints <- file('../includes/hanoi.bachkhoa.motorpoints.shp');
	float bus_length <- 10.0;
	float bus_width <- 2.0;
	float moto_length <- 3.0; 
	float moto_width <- 1.0;
	graph the_graph;
	geometry creating_area_geometry;
	list background_species_for_bus <- [species(roadwidth), species(busstation)];
	list background_species_for_motor <- [species(roadwidth), species(motopoints)];
	list obstacle_specy_list <- [species(wheelvehicle)];
	init{
		/* coded by Patrick */
		let roads_geom type: list of: geometry <- split_lines(geometry(shape_file_roadlines).geometries);
		loop road_geom over:roads_geom {
			create roadline {
				set shape <- road_geom;
				create roadwidth {
					set shape <- myself.shape buffer 11;
				}
			}
		}
		set the_graph <- as_edge_graph (list(roadline)) ;
		create targetpoint from: shape_file_points;
		create busstation from: shape_file_busstations;
		create motopoints from: shape_file_motorpoints;
	}
	reflex create_bus when: ((length(list(wheelvehicle)) < 300) and (time mod 60 = 1)){
		create wheelvehicle number: 1{
			set location <- (first(list(busstation))).location;
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set target <- one_of(list(targetpoint));
			set speed <- 2 + rnd(6);
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_bus;
			set considering_range <- 30;
			set color <- rgb('yellow');
		}
		create wheelvehicle number: 1{
			set location <- (last(list(busstation))).location;
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set target <- one_of(list(targetpoint));
			set speed <- 2 + rnd(6);
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_bus;
			set considering_range <- 30;
			set color <- rgb('yellow');
		}
	}
	reflex create_moto when: ((length(list(wheelvehicle)) < 200) and (time mod 20 = 1)){
		create wheelvehicle number: 1{
			set location <- (first(list(motopoints))).location;
			set heading <- 270;
			set shape <- rectangle({moto_width, moto_length});
			set target <- one_of(list(targetpoint));
			set speed <- 4 + rnd(6);
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_bus;
			set considering_range <- 30;
			set color <- rgb('red');
		}
		create wheelvehicle number: 1{
			set location <- (last(list(motopoints))).location;
			set heading <- 270;
			set shape <- rectangle({moto_width, moto_length});
			set target <- one_of(list(targetpoint));
			set speed <- 4 + rnd(6);
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_bus;
			set considering_range <- 30;
			set color <- rgb('red');
		}
	}
}

environment bounds: shape_file_roadlines;

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
	species targetpoint{
		rgb color <- rgb('blue');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species motopoints{
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species busstation{
		rgb color <- rgb('gray');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	
	species wheelvehicle skills: [driving2d]{
		float length <- bus_length;
		float width <- bus_width;
		rgb color <- rgb('yellow');
		targetpoint target <- nil ;
		/* for driving skill */
		reflex move_with_vehicleGoto when: target != nil{
			let moving_status type: int <- -1;
			do vehicle_goto target: target on: the_graph speed: speed target_type: true returns: moving_status;
			if (int(moving_status) > 0){
				let temp_targets <- list(targetpoint) - target;
				set target <- one_of(temp_targets);
			}
			
		}
		reflex chose_target when: target = nil{
			set target <- one_of(list(targetpoint));
		}
		/* */
		aspect base{
			draw shape: geometry color: color z: 2;
		}
	}
}

experiment modelRandomTarget type: gui {
	output{
		/* */
		display city_display_opengl refresh_every: 1 type: opengl autosave: true{
			species roadwidth aspect: base transparency: 0.1;
			species roadline aspect: base transparency: 0.1;
			species targetpoint aspect: base;
			species busstation aspect: base transparency: 0.5;
			species wheelvehicle aspect: base;
			species motopoints aspect: base transparency: 0.5;
		}
		/* *
		display city_display_normal refresh_every: 1{
			species roadwidth aspect: base transparency: 0.1;
			species roadline aspect: base transparency: 0.1;
			species targetpoint aspect: base;
			species busstation aspect: base transparency: 0.5;
			species wheelvehicle aspect: base;
			species motopoints aspect: base transparency: 0.5;
		}
		/* */
	}
}
