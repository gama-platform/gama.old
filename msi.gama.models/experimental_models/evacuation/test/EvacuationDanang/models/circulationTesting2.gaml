/**
 *  evacDanang1
 *  Author: lvminh
 *  Description: 
 */

model evacDanang1

global {
	file shape_file_roadlines  <- file('../includes/danang.roadlines.shp');
	file shape_file_points  <- file('../includes/danang.points.shp');
	file shape_file_places  <- file('../includes/danang.places.shp');
	file shape_file_natural  <- file('../includes/danang.natural.shp');
	file shape_file_landuse  <- file('../includes/danang.landuse.shp');
	file shape_file_bounds <- file('../includes/danang.bounds.shp');
	file shape_file_busstations <- file('../includes/danang.busstations.shp');
	file shape_file_beaches <- file('../includes/danang.beaches.shp');
	file shape_file_busstops <- file('../includes/danang.targetpoints.shp');
	const my_colors type: list of: rgb init: [rgb('black'), rgb('magenta'), rgb('blue'), rgb('orange'), rgb('gray'), rgb('yellow'), rgb('red')];
	float bus_length <- 12.0;
	float bus_width <- 2.5;
	float moto_length <- 3.0; 
	float moto_width <- 1;
	graph the_graph;
	int eastern_beach_population <- 0;
	int northern_beach_population <- 0;
	int number_of_bus_at_eastern_station <- 1;
	int number_of_bus_at_northern_station <- 0;
	geometry creating_area_geometry;
	list obstacle_specy_list <- [species(wheelvehicle), species(pedestrian)];
	list background_sepcies_for_pedestrian <- [species(roadwidth), species(busstation), species(beach)];
	list background_species_for_vehicle <- [species(roadwidth), species(busstation)];
	init{
		create roadline from: shape_file_roadlines;
		loop rd over: roadline as list {
			create roadwidth {
				set shape <- rd.shape buffer 10;
			}	
		}
		set the_graph <- as_edge_graph(list(roadline));
		create mylocation from: shape_file_places;
		create busstation from: shape_file_busstations;
		create beach from: shape_file_beaches;
		create busstop from: shape_file_busstops;
		set creating_area_geometry <- first(list(beach));
		create pedestrian number: eastern_beach_population{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 0;
			set shape <- circle(size);
			set target <- nil;
			set speed <- 1;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_sepcies_for_pedestrian;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - (shape buffer 2);
		}
		set creating_area_geometry <- last(list(beach));
		create pedestrian number: northern_beach_population{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 0;
			set shape <- circle(size);
			set target <- nil;
			set speed <- 1;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_sepcies_for_pedestrian;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - (shape buffer 2);
		}
		/* *
		set creating_area_geometry <- first(list(busstation));
		create wheelvehicle number: number_of_bus_at_northern_station{
			set location <- any_location_in(creating_area_geometry);
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set target <- nil;
			set speed <- 1;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_vehicle;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - shape;
		}
		/* */
		set creating_area_geometry <- last(list(busstation));
		create wheelvehicle number: number_of_bus_at_eastern_station{
			set location <- any_location_in((list(busstop) at 8));
			set heading <- 270;
			set shape <- rectangle({bus_width, bus_length});
			set target <- (list(busstop) at 9);
			set speed <- 7;
			set obstacle_species <- obstacle_specy_list;
			set background_species <- background_species_for_vehicle;
			set considering_range <- 30;
			set color <- rgb('green');
			set creating_area_geometry <- creating_area_geometry - shape;
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
	species mylocation{
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
		rgb color <- rgb('blue');
		aspect base{
			draw geometry: shape color: color;
		}
	}
	species wheelvehicle skills: [driving]{
		float length <- bus_length;
		float width <- bus_width;
		rgb color <- rgb('yellow');
		busstop target <- nil ;
		/* for driving skill */
		reflex move_with_vehicleGoto{
			do goto target: target on: the_graph speed: speed;
			if ((self distance_to target) < 10){
				do write message: "reaches destination";
				do write message: "" + (length(list(busstop)));
				let temp_targets type: list <- list(busstop) - target;
				do write message: "" + (length(list(temp_targets)));
				set target <- one_of(temp_targets);
				do write message: "" + target;
			}
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
		/* for driving skill */
		reflex move_with_pedestrianGoto{
			do pedestrianGoto target: target on: the_graph speed: speed;
		}
		/* */
		aspect base{
			draw shape: geometry color: color;
		}
	}
}

experiment evacDanang1 type: gui {
	output{
		display city_display refresh_every: 1{
			species roadwidth aspect: base;
			species roadline aspect: base;
			species mylocation aspect: base;
			species busstation aspect: base;
			species beach aspect: base transparency: 0.5;
			species pedestrian aspect: base;
			species wheelvehicle aspect: base;
			species busstop aspect: base;
		}
		display city_display_with_opengl refresh_every: 1 type: opengl{
			species roadwidth aspect: base;
			species roadline aspect: base;
			species mylocation aspect: base;
			species busstation aspect: base;
			species beach aspect: base transparency: 0.5;
			species pedestrian aspect: base;
			species wheelvehicle aspect: base;
			species busstop aspect: base;
		}

	}
}
