model tutorial_gis_city_traffic

global {
	var shape_file_buildings type: string init: '../includes/building.shp' parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	var shape_file_roads type: string init: '../includes/road.shp' parameter: 'Shapefile for the roads:' category: 'GIS' ;
	var shape_file_bounds type: string init: '../includes/bounds.shp' parameter: 'Shapefile for the bounds:' category: 'GIS' ;
	var nb_people type: int init: 100 parameter: 'Number of people agents' category: 'People' ;
	var day_time type: int value: time mod 144 ;
	var min_work_start type: int init: 36 parameter: 'Earliest hour to start work' category: 'People' ;
	var max_work_start type: int init: 60 parameter: 'Latest hour to start work' category: 'People' ;
	var min_work_end type: int init: 84 parameter: 'Earliest hour to end work' category: 'People' ;
	var max_work_end type: int init: 132 parameter: 'Latest hour to end work' category: 'People' ;
	var min_speed type: float init: 50 parameter: 'minimal speed' category: 'People' ;
	var max_speed type: float init: 100 parameter: 'maximal speed' category: 'People' ;
	var the_graph type: graph;
	init {
		create building from: shape_file_buildings with: [type::read('NATURE')] {
			if  type='Industrial' {
				set color value: rgb('blue') ;
			}
		}
		create species: road from: shape_file_roads ;
		set the_graph value: as_edge_graph(list(road));
		
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
}
entities {
	species building {
		var type type: string ;
		var color type: rgb init: rgb('gray')  ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species road {
		var color type: rgb init: rgb('black') ;
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
		var shape type: geometry init: circle(5);

		reflex time_to_work when: day_time = start_work {
			set objectif value: 'working' ;
			set the_target value: any_location_in (working_place.shape);
		}
		reflex time_to_go_home when: day_time = end_work {
			set objectif value: 'go home' ;
			set the_target value: any_location_in (living_place.shape); 
		}  
		reflex move when: the_target != nil {
			do goto target: the_target on: the_graph ; 
			switch the_target { 
				match location {set the_target value: nil ;}
			}
		}
		aspect default {
			draw shape: geometry color: color ;
		}
	}
}
environment bounds: shape_file_bounds ;
output {
		display city_display refresh_every: 1 {
		species building aspect: base ;
		species road aspect: base ;
		species people  ;
	}
}
