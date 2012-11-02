/** 
 *  RoadTrafficCity
 *  Author: patricktaillandier
 *  Description: 
 */
  
model RoadTrafficCity
 
   
global {   
	file shape_file_roads parameter: 'Shapefile for the roads:' category: 'GIS' <- file('../includes/ManhattanRoads.shp') ;
	file shape_file_bounds parameter: 'Shapefile for the bounds:' category: 'GIS' <- file('../includes/ManhattanBounds.shp') ;
	file shape_file_buildings parameter: 'Shapefile for the buildings:' category: 'GIS' <- file('../includes/ManhattanBuildings.shp') ;
	int nbGoalsAchived <- 0;
	list roadsList of: road ; 
	list buildings of: building;
	int nb_people <- 100;
	int day_time update: time mod 144 ;
	int min_work_start <- 36;
	int max_work_start <- 60;
	int nb_people <- 500;
	int min_work_end <- 84; 
	int max_work_end <- 132; 
	float min_speed <- 50.0;
	float max_speed <- 100.0; 
	float destroy <- 0.02;
	graph the_graph;
	float city_center_radius <- 100.0;
	
	 
	init {  
		create road from: shape_file_roads with:[nbLanes::read('LANE_NB')];
		create building from: shape_file_buildings;
		set roadsList <- road as list;
		set buildings <- building as list;
		ask road as list {
			set visu_geom <- shape buffer (2 * nbLanes);	
		}
		set the_graph <-  (as_edge_graph(list(road))) with_optimizer_type "Dijkstra";
		create people number: nb_people { 
			set living_space <-3;
			set tolerance <- 0.1;
			set lanes_attribute <- "nbLanes";
			set obstacle_species <- [species(self)]; 
			set speed <- min_speed + rnd (max_speed - min_speed) ;
			set start_work <- min_work_start + rnd (max_work_start - min_work_start) ;
			set end_work <- min_work_end + rnd (max_work_end - min_work_end) ;
			set living_place <- one_of(buildings) ;
			set working_place <- one_of(buildings) ;
			set location <- living_place.location; 
			 
		}   
	}
	
	action remove_center {
		
		ask ((location neighbours_at city_center_radius) of_species road) {
			do remove;
		}
	}
	
	reflex update_graph{
		let weights_map type: map <- roadsList as_map [each:: each.coeff_traffic];
		set the_graph <- the_graph  with_weights weights_map;
	}
	
} 
entities {
	species road  { 
		int nbLanes;
		int indexDirection; 
		bool blocked <- false;
		rgb color <- rgb("black");
		geometry visu_geom update: shape buffer (2 * nbLanes);
		float coeff_traffic <- 1.0 update: 1 + (float(length(people at_distance 1.0)) / shape.perimeter * 200 / nbLanes);
		aspect base { 
			draw geometry: shape color: rgb('black') ;
		} 
		
		
		user_command Remove_road action: remove;
		user_command Add_road action: add;
		
		action remove {
			set blocked <- true;
			set the_graph <-  (as_edge_graph(list(road) where (!each.blocked))) with_optimizer_type "Dijkstra";
			let weights_map type: map <- roadsList as_map [each:: each.coeff_traffic];
			set the_graph <- the_graph  with_weights weights_map;
			
			set color <- rgb("magenta");
		}
		
		action add {
			set blocked <- false;
			set the_graph <-  (as_edge_graph(list(road) where (!each.blocked))) with_optimizer_type "Dijkstra";
			let weights_map type: map <- roadsList as_map [each:: each.coeff_traffic];
			set the_graph <- the_graph  with_weights weights_map;
			
			set color <- rgb("black");
		}
		aspect road_width {  
			draw geometry: visu_geom color: color ;
		}
		aspect traffic_jam {  
			if (coeff_traffic > 0.025) {
				draw geometry: shape buffer (coeff_traffic / 4.0) color: rgb("red") ;
			}
		} 
		
	}
	species building  { 
		rgb color <- rgb("gray");
		aspect base { 
			draw geometry: shape color: color ;
		}
	}
	species people skills: [driving]{ 
		float speed; 
		rgb color <- rgb([rnd(255),rnd(255),rnd(255)]) ;
		point targetBis <- nil ; 
		point previousLoc <- nil;
		bool normalMove <- true;
		float evadeDist <- 500.0;
		building living_place <- nil ;
		building working_place <- nil ;
		int start_work ;
		int end_work  ;
		string objective ; 
		point the_target <- nil ;
	
		
		reflex move when: the_target != nil and normalMove{
			set previousLoc <- copy(location);
			do goto_driving target: the_target on: the_graph speed: speed ; 
			switch location { 
				match the_target {
					set the_target <- nil;
					set nbGoalsAchived <- nbGoalsAchived +1;
				}
				match previousLoc {
					set targetBis <- last((one_of(roadsList where (each distance_to self < evadeDist)).shape).points);
					set normalMove <- false;
				}
			}
		}
		reflex EvadeMove when: !(normalMove){
			set previousLoc <- copy(location);
			do goto_driving target: targetBis on: the_graph speed: speed ; 
			switch location { 
				match targetBis {
					set normalMove <- true;
				}
				match previousLoc {
					set targetBis <- last((one_of(roadsList where (each distance_to self < evadeDist)).shape).points);
				}
			}
		}
		reflex time_to_work when: day_time = start_work {
			set objective <- 'working' ;
			set the_target <- any_location_in (working_place);
		}
		reflex time_to_go_home when: day_time = end_work {
			set objective <- 'go home' ;
			set the_target <- any_location_in (living_place); 
		}  
	
		aspect base {
			draw shape: circle color: color size: 20 ;
		}
	}
}

environment bounds: shape_file_bounds ;

experiment traffic type: gui {
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
	parameter 'Raidus of the city center' var: city_center_radius category: 'City' ;
	user_command "Remove city center" action: remove_center; 
	
	output {
		display city_display refresh_every: 1 {
			species road aspect: road_width ;
			species building aspect: base;
			species people aspect: base;
		}
		display traffic_jam_display refresh_every: 1 {
			species road aspect: base ;
			species road aspect: traffic_jam ;
		}
		display chart_display refresh_every: 10 {
			chart name: 'Traffic jam' type: series background: rgb('lightGray') size: {0.9, 0.4} position: {0.05, 0.05} {
				data name:'Mean road traffic coefficient' value: mean (roadsList collect each.coeff_traffic) style: line color: rgb('green') ;
				data name:'Max road traffic coefficient' value: roadsList max_of (each.coeff_traffic) style: line color: rgb('red') ;
			}
			chart name: 'People Objectif' type: pie background: rgb('lightGray') style: exploded size: {0.9, 0.4} position: {0.05, 0.55} {
				data name:'Working' value: length ((people as list) where (each.objective='working')) color: rgb('green') ;
				data name:'Staying home' value: length ((people as list) where (each.objective='go home')) color: rgb('blue') ;
			}
		}
		monitor nbGoalsAchived value: nbGoalsAchived refresh_every: 1 ;
	}
}



