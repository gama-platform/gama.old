model tutorial_gis_city_traffic

global {
	file shape_file_buildings <- shape_file('../includes/building.shp', 0);
	file shape_file_roads <- shape_file('../includes/road.shp', 0);
	file shape_file_bounds <- shape_file('../includes/bounds.shp', 0);
	geometry shape <- envelope(shape_file_bounds);
	int nb_people <- 100;
	int day_time update: cycle mod 144;
	int min_work_start <- 36;
	int max_work_start <- 60;
	int min_work_end <- 84;
	int max_work_end <- 132;
	float min_speed <- 50.0;
	float max_speed <- 100.0;
	graph the_graph;
	init {
		create building from: shape_file_buildings with: [type:: string(read('NATURE'))] {
			if type = 'Industrial' {
				color <- rgb('blue');
			}
			height <- 10 + rnd(90);
		}

		create road from: shape_file_roads;
		the_graph <- as_edge_graph(road);
		list<building> residential_buildings <- building where (each.type = 'Residential');
		list<building> industrial_buildings <- building where (each.type = 'Industrial');
		create people number: nb_people {
			speed <- min_speed + rnd(max_speed - min_speed);
			start_work <- min_work_start + rnd(max_work_start - min_work_start);
			end_work <- min_work_end + rnd(max_work_end - min_work_end);
			living_place <- one_of(residential_buildings);
			working_place <- one_of(industrial_buildings);
			location <- any_location_in(living_place);
			location <- {location.x, location.y,living_place.height };
		}

	}

}

entities {
	species building {
		string type;
		rgb color <- rgb('gray');
		int height;
		aspect base {
			draw shape color: color depth: height;
		}

	}

	species road {
		rgb color <- rgb('black');
		aspect base {
			draw shape color: color;
		}

	}

	species people skills: [moving] {
		rgb color <- rgb('yellow');
		building living_place <- nil;
		building working_place <- nil;
		int start_work;
		int end_work;
		string objectif;
		point the_target <- nil;
		reflex time_to_work when: day_time = start_work {
			objectif <- 'working';
			the_target <- any_location_in(working_place);
		}

		reflex time_to_go_home when: day_time = end_work {
			objectif <- 'go home';
			the_target <- any_location_in(living_place);
		}

		reflex move when: the_target != nil {
			do goto target: the_target on: the_graph;
			switch the_target {
				match location {
					the_target <- nil;
					location <- {location.x, location.y,objectif  = 'go home' ?  living_place.height : working_place.height};
				}

			}

		}

		aspect base {
			draw sphere(10) color: color;
		}

	}

}

experiment road_traffic type: gui {
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS';
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS';
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS';
	parameter 'Number of people agents' var: nb_people category: 'People';
	parameter 'Earliest hour to start work' var: min_work_start category: 'People';
	parameter 'Latest hour to start work' var: max_work_start category: 'People';
	parameter 'Earliest hour to end work' var: min_work_end category: 'People';
	parameter 'Latest hour to end work' var: max_work_end category: 'People';
	parameter 'minimal speed' var: min_speed category: 'People';
	parameter 'maximal speed' var: max_speed category: 'People';
	output {
		display city_display type: opengl ambient_light: 100 {
			species building aspect: base;
			species road aspect: base;
			species people aspect: base;
		}

	}

}

experiment road_traffic_multi_layer type: gui {
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS';
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS';
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS';
	parameter 'Number of people agents' var: nb_people category: 'People';
	parameter 'Earliest hour to start work' var: min_work_start category: 'People';
	parameter 'Latest hour to start work' var: max_work_start category: 'People';
	parameter 'Earliest hour to end work' var: min_work_end category: 'People';
	parameter 'Latest hour to end work' var: max_work_end category: 'People';
	parameter 'minimal speed' var: min_speed category: 'People';
	parameter 'maximal speed' var: max_speed category: 'People';
	output {
		display city_display type: opengl ambient_light: 100 {
			species road aspect: base;
			species building aspect: base position:{0,0,0.25};
			species people aspect: base position:{0,0,0.5};
		}
	}

}

experiment AnimatedView type: gui {
	output {
		display animatedView type: opengl ambient_light: 100 {
			species road aspect: base;
			species building aspect: base position: { 0, 0, (time * 2)/1000 };
			species people aspect: base position: { 0, 0, (time * 4)/1000 };
		}
	}
}