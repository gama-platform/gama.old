/**
 *  EastSideScenario1a
 *  Author: lvminh
 *  Description: 
 */

model evacDanang2

global {
	file shape_file_roadlines  <- file('../includes/danang.eastside.roadlines.shp');
	file shape_file_places  <- file('../includes/danang.eastside.highgrounds.shp');
	file shape_file_busstations <- file('../includes/danang.eastside.busstations.shp');
	file shape_file_beaches <- file('../includes/danang.eastside.beaches.shp');
	file shape_file_busstops <- file('../includes/danang.eastside.busstops.shp');
	file shape_file_buildings <- file('../includes/danang.eastside.buildings.shp');
	const my_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')];
	float bus_length <- 12.0;
	float bus_width <- 2.5;
	float moto_length <- 3.0; 
	float moto_width <- 1;
	graph the_graph;
	int eastern_beach_population <- 1;
	int northern_beach_population <- 1;
	int number_of_bus_at_eastern_station <- 1;
	int number_of_bus_at_northern_station <- 1;
	geometry creating_area_geometry;
	list obstacle_specy_list <- [species(wheelvehicle), species(pedestrian)];
	list background_sepcies_for_pedestrian <- [species(roadwidth), species(busstation), species(beach), species(building)];
	list background_species_for_vehicle <- [species(roadwidth), species(busstation)];
	init{
		let roads_geom type: list of: geometry <- split_lines(geometry(shape_file_roadlines).geometries);
		loop road_geom over:roads_geom {
			create roadline {
				set shape <- road_geom;
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
			set available <- capacity;
			set shape <- shape buffer 5;
		}
		create busstop from: shape_file_busstops with: [zone_name:: read('zoneName'), priority:: read('priority')];
		set creating_area_geometry <- (list(beach) at 0);
		create pedestrian number: eastern_beach_population{
			//set location <- any_location_in(creating_area_geometry);
			set location <- (list(busstop) at 0).location;
			set heading <- 0;
			set shape <- circle(size);
			set target <- nil;
			set speed <- 2;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_sepcies_for_pedestrian;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - (shape buffer 2);
		}
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
		int available <- 0;
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
			do vehicleGoto target: target on: the_graph speed: speed;
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
	species pedestrian skills: [driving]{
		point target <- nil;
		float size<- 1;
		rgb color <- rgb('yellow');
		list shelters of: building <- (building as list);
		
		reflex select_target when: (target = nil){
			let available_shelters type: list of: building <- ((list(shelters) where (each.available > 0)) sort_by (self distance_to each));
			if ((available_shelters != nil) and (length(available_shelters)>0)){
				set target <- first(available_shelters).location;
				do write message: "shelter location" + target;	
			}
			else{
				do write message: "can not find shelter!";
			}
		}
		/* for driving skill */
		reflex move_with_vehicleGoto when: (target != nil){
			let moving_status value: -1;
			do vehicleGoto target: target on: the_graph speed: speed returns: moving_status;
			if (moving_status = 2){
				do write message: "arrived target";
			}
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
}

experiment EastSideScenario1a type: gui {
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
