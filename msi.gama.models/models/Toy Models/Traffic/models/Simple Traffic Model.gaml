/**
* Name: Traffic
* Author: Patrick Taillandier & Alexis Drogoul
* Description: A simple traffic model with a pollution model: the speed on a road depends on the number of people 
* on the road (the highest, the slowest), and the people diffuse pollution on the envrionment when moving.
* Tags: gis, shapefile, graph, skill, transport, field
*/
model traffic

global {
//Shapefile of the buildings
	file building_shapefile <- file("../includes/buildings.shp");
	//Shapefile of the roads
	file road_shapefile <- file("../includes/roads.shp");
	//Shape of the environment
	geometry shape <- envelope(building_shapefile) + envelope(road_shapefile);
	//Step value
	float step <- 10 #s;
	field cell <- field(200, 200);
	//Graph of the road network
	graph road_network;
	//Map containing all the weights for the road network graph
	map<road, float> road_weights;

	init {
	//Initialization of the building using the shapefile of buildings
		create building from: building_shapefile;

		//Initialization of the road using the shapefile of roads
		create road from: road_shapefile;

		//Creation of the people agents
		create people number: 1000 {
		//People agents are located anywhere in one of the building
			location <- any_location_in(one_of(building));
			state <- flip(0.75) ? "ok" : "notok";
		}
		//Weights of the road
		road_weights <- road as_map (each::each.shape.perimeter);
		road_network <- as_edge_graph(road);
	}
	//Reflex to update the speed of the roads according to the weights
	reflex update_road_speed {
		road_weights <- road as_map (each::each.shape.perimeter / each.speed_coeff);
		road_network <- road_network with_weights road_weights;
	}

	//Reflex to decrease and diffuse the pollution of the environment
	reflex pollution_evolution {
	//ask all cells to decrease their level of pollution
		cell <- cell * 0.8;

		//diffuse the pollutions to neighbor cells
	//diffuse var: pollution on: cell proportion: 0.9;
	}

}

//Species to represent the people using the skill moving
species people skills: [moving] {
//Target point of the agent
	point target;
	//Probability of leaving the building
	float leaving_proba <- 0.05;
	//Speed of the agent
	float speed <- rnd(10) #km / #h + 1;
	// Random state
	string state;
	//Reflex to leave the building to another building
	reflex leave when: (target = nil) and (flip(leaving_proba)) {
		target <- any_location_in(one_of(building));
	}
	//Reflex to move to the target building moving on the road network
	reflex move when: target != nil {
	//we use the return_path facet to return the path followed
		path path_followed <- goto(target: target, on: road_network, recompute_path: false, return_path: true, move_weights: road_weights);

		//if the path followed is not nil (i.e. the agent moved this step), we use it to increase the pollution level of overlapping cell
		if (path_followed != nil) {
			try {
				cell[path_followed.shape.location] <- cell[path_followed.shape.location] + 10;
			}

			catch {
			}

		}

		if (location = target) {
			target <- nil;
		} }

	aspect default {
		draw rectangle(4,10) rotated_by (heading+90) color:( #dodgerblue) depth: 3;
		draw rectangle(4, 6) rotated_by (heading+90) color:( #dodgerblue) depth: 4;
	} }
	//Species to represent the buildings
species building {

	aspect default {
		draw shape color: darker(#darkgray).darker;
	}

}
//Species to represent the roads
species road {
//Capacity of the road considering its perimeter
	float capacity <- 1 + shape.perimeter / 30;
	//Number of people on the road
	int nb_people <- 0 update: length(people at_distance 1);
	//Speed coefficient computed using the number of people on the road and the capicity of the road
	float speed_coeff <- 1.0 update: exp(-nb_people / capacity) min: 0.1;
	int buffer <- 3;

	aspect default {
		draw (shape + buffer * speed_coeff * 2) color: #white;
	}

}

experiment traffic type: gui autorun: true{
	float minimum_cycle_duration <- 0.01;
	list<rgb> pal <- palette([ #black, #green, #yellow, #orange, #orange, #red, #red, #red]);
	map<rgb,string> legends <- [#green::"Good",#yellow::"Average",#orange::"Bad",#red::"Hazardous"];
	
	output synchronized: true{
		display carte type: 3d axes: false background: #black fullscreen: true toolbar: false{
			
			 overlay position: { 500#px,200#px} size: { 1 #px, 1 #px } background: # black border: #black rounded: false
            	{
            	//for each possible type, we draw a square with the corresponding color and we write the name of the type
                float y <- 30#px;
                loop p over: reverse(legends.pairs)
                {
                    draw square(40#px) at: { 20#px, y } color: rgb(p.key, 0.6) ;
                    draw p.value at: { 60#px, y} anchor: #left_center color: # white font: font("Arial", 14, #bold);
                    y <- y + 40#px;
                }
                draw rectangle(40#px, 160#px) at: {20#px, 90#px} wireframe: true color: #white;
                
                y <- y + 40#px;
                draw square(40#px) at: { 20#px, y } color: rgb(darker(#darkgray).darker) ;
                draw "Buildings" at: { 60#px, y} anchor: #left_center color: # lightblue font: font("Arial", 14, #bold);
                
                y <- y + 40#px;
                draw square(40#px) at: { 20#px, y } color: rgb(#dodgerblue) ;
                draw "Cars" at: { 60#px, y} anchor: #left_center color: #lightblue font: font("Arial", 14, #bold);

                y <- y + 40#px;
                draw square(40#px) at: { 20#px, y } color:rgb(#white) ;
                draw "Road" at: { 60#px, y} anchor: #left_center color: #lightblue font: font("Arial", 14, #bold);
                
                draw rectangle(40#px, 120#px) at: {20#px, 270#px} wireframe: true color: #white;

            }
			
			light #ambient intensity: 128;
			camera 'default' location: {1245.8884, 3324.2908, 1231.2804} target: {1252.5405, 1418.6084, 0.0};
			species building refresh: false;
			species road;
			species people;

			//display the pollution grid in 3D using triangulation.
			mesh cell scale: 9 triangulation: true transparency: 0.4 smooth: 4 above: 0.8 color: pal;
		}

	}

}
