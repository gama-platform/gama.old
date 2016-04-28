/**
* Name: Prima 5
* Author: 
* Description: This model shows how to create agent and make them move randomly in the world.
* 	Some agents are infected, and others can gain the infection if they are in a certain range. The 
* 	people are now placed in buildings at the initial state. Those buildings are created thanks to
* 	a shapefile. Roads are also created thanks to an other shapefile. A graph is now created from
* 	the road agents, and the people move from their location to a targent on the graph. The display
* 	is now a 3D display. The model adds a new level as people going inside a building will be now
* 	a new species belonging to the building and that will be manage by the building agent concerned
* Tags: skill, shapefile, graph, 3d, multi_level
*/
model model5 

global {
	//Shapefile of the roads
	file roads_shapefile <- file("../includes/road.shp");
	//Shapefile of the buildings
	file buildings_shapefile <- file("../includes/building.shp");
	//The bounds of the world are the bounds of the roads shapefile
	geometry shape <- envelope(roads_shapefile);
	//The graph of road on which people agents will move
	graph road_network;
	init {
		//Creation of the roads using the road shapefile
		create roads from: roads_shapefile;
		//Creation of the graph using the road agents as edge
		road_network <- as_edge_graph(roads);
		//Creation of the building using the building shapefile
		create buildings from: buildings_shapefile;
		//Creation of the people that will be placed in a building randomly chosen
		create people number:1000 {
			buildings init_place <- one_of(buildings);
			location <- any_location_in(init_place) + {0,0, init_place.height};
			target <- any_location_in(one_of(buildings));
		}
	}
}
//Species people that can be infected and will move from a location to a target
species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	point target;
	//Make the agent move to its target
	reflex move {
		do goto target:target on: road_network;
		//Change the target once it has been reached
		if (location = target) {
			target <- any_location_in(one_of(buildings));
		}
	}
	//Reflex to infect the people agent if it is not infected already and if there are infected people nearby
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
//Species buildings that will change the people agents inside it to people in building agent
//Managing them once they have been captured, and releasing them once they reached a
//leaving time
species buildings {
	float height <- 10.0+ rnd(10);
	int nb_I -> {members count (people_in_building(each).is_infected)};
	
	aspect geom {
		draw shape color: empty(members) ? #gray : (nb_I/length(members) > 0.5 ? #red : #green) depth: height;
	}
	//Species people in building that will be managed by the building agents
	species people_in_building parent: people schedules: [] {
		int leaving_time;
		aspect circle{}
	}
	//Reflex to capture the people entering inside the building, changing them in people in building species and puting a leaving time to know when lettiing
	// them out
	reflex let_people_enter {
		list<people> entering_people <- (people inside self);
		if !(empty (entering_people)) {
			//Change the species of the people captured from people species to people_in_building species
			capture entering_people as: people_in_building returns: people_captured;
			ask people_captured {
				leaving_time <- int(time + 50 + rnd(50));
			}
 		}
	}
	//Reflex to let the people_in_building agents go out if there leaving time is reached, releasing them in the world as people agent
	reflex let_people_leave  {
		list<people_in_building> leaving_people <- members of_species people_in_building where (time >= each.leaving_time);
		if !(empty (leaving_people)) {
			release leaving_people as: people in: world;
		}
	}
}

experiment main_experiment type:gui{
	output {
		display map type: opengl {
			species roads aspect:geom;
			species buildings aspect:geom;
			species people aspect:circle;			
		}
	}
}