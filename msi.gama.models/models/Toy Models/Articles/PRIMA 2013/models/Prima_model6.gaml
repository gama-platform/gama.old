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
* 	The people_in_building agent will be infected inside a building respecting an ordinary differential
* 	equation system.
* Tags: skill, shapefile, graph, 3d, multi_level, equation
*/
model model6 

global {
	//Shapefile of roads
	file roads_shapefile <- file("../includes/road.shp");
	//Shapefile of buildings
	file buildings_shapefile <- file("../includes/building.shp");
	//Bounds of the world will be the bounds of the shapefile of roads
	geometry shape <- envelope(roads_shapefile);
	//The graph of roads on which people agents will move
	graph road_network;
	
	//The beta used for the ODE system
	float beta <- 0.4;
	
	init {
		//Creation of the roads using the shapefile
		create roads from: roads_shapefile;
		//Creation of the graph using the road agents as edge
		road_network <- as_edge_graph(roads);
		//Creation of the buildings using the shapefile
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
	//Reflex to make the agent move from its location to its target on the graph
	reflex move {
		do goto target:target on: road_network;
		//Change the target once it has been reached
		if (location = target) {
			target <- any_location_in(one_of(buildings));
		}
	}
	//Reflex to know if a non-infected agent is infected by the agents nearby
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
	//Variable to know the number of people infected inside the buildings
	int nb_I -> {members count (people_in_building(each).is_infected)};
	//Variable to know the number of people inside the building
	int nbInhabitants update: length(members);
	//List of all the people_in_building agents not infected				
	list<people_in_building> membersS update: list<people_in_building>(members) where (!each.is_infected);
	//List of all the people_in_building agents infected				
	list<people_in_building> membersI update: list<people_in_building>(members) where (each.is_infected);
	float t;
	//Float used in the ODE system representing the number of non infected agents    
	float S update: length(membersS) as float; 
	//Float used in the ODE system representing the number of infected agents 
   	float I update: length(membersI) as float;
   	float I_to_1 <- 0.0;
   	float h<-0.1;
   	
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
			capture entering_people as: people_in_building returns: people_captured;
			ask people_captured {
				leaving_time <- int(time + 50 + rnd(50));
			}
 		}
	}
	//Reflex to let the people_in_building agents go out if there leaving time is reached, releasing them in the world as people agent
	reflex let_people_leave  {
		list<people_in_building> leaving_people <- list<people_in_building>(members) where (time >= each.leaving_time);
		if !(empty (leaving_people)) {
			release leaving_people as: people in: world;
		}
	}
	//ODE system to represent the infection among the building
	equation SIR{ 
		diff(S,t) = (- beta * S * I / nbInhabitants) ;
		diff(I,t) = (  beta * S * I / nbInhabitants) ;
	}
	//Make the computation of the ODE System only when they are people inside
	reflex epidemic when:(S>0 and I>0){ 	
		float I0 <- I;
    	solve SIR method: "rk4" step: h ;
    	I_to_1 <- I_to_1 + (I - I0);
    	if(I_to_1 > 1) {
    		ask(membersS){
    			is_infected <- true;
    			myself.I_to_1 <- myself.I_to_1 - 1;
    		}
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