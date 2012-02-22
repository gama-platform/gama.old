model tutorial_gis_city_traffic

global {
	string shape_file_buildings <- '../includes/building.shp' parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	string shape_file_roads <- '../includes/road.shp' parameter: 'Shapefile for the roads:' category: 'GIS' ;
	string shape_file_bounds <- '../includes/bounds.shp' parameter: 'Shapefile for the bounds:' category: 'GIS' ;
	int nb_people <- 200 parameter: 'Number of people agents' category: 'People' ;
	int day_time value: time mod 144 ;
	int min_work_start <- 36 parameter: 'Earliest hour to start work' category: 'People' ;
	int max_work_start <- 60 parameter: 'Latest hour to start work' category: 'People' ;
	int min_work_end <- 84 parameter: 'Earliest hour to end work' category: 'People' ;
	int max_work_end <- 132 parameter: 'Latest hour to end work' category: 'People' ;
	float min_speed <- 50 parameter: 'minimal speed' category: 'People' ;
	float max_speed <- 100 parameter: 'maximal speed' category: 'People' ;
	float destroy <- 0.02 parameter: 'Value of destruction when a people agent takes a road' category: 'Road' ;
	graph the_graph; 
	init {
		create species: building from: shape_file_buildings with: [type::read('NATURE')] {
			if condition: type='Industrial' {
				set color <- rgb('blue') ;
			}
		}
		create species: road from: shape_file_roads ;
		let weights_map type: map <- (list (road)) as_map [each:: each.destruction_coeff];
		set the_graph <- as_edge_graph(list(road))  with_weights weights_map;
		
		let residential_buildings type: list value: (building as list) where (each.type='Residential') ;
		let industrial_buildings type: list value: (building as list) where (each.type='Industrial') ;
		create species: people number: nb_people {
			set speed value: min_speed + rnd (max_speed - min_speed) ;
			set start_work value: min_work_start + rnd (max_work_start - min_work_start) ;
			set end_work value: min_work_end + rnd (max_work_end - min_work_end) ;
			set living_place value: one_of(residential_buildings) ;
			set working_place value: one_of(industrial_buildings) ;
			set location value: any_location_in (living_place.shape); 
		}
	}
	reflex update_graph{
		let weights_map type: map value: (list (road)) as_map [each:: each.destruction_coeff];
		set the_graph value: the_graph  with_weights weights_map;
	}
}
entities {
	species building {
		var type type: string ;
		var color type: rgb := rgb('gray')  ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species road {
		var destruction_coeff type: float init: 1 ;
		var color type: rgb init: [min([255, int(255*(destruction_coeff - 1))]),max ([0, int(255 - (255*(destruction_coeff - 1)))]),0]  value: [min([255, int(255*(destruction_coeff - 1))]),max ([0, int(255 - (255*(destruction_coeff - 1)))]),0] ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species people skills: [moving]{
		var color type: rgb init: rgb('yellow') ;
		var living_place type: building init: nil ;
		var working_place type: building init: nil ;
		var start_work type: int ;
		var end_work type: int ;
		var objectif type: string ;
		var the_target type: point init: nil ;
		aspect base {
			draw shape: circle color: color size: 10 ;
		}
		reflex time_to_work when: day_time = start_work {
			set objectif value: 'working' ;
			set the_target value: any_location_in (working_place.shape);
		}
		reflex time_to_go_home when: day_time = end_work {
			set objectif value: 'go home' ;
			set the_target value: any_location_in (living_place.shape); 
		}
		reflex move when: the_target != nil { 
			let path_followed type: path value: self goto [target::the_target, on::the_graph];
			let segments type: list of: geometry value: path_followed.segments;
			loop line over: segments {
				let dist type: float value: line.perimeter;
				let ag type: road value: path_followed agent_from_geometry line; 
				ask target: road(ag) {
					set destruction_coeff value: destruction_coeff + (destroy * dist / shape.perimeter);
				}
			}
			if condition: the_target = location {
				set the_target value: nil ;
			}
		}
	}
}
environment bounds: shape_file_bounds ;
output {
	display city_display refresh_every: 1 {
		species building aspect: base ;
		species road aspect: base ;
		species people aspect: base ;
	}
}
