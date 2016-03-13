/**
* Name: Prima 4
* Author: 
* Description: This model shows how to create agent and make them move randomly in the world.
* 	Some agents are infected, and others can gain the infection if they are in a certain range. The 
* 	people are now placed in buildings at the initial state. Those buildings are created thanks to
* 	a shapefile. Roads are also created thanks to an other shapefile. A graph is now created from
* 	the road agents, and the people move from their location to a targent on the graph. The display
* 	is now a 3D display.
* Tags: skill, shapefile, graph, 3d
*/
model model4 

global {
	//Shapefile for the roads
	file roads_shapefile <- shape_file("../includes/road.shp");
	//Shapefile for the buildings
	file buildings_shapefile <- file("../includes/building.shp");
	//The world bounds are now the shapefile roads 's boundss
	geometry shape <- envelope(roads_shapefile);
	//The graph of roads on which people will move
	graph road_network;
	init {
		//Creation of the roads using the road shapefile
		create roads from: roads_shapefile;
		//Creation of the graph using roads as the edges of the graph
		road_network <- as_edge_graph(roads);
		//Creation of the buildings from the building shapefile
		create buildings from: buildings_shapefile;
		//Creation of the people agent, located in one of the building
		create people number:1000 {
			buildings init_place <- one_of(buildings);
			location <- any_location_in(init_place) + {0,0, init_place.height};
			target <- any_location_in(one_of(buildings));
		}
	}
}
//Species people that will move from a location to a target and can be infected
species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	point target;
	
	//Reflex to move to the target on the graph of roads
	reflex move {
		do goto target:target on: road_network;
		//Once the target is reached, find an other target
		if (location = target) {
			target <- any_location_in(one_of(buildings));
		}
	}
	//Reflex to check if we are infected if we aren't already and infected people are nearby
	reflex infect when: is_infected{
		ask people at_distance 10 {
			if flip(0.01) {
				is_infected <- true;
			}
		}
	}
	aspect circle{
		draw sphere(5) color:is_infected ? #red : #green;
	}
}

species roads {
	aspect geom {
		draw shape color: #black;
	}
}

species buildings {
	float height <- 10.0+ rnd(10);
	aspect geom {
		draw shape color: #gray depth: height;
	}
}

experiment main_experiment type:gui{
	output {
		//Type opengl to display in 3D
		display map type: opengl ambient_light: 150{
			species roads aspect:geom;
			species buildings aspect:geom;
			species people aspect:circle;			
		}
	}
}