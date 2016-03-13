/**
* Name: Prima 3
* Author: 
* Description: This model shows how to create agent and make them move randomly in the world.
* 	Some agents are infected, and others can gain the infection if they are in a certain range. The 
* 	people are now placed in buildings at the initial state. Those buildings are created thanks to
* 	a shapefile. Roads are also created thanks to an other shapefile. A graph is now created from
* 	the road agents, and the people move from their location to a targent on the graph.
* Tags: skill, shapefile, graph
*/
model model3 
 
global {
	//Shapefile for the roads
	file roads_shapefile <- file("../includes/road.shp");
	//Shapefile for the buildings
	file buildings_shapefile <- file("../includes/building.shp");
	//The bounds of the world are the same as the bounds of the roads shapefile
	geometry shape <- envelope(roads_shapefile);
	//We need now a graph on which people will move
	graph road_network;
	init {
		//Create the roads using the shapefiles
		create roads from: roads_shapefile;
		//Create the graph using the road agents as edges of the graph
		road_network <- as_edge_graph(roads);
		//Create the buildings using the shapefiles
		create buildings from: buildings_shapefile;
		//Create the people agents and choose their location and target
		create people number:1000 {
			location <- any_location_in(one_of(buildings));
			target <- any_location_in(one_of(buildings));
		}
	}
}
//Species people that will move from a location to a target
species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	point target;
	//Reflex to make the agent move to a target
	reflex move {
		do goto target:target on: road_network;
		//Each time the agent is at the target's location, chose an other target
		if (location = target) {
			target <- any_location_in(one_of(buildings));
		}
	}
	//check if the agent is infected if it is not infected already and if there are infected people nearby
	reflex infect when: is_infected{
		ask people at_distance 10 {
			if flip(0.01) {
				is_infected <- true;
			}
		}
	}
	aspect circle{
		draw circle(5) color:is_infected ? #red : #green;
	}
}

species roads {
	aspect geom {
		draw shape color: #black;
	}
}

species buildings {
	aspect geom {
		draw shape color: #gray;
	}
}

experiment main_experiment type:gui{
	output {
		display map {
			species roads aspect:geom;
			species buildings aspect:geom;
			species people aspect:circle;			
		}
	}
}