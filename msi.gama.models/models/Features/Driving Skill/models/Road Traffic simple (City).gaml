 /** 
 *  RoadTrafficCity
 *  Author: patricktaillandier
 *  Description: 
 */
  
model RoadTrafficCity
 
   
global {   
	file shape_file_roads parameter: "Shapefile for the roads:" category: "GIS" <- file("../includes/ManhattanRoads.shp") ;
	file shape_file_bounds parameter: "Shapefile for the bounds:" category: "GIS" <- file("../includes/ManhattanBounds.shp") ;
	file shape_file_buildings parameter: "Shapefile for the buildings:" category: "GIS" <- file("../includes/ManhattanBuildings.shp") ;
	geometry shape <- envelope(shape_file_bounds);
	int nbGoalsAchived <- 0;
	int day_time update: cycle mod 144 ;
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
		create road from: shape_file_roads with:[nbLanes::int(read("lanes"))];
		create building from: shape_file_buildings;
		ask road as list {
			visu_geom <- shape + (2 * nbLanes);	
		}
		the_graph <-  (as_edge_graph(road));
		create people number: nb_people { 
			living_space <- 3.0;
			tolerance <- 0.1;
			lanes_attribute <- "nbLanes";
			obstacle_species <- [species(self)]; 
			speed <- min_speed + rnd (max_speed - min_speed) ;
			start_work <- min_work_start + rnd (max_work_start - min_work_start) ;
			end_work <- min_work_end + rnd (max_work_end - min_work_end) ;
			living_place <- one_of(building) ;
			working_place <- one_of(building) ;
			location <- living_place.location; 
		}   
	}
	
	reflex update_graph when:every(10){
		map<road,float> weights_map <- road as_map (each:: (each.shape.perimeter * each.coeff_traffic));
		the_graph <- the_graph with_weights weights_map;
	}
	
} 
	
species road  { 
	int nbLanes;
	int indexDirection; 
	bool blocked <- false;
	rgb color <- rgb("black");
	float coeff_traffic <- 1.0 update: 1 + (float(length(people at_distance 1.0)) / shape.perimeter * 200 / nbLanes);
	geometry visu_geom;
	
	aspect base { 
		draw shape color: rgb("black") ;
	} 
		
	user_command "Remove a road" action: remove;
	user_command "Add a road" action: add;
		 
	action remove {
		blocked <- true;
		the_graph <-  (as_edge_graph(road where (!each.blocked))) ;
		map<road,float> weights_map <- road as_map (each:: each.coeff_traffic);
		the_graph <- the_graph  with_weights weights_map;
		color <- rgb("magenta");
	}
		
	action add {
		blocked <- false;
		the_graph <-  (as_edge_graph(road where (!each.blocked)));
		map<road,float> weights_map <- road as_map (each:: each.coeff_traffic);
		the_graph <- the_graph  with_weights weights_map;
		color <- rgb("black");
	}
		
	aspect road_width {  
		draw visu_geom color: color ;
	}
	
	aspect traffic_jam {  
		if (coeff_traffic > 0.025) {
			draw shape + (coeff_traffic / 4.0) color: rgb("red") ;
		}
	} 		
}
	
species building  { 
	rgb color <- rgb("gray");
	aspect base { 
		draw shape color: color ;
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
		previousLoc <- copy(location);
		do goto_driving target: the_target on: the_graph speed: speed ; 
		switch location { 
			match the_target {
				the_target <- nil;
				nbGoalsAchived <- nbGoalsAchived +1;
			}
			match previousLoc {
				targetBis <- last((one_of(road where (each distance_to self < evadeDist)).shape).points);
				normalMove <- false;
			}
		}
	}
		
	reflex EvadeMove when: !(normalMove){
		previousLoc <- copy(location);
		do goto_driving target: targetBis on: the_graph speed: speed ; 
		switch location { 
			match targetBis {
				normalMove <- true;
			}
			match previousLoc {
				targetBis <- last((one_of(road where (each distance_to self < evadeDist)).shape).points);
			}
		}
	}
		
	reflex time_to_work when: day_time = start_work {
		objective <- "working" ;
		the_target <- any_location_in (working_place);
	}
	
	reflex time_to_go_home when: day_time = end_work {
		objective <- "go home" ;
		the_target <- any_location_in (living_place); 
	}  
	
	aspect base {
		draw circle(20) color: color;
	}
}

experiment traffic type: gui {
	parameter "Shapefile for the buildings:" var: shape_file_buildings category: "GIS" ;
	parameter "Shapefile for the roads:" var: shape_file_roads category: "GIS" ;
	parameter "Shapefile for the bounds:" var: shape_file_bounds category: "GIS" ;
	parameter "Number of people agents" var: nb_people category: "People" ;
	parameter "Earliest hour to start work" var: min_work_start category: "People" ;
	parameter "Latest hour to start work" var: max_work_start category: "People" ;
	parameter "Earliest hour to end work" var: min_work_end category: "People" ;
	parameter "Latest hour to end work" var: max_work_end category: "People" ;
	parameter "minimal speed" var: min_speed category: "People" ;
	parameter "maximal speed" var: max_speed category: "People" ;
	parameter "Value of destruction when a people agent takes a road" var: destroy category: "Road" ;
	parameter "Radius of the city center" var: city_center_radius category: "City" ;
	
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
			chart name: "Traffic jam" type: series size: {0.9, 0.4} position: {0.05, 0.05} {
				data name:"Mean road traffic coefficient" value: mean (road collect each.coeff_traffic) style: line color: rgb("green") ;
				data name:"Max road traffic coefficient" value: road max_of (each.coeff_traffic) style: line color: rgb("red") ;
			}
			chart name: "People Objectif" type: pie style: exploded size: {0.9, 0.4} position: {0.05, 0.55} {
				data name:"Working" value: length ((people as list) where (each.objective="working")) color: rgb("green") ;
				data name:"Staying home" value: length ((people as list) where (each.objective="go home")) color: rgb("blue") ;
			}
		}
		monitor "Number of goals achieved" value: nbGoalsAchived refresh_every: 1 ;
	}
}



