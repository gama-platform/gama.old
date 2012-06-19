model tutorial_gis_city_traffic

global {
	file shape_file_buildings <- file('../includes/building.shp') parameter: 'Shapefile for the buildings:' category: 'GIS' ;
	file shape_file_roads <- file('../includes/road.shp') parameter: 'Shapefile for the roads:' category: 'GIS' ;
	file shape_file_bounds <- file('../includes/bounds.shp') parameter: 'Shapefile for the bounds:' category: 'GIS' ; 
	int nb_people <- 100 parameter: 'Number of people agents' category: 'People' ;
	int day_time update: time mod 144 ;
	int min_work_start <- 36 parameter: 'Earliest hour to start work' category: 'People' ;
	int max_work_start <- 60 parameter: 'Latest hour to start work' category: 'People' ;
	int min_work_end <- 84 parameter: 'Earliest hour to end work' category: 'People' ; 
	int max_work_end <- 132 parameter: 'Latest hour to end work' category: 'People' ;
	float min_speed <- 50 parameter: 'minimal speed' category: 'People' ;
	float max_speed <- 100 parameter: 'maximal speed' category: 'People' ;
	float destroy <- 0.02 parameter: 'Value of destruction when a people agent takes a road' category: 'Road' ;
	graph the_graph;
	
	init {
		create building from: shape_file_buildings with: [type::read ('NATURE')] {       
			if type='Industrial' {
				set color <- rgb('blue') ;
			}  
	  	}
		create road from: shape_file_roads ;
		let weights_map type: map <- (list (road)) as_map [each:: each.destruction_coeff];
		set the_graph <- as_edge_graph(list(road))  with_weights weights_map;
		
		let residential_buildings type: list of: building <- list(building) where (each.type='Residential');
		let industrial_buildings type: list of: building <- (building as list) where (each.type='Industrial') ;
		create people number: nb_people {
			set speed <- min_speed + rnd (max_speed - min_speed) ;
			set start_work <- min_work_start + rnd (max_work_start - min_work_start) ;
			set end_work <- min_work_end + rnd (max_work_end - min_work_end) ;
			set living_place <- one_of(residential_buildings) ;
			set working_place <- one_of(industrial_buildings) ;
			set location <- any_location_in (living_place);  
		}  
	}
	
	reflex update_graph{
		let weights_map type: map <- (list (road)) as_map [each:: each.destruction_coeff];
		set the_graph <- the_graph  with_weights weights_map;
	}
}
entities {
	species building {
		string type; 
		rgb color <- rgb('gray')  ; 
		//Add an arbitrary elevation for building
		aspect base {
			draw shape: geometry color: color ; 
		}
	}
	species road  {
		float destruction_coeff <- 1 ;
		int colorValue <- int(255*(destruction_coeff - 1)) update: int(255*(destruction_coeff - 1));
		rgb color <- [min([255, colorValue]),max ([0, 255 - colorValue]),0]  update: [min([255, colorValue]),max ([0, 255 - colorValue]),0] ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species people skills: [moving]{
		rgb color <- rgb('yellow') ;
		building living_place <- nil ;
		building working_place <- nil ; 
		int start_work ;
		int end_work  ;
		string objective ; 
		point the_target <- nil ;
		
		reflex time_to_work when: day_time = start_work {
			set objective <- 'working' ;
			set the_target <- any_location_in (working_place);
		}
		reflex time_to_go_home when: day_time = end_work {
			set objective <- 'go home' ;
			set the_target <- any_location_in (living_place); 
		}  
		reflex move when: the_target != nil {
			let path_followed type: path <- self goto [target::the_target, on::the_graph, return_path:: true];
			let segments type: list of: geometry <- path_followed.segments;
			loop line over: segments {
				let dist type: float <- line.perimeter;
				let ag type: road <- path_followed agent_from_geometry line; 
				ask road(ag) {
					set destruction_coeff <- destruction_coeff + (destroy * dist / shape.perimeter);
				}
			}
			switch the_target { 
				match location {set the_target <- nil ;}
			}
		}
		aspect base {
			draw shape: circle color: color size: 10 ;
		}
	}
}
environment bounds: shape_file_bounds ;


output {
	display city_display type:opengl refresh_every: 1 {
		species building aspect:base;
		species road aspect: base;
		species people aspect: base;
	}
}




