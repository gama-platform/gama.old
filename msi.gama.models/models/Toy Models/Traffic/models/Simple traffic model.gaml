/**
* Name: Traffic
* Author: Patrick Taillandier
* Description: A simple road network model: the speed on a road depends on the number of people on this road (the highest, the slowest)
* Tags: gis, shapefile, graph, skill, transport
*/

model traffic

global {
	//Shapefile of the buildings
	file building_shapefile <- file("../includes/buildings.shp");
	//Shapefile of the roads
	file road_shapefile <- file("../includes/roads.shp");
	//Shape of the environment
	geometry shape <- envelope(road_shapefile);
	//Step value
	float step <- 10 #s;
	//Graph of the road network
	graph road_network;
	//Map containing all the weights for the road network graph
	map<road,float> road_weights;
	
	init {
		//Initialization of the building using the shapefile of buildings
		create building from: building_shapefile;
		//Initialization of the road using the shapefile of roads
		create road from: road_shapefile;
		
		//Creation of the people agents
		create people number: 1000{
			//People agents are located anywhere in one of the building
			location <- any_location_in(one_of(building));
      	}
      	//Weights of the road
      	road_weights <- road as_map (each::each.shape.perimeter);
      	road_network <- as_edge_graph(road);
	}
	//Reflex to update the speed of the roads according to the weights
	reflex update_road_speed  {
		road_weights <- road as_map (each::each.shape.perimeter / each.speed_coeff);
		road_network <- road_network with_weights road_weights;
	}
}
//Species to represent the people using the skill moving
species people skills: [moving]{
	//Target point of the agent
	point target;
	//Probability of leaving the building
	float leaving_proba <- 0.05; 
	//Speed of the agent
	float speed <- 5 #km/#h;
	rgb color <- rnd_color(255);
	//Reflex to leave the building to another building
	reflex leave when: (target = nil) and (flip(leaving_proba)) {
		target <- any_location_in(one_of(building));
	}
	//Reflex to move to the target building moving on the road network
	reflex move when: target != nil {
		do goto target: target on: road_network recompute_path: false move_weights: road_weights;
		if (location = target) {
			target <- nil;
		}	
	}
	
	aspect default {
		draw circle(5) color: color;
	}
}
//Species to represent the buildings
species building {
	aspect default {
		draw shape color: #gray;
	}
}
//Species to represent the roads
species road {
	//Capacity of the road considering its perimeter
	float capacity <- 1 + shape.perimeter/30;
	//Number of people on the road
	int nb_people <- 0 update: length(people at_distance 1);
	//Speed coefficient computed using the number of people on the road and the capicity of the road
	float speed_coeff <- 1.0 update:  exp(-nb_people/capacity) min: 0.1;
	
	aspect default {
		draw (shape + 3 * speed_coeff) color: #red;
	}
}
experiment trafic type: gui {
	float minimum_cycle_duration <- 0.01;
	output {
		display carte type: opengl{
			species building refresh: false;
			species road ;
			species people ;
		}
	}
}
