/**
* Name: Prima 2
* Author: 
* Description: This model shows how to create agent and make them move randomly in the world.
* 	Some agents are infected, and others can gain the infection if they are in a certain range. The 
* 	people are now placed in buildings at the initial state. Those buildings are created thanks to
* 	a shapefile. Roads are also created thanks to an other shapefile.
* Tags: skill, shapefile
*/
model model2 
 
global {
	//Shapefile for the roads
	file roads_shapefile <- file("../includes/road.shp");
	//Shapefile for the buildings
	file buildings_shapefile <- file("../includes/building.shp");
	//Definition of the shape as the bounds of the roads shapefile
	geometry shape <- envelope(roads_shapefile);
	init {
		//Creation of the road agents using the shapefile
		create roads from: roads_shapefile;
		//Creation of the building agents using the shapefile
		create buildings from: buildings_shapefile;
		//Creation of the people agent and place them in one of the building and chose a target in the building agents
		create people number:1000 {
			location <- any_location_in(one_of(buildings));
			target <- any_location_in(one_of(buildings));
		}
	}
}

//People species with agents moving and can be infected
species people skills:[moving]{		
	float speed <- 5.0 + rnd(5);
	bool is_infected <- flip(0.01);
	point target;
	//Make the agent wander at each step with a certain speed.
	reflex move {
		do wander;
	}
	//Infect the agent if it is not already infected, and according to the infected people in a range
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