/**
* Name: Simple Road Network 
* Author: Patrick Taillandier
* Description: Model using shapefiles to create buildings and a road graph, with people going from their living place to their work place 
* depending on the hour. The traffic jam is also taken into account to slow the people agents when they are too much on the same 
* road. The experiment shows a display of the city, with people agents, buildings and roads, a display of the traffic jam occuring on the 
* roads, and a chart display showing two charts : one for the traffic jam coefficients, and an other for the objectives of the people agents.
* Tags: gis, shapefile, graph, agent_movement, skill, transport
*/
  
model RoadTrafficCity
 
   
global {   
	
	//Shapefiles for the buildings, the roads and the bounds of the environment
	file shape_file_roads parameter: "Shapefile for the roads:" category: "GIS" <- file("../includes/ManhattanRoads.shp") ;
	file shape_file_bounds parameter: "Shapefile for the bounds:" category: "GIS" <- file("../includes/ManhattanBounds.shp") ;
	file shape_file_buildings parameter: "Shapefile for the buildings:" category: "GIS" <- file("../includes/ManhattanBuildings.shp") ;
	geometry shape <- envelope(shape_file_bounds);
	
	//Stock the number of times agents reached their goal (their house or work place)
	int nbGoalsAchived <- 0;
	
	//represent the day time for the agent to inform them to go work or home
	int day_time update: cycle mod 144 ;
	
	//Variables to manage the minimal and maximal time to start working
	int min_work_start <- 36;
	int max_work_start <- 60;
	
	//Number of people created
	int nb_people <- 500;
	
	//Variables to manage the minimal and maximal time to go home
	int min_work_end <- 84; 
	int max_work_end <- 132; 
	
	//Manage the speed allowed in the model for the people agents
	float min_speed <- 50.0;
	float max_speed <- 100.0; 
	
	//Graph of the road network
	graph the_graph;
	
	 
	init {  
		
		//creation of the agents of road and building species using the shapefile and linking the 
		create road from: shape_file_roads with:[nbLanes::int(read("lanes"))];
		create building from: shape_file_buildings;
		
		//Increase the shape of roads according to the number of lanes it has
		ask road as list {
			visu_geom <- shape + (2 * nbLanes);	
		}
		//Initliazation of the graph with the road species
		the_graph <-  (as_edge_graph(road));
		
		//Initialization of nb_people agents of people species
		// and definition of their living and working places
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
	
	//Update of the graph every 10 Cycles to take into account the traffic jam of the road in the weights of the graph
	reflex update_graph when:every(10#cycle){
		map<road,float> weights_map <- road as_map (each:: (each.shape.perimeter * each.coeff_traffic));
		the_graph <- the_graph with_weights weights_map;
	}
	
} 
	
species road  { 
	int nbLanes;
	int indexDirection; 
	bool blocked <- false;
	rgb color <- #black;
	float coeff_traffic <- 1.0 update: 1 + (float(length(people at_distance 1.0)) / shape.perimeter * 200 / nbLanes);
	geometry visu_geom;
	
	aspect base { 
		draw shape color: #black ;
	} 
	
	
	//Command that the user can execute to remove or add a road	
	user_command "Block" action: remove;
	user_command "Unblock" action: add;
		 
	action remove {
		blocked <- true;
		the_graph <-  (as_edge_graph(road where (!each.blocked))) ;
		map<road,float> weights_map <- road as_map (each:: each.coeff_traffic);
		the_graph <- the_graph  with_weights weights_map;
		color <- #magenta;
	}
		
	action add {
		blocked <- false;
		the_graph <-  (as_edge_graph(road where (!each.blocked)));
		map<road,float> weights_map <- road as_map (each:: each.coeff_traffic);
		the_graph <- the_graph  with_weights weights_map;
		color <- #black;
	}
		
	aspect road_width {  
		draw visu_geom color: color ;
	}
	
	aspect traffic_jam {  
		if (coeff_traffic > 0.025) {
			draw shape + (coeff_traffic / 4.0) color: #red ;
		}
	} 		
}
	
species building  { 
	rgb color <- #gray;
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
		
	//Reflex to make the agent move while it had a target and normalMove equals true
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
		
	//Reflex to make the agent move when it is not normal moving 
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
	
	//Reflex to make the agent go to its working place when it's time to go work
	reflex time_to_work when: day_time = start_work {
		objective <- "working" ;
		the_target <- any_location_in (working_place);
	}
	
	//Reflex to make the agent go to its living place when it's time to go home
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
	
	output {
		layout #split;
		
		display city_display {
			species road aspect: road_width ;
			species building aspect: base;
			species people aspect: base;
		}
		display traffic_jam_display {
			species road aspect: base ;
			species road aspect: traffic_jam ;
		}
		display chart_display refresh: every(10#cycle) {
			chart "Traffic jam" type: series size: {0.9, 0.4} position: {0.05, 0.05} {
				data "Mean road traffic coefficient" value: mean (road collect each.coeff_traffic) style: line color: #green ;
				data "Max road traffic coefficient" value: road max_of (each.coeff_traffic) style: line color: #red ;
			}
			chart "People Goals" type: pie style: exploded size: {0.9, 0.4} position: {0.05, 0.55} {
				data "Working" value: length ((people as list) where (each.objective="working")) color: #green ;
				data "Staying home" value: length ((people as list) where (each.objective="go home")) color: #blue ;
			}
		}
		monitor "Number of goals achieved" value: nbGoalsAchived ;
	}
}



