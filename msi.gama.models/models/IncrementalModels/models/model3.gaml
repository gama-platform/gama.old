/**
 *  model3
 *  This model illustrates how to load GIS data representing the road and to make people on this roads. 
 */ 
 model model3
 
 global {
	file shape_file_bounds <- file('../includes/bounds.shp');
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	geometry shape <- envelope(shape_file_bounds);
	graph roads_graph;
	init {
		create Buildings from: shape_file_buildings with: [type:: string(read('NATURE')), company::string(read('COMPANY'))] {
			if type = 'Industrial' {
				my_color <- rgb('blue');
			} else if type = 'Residential' {
				my_color <- rgb('red');
			}

		}

		create Roads from: shape_file_roads;
		roads_graph <- as_edge_graph(Roads as list);
		create Workers number: 100 {
			do wanna_go_to_work;
			do moving;
		}

	}

}

entities {
	species Buildings {
		string type;
		string company;
		rgb my_color;
		aspect asp1 {
			draw shape color: my_color;
		}
	
	}
	
	species Roads {
		aspect normal {
			draw shape color: rgb('black');
		}
	
	}
	
	species Workers skills: [moving] {
		Buildings my_target;
		int transportation <- rnd(10); // moving speed depend on type of transportation
		int threshold_time <- 200+rnd(500);
		int flag_time <- 0;
		aspect asp1 {
			draw circle(5) color: my_target.my_color;
		}
	
		action wanna_go_to_work {
			my_target <- any(Buildings where (each.type = 'Industrial'));
			threshold_time <- 200+rnd(500);
		}
	
		action wanna_go_home {
			my_target <- any(Buildings where (each.type = 'Residential'));
			threshold_time <- 200+rnd(500);
		}
	
		action moving {
			do goto target: my_target on: roads_graph speed: 5 + transportation;
			if (location = my_target.location and flag_time = 0) {
				flag_time <- cycle;
			}
	
		}
	
		reflex day_life {
			do moving;
			if (flag_time > 0 and cycle - flag_time >= threshold_time) {
				flag_time <- 0;
								
				if (my_target.type = 'Industrial') {
					do wanna_go_home;
				} else {
					do wanna_go_to_work;
				}
	
			}
	
		}
	
	}

}

experiment exp3 type: gui {
	output {
		display disp1 refresh_every: 1 {
			species Buildings aspect: asp1;
			species Roads aspect: normal;
			species Workers aspect: asp1;
		}

	}
}