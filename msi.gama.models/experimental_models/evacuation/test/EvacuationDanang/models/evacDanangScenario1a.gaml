/**
 *  evacDanangScenario1a
 *  Author: lvminh
 *  Description: All are pedestrians. They are initially at the beaches. They all know everything about the shelter (location, availability).  
 */

model evacDanangScenario1a

global {
	file shape_file_roadlines  <- file('../includes/danang.roadlines.shp');
	file shape_file_places  <- file('../includes/danang.highgrounds.shp');
	file shape_file_busstations <- file('../includes/danang.busstations.shp');
	file shape_file_beaches <- file('../includes/danang.beaches.shp');
	file shape_file_busstops <- file('../includes/danang.busstop.shp');
	file shape_file_buildings <- file('../includes/danang.buidings.shp');
	const my_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')];
	float pedestrian_max_speed <- 5.4 min: 0.1 parameter: 'Max speed (m per s)' category: 'Pedestrian';
	float local_density_range <- 5.0 min: 2.0 max: 10.0 parameter: 'Density for considering range' category: 'Pedestrian';
	float max_density <- 5.0 min: 1.0 max: 10.0 parameter: 'Max density (people per m2)' category: 'Pedestrian';
	float bus_length <- 12.0;
	float bus_width <- 2.5;
	float moto_length <- 3.0; 
	float moto_width <- 1;
	graph the_graph;
	int eastern_beach_population <- 1000;
	int northern_beach_population <- 1000;
	int number_of_bus_at_eastern_station <- 1;
	int number_of_bus_at_northern_station <- 1;
	int last_arrival_time <- 0;
	int number_of_survival <- 0;
	geometry creating_area_geometry;
	list obstacle_specy_list <- [species(wheelvehicle), species(pedestrian)];
	list background_sepcies_for_pedestrian <- [species(roadwidth), species(busstation), species(beach), species(building)];
	list background_species_for_vehicle <- [species(roadwidth), species(busstation)];
	init{
		let roads_geom type: list of: geometry <- split_lines(geometry(shape_file_roadlines).geometries);
		loop road_geom over:roads_geom {
			create roadline {
				set shape <- road_geom;
				set width <- 10;
				create roadwidth {
					set shape <- myself.shape buffer 10;
				}
			}
		}
		set the_graph <- as_edge_graph (list(roadline));
		create highground from: shape_file_places;
		create busstation from: shape_file_busstations;
		create beach from: shape_file_beaches;
		create building from: shape_file_buildings{
			set capacity <- 100;
			set availability <- capacity;
			set shape <- shape buffer 5;
		}
		create busstop from: shape_file_busstops with: [zone_name:: read('zoneName'), priority:: read('priority')];
		set creating_area_geometry <- (list(beach) at 0);
		create pedestrian number: eastern_beach_population{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 0;
			set shape <- circle(size);
			set target <- nil;
			set speed <- pedestrian_max_speed;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_sepcies_for_pedestrian;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - (shape buffer 2);
		}
		set creating_area_geometry <- (list(beach) at 1);
		create pedestrian number: northern_beach_population{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 0;
			set shape <- circle(size);
			set target <- nil;
			set speed <- pedestrian_max_speed;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_sepcies_for_pedestrian;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - (shape buffer 2);
		}
		/* *
		set creating_area_geometry <- first(list(busstation));
		create wheelvehicle number: number_of_bus_at_eastern_station{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set target <- first(list(busstop));
			set speed <- 7;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_vehicle;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - shape;
		}
		/* */
	}
	reflex stop_simulation when: (length(pedestrian) = 0){
		do write message: "simulation stops at " + time;
		do halt;
   }
}

environment bounds: shape_file_roadlines;

entities {
	species roadline{
		float width;
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
	species highground{
		string location_name;
		rgb color <- rgb('blue');
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
	species busstop{
		rgb color <- rgb('red');
		string zone_name <- "";
		int priority <- 0;
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species beach{
		rgb color <- rgb([120, 120, 255]);
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species building{
		int capacity <- 0;
		int availability <- 0;
		rgb color <- rgb('orange');
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
		reflex move_with_vehicleGoto when: (target != nil){
			//do vehicleGoto target: target on: the_graph speed: speed type_target: false;
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
	species pedestrian skills: [driving]{
		building target <- nil;
		float size <- 1.0;
		rgb color <- rgb('yellow');
		float local_density <- 0.0;
		list shelters of: building <- (building as list);
		float local_density_window_area <- local_density_range*local_density_range*3.14;
		action compute_speed type: float { //coded by Vo Ducan
			/*
			 * 30%: 10961ms 4.12m/s
			 * 
			 * 100%: 60415ms 2.44m/s 192pedestrians 
			 */
			let obstacle_pedestrians type: int <- 1 + length(pedestrian at_distance local_density_range);

			/*
			 * 100% 67309ms 2.3m/s 191pedestrians
			 */

			if (obstacle_pedestrians = 0) { 
				set local_density <- 0;
				return pedestrian_max_speed;
			}
			
			set local_density <- (float(obstacle_pedestrians)) / local_density_window_area;
			
			if (local_density >= max_density) {  return 0; }
			
			else {return pedestrian_max_speed * ( 1 - (local_density / max_density) );}
		}
		reflex select_target when: ((target = nil) or ((target != nil) and (target.availability = 0))){
			let available_shelters type: list of: building <- ((list(shelters) where (each.availability > 0)) sort_by (self distance_to each));
			if ((available_shelters != nil) and (length(available_shelters)>0)){
				set target <- first(available_shelters);	
				//do write message: "shelter location" + target;	
			}
			else{
				do write message: "can not find shelter!";
			}
		}
		/* for driving skill */
		reflex move_with_vehicleGoto when: (target != nil){
			set speed <- self compute_speed [];
			let moving_status type: int <- -1;
			do vehicleGoto target: target.location on: the_graph speed: speed target_type: false returns: moving_status;
			if (int(moving_status) > 0){
				do write message: "arrived target";
				ask target {
					set availability <- (availability - 1);
					//do write message: "new availability: " + availability;
				}
				set last_arrival_time <- time;
				set number_of_survival <- (number_of_survival + 1);
				do write message: "at: " + last_arrival_time + ", " + number_of_survival + " arrive shelters";
				do die;
			}
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
}

experiment evacDanangScenario1a type: gui {
	output{
		display city_display refresh_every: 1{
			species roadwidth aspect: base;
			species roadline aspect: base;
			species highground aspect: base;
			species busstation aspect: base;
			species beach aspect: base transparency: 0.02;
			species pedestrian aspect: base;
			species wheelvehicle aspect: base;
			species busstop aspect: base transparency: 0.02;
			species building aspect: base;
		}
		display city_display_with_opengl refresh_every: 1 type: opengl{
			species roadwidth aspect: base;
			species roadline aspect: base;
			species highground aspect: base;
			species busstation aspect: base;
			species beach aspect: base transparency: 0.02;
			species pedestrian aspect: base;
			species wheelvehicle aspect: base;
			species busstop aspect: base transparency: 0.02;
			species building aspect: base;
		}
	}
}
