model tutorial_gis_city_traffic



global {
	file shape_file_buildings <- file('../includes/building.shp');
	file shape_file_roads <- file('../includes/road.shp');
	file shape_file_bounds <- file('../includes/bounds.shp');
	int nb_people <- 100;
	int day_time update: time mod 144 ;
	int min_work_start <- 36;
	int max_work_start <- 60;
	int min_work_end <- 84; 
	int max_work_end <- 132; 
	float min_speed <- 50.0;
	float max_speed <- 100.0; 
	float destroy <- 0.02;
	graph the_graph;
	
		
	init {
		create building from: shape_file_buildings with: [type::string(read ('NATURE'))] {       
			if type='Industrial' {
				set color <- rgb('blue') ;
			}  
	  	}
		create road from: shape_file_roads ;
		let weights_map type: map <- (list (road)) as_map (each :: each.destruction_coeff);
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
		let weights_map type: map <- (list (road)) as_map (each :: each.destruction_coeff);
		set the_graph <- the_graph  with_weights weights_map;
	}
}
entities {
	species building {
		string type; 
		rgb color <- rgb('gray')  ; 
		aspect base {
			draw geometry: shape color: color  depth:100 ;
		}
	}
	species road  {
		float destruction_coeff <- 1.0 ;
		int colorValue <- int(255*(destruction_coeff - 1)) update: int(255*(destruction_coeff - 1));
		rgb color <- rgb([min([255, colorValue]),max ([0, 255 - colorValue]),0])  update: rgb([min([255, colorValue]),max ([0, 255 - colorValue]),0]) ;
		aspect base {
			draw geometry: shape color: color ;
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
				let ag type: road <- road(path_followed agent_from_geometry line); 
				ask road(ag) {
					set destruction_coeff <- destruction_coeff + (destroy * dist / shape.perimeter);
				}
			}
			switch the_target { 
				match location {set the_target <- nil ;}
			}
		}
		aspect base {
			draw sphere(10) color: color;
		}
	}
}
environment bounds: shape_file_bounds ;

experiment road_traffic type: gui {
	parameter 'Shapefile for the buildings:' var: shape_file_buildings category: 'GIS' ;
	parameter 'Shapefile for the roads:' var: shape_file_roads category: 'GIS' ;
	parameter 'Shapefile for the bounds:' var: shape_file_bounds category: 'GIS' ;
	parameter 'Number of people agents' var: nb_people category: 'People' ;
	parameter 'Earliest hour to start work' var: min_work_start category: 'People' ;
	parameter 'Latest hour to start work' var: max_work_start category: 'People' ;
	parameter 'Earliest hour to end work' var: min_work_end category: 'People' ;
	parameter 'Latest hour to end work' var: max_work_end category: 'People' ;
	parameter 'minimal speed' var: min_speed category: 'People' ;
	parameter 'maximal speed' var: max_speed category: 'People' ;
	parameter 'Value of destruction when a people agent takes a road' var: destroy category: 'Road' ;

	
	output {
		display city_display refresh_every: 1 type: opengl ambient_light:0.1{
			species people aspect: base z:0;
			species road aspect: base z:0.25;
			species building aspect:base z:0.5;
		}
	}
}

experiment AnimatedView type: gui {
	output {

	   display animatedView type: opengl ambient_light: 0.1 {
	   	    species road aspect: base z:0;
			species building aspect: base position: {0,0,time*2};
			species people aspect:base position: {0,0,time*4};
		}
	} 
}




