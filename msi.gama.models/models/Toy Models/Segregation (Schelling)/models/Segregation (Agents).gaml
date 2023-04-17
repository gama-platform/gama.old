/**
* Name: segregationAgents
* Author: 
* Description: A model showing the segregation of the people just by putting a similarity wanted parameter using agents 
* 	to represent the individuals
* Tags: grid
*/

model segregation

//import the Common Schelling Segregation model
import "../includes/Common Schelling Segregation.gaml"

global {
	//List of all the free places
	list<space> free_places ;
	//List of all the places
	list<space> all_places ;
	//Shape of the world
	geometry shape <- square(dimensions);
	
	//Action to initialize the people agents
	action initialize_people { 
		create people number: number_of_people; 
		all_people <- people as list ;  
	} 
	//Action to initialize the places
	action initialize_places { 
		all_places <- shuffle (space);
		free_places <- all_places;  
	} 
}
//Grid to discretize space, each cell representing a free space for the people agents
grid space width: dimensions height: dimensions neighbors: 8 use_regular_agents: false frequency: 0{
	rgb color  <- #black;
}

//Species representing the people agents
species people parent: base  {
	//Color of the people agent
	rgb color <- colors at (rnd (number_of_groups - 1));
	//List of all the neighbours of the agent
	list<people> my_neighbours -> people at_distance neighbours_distance ;
	//Cell representing the place of the agent
	space my_place;
	init {
		//The agent will be located on one of the free places
		my_place <- one_of(free_places);
		location <- my_place.location; 
		//As one agent is in the place, the place is removed from the free places
		free_places >> my_place;
	} 
	//Reflex to migrate the people agent when it is not happy 
	reflex migrate when: !is_happy {
		//Add the place to the free places as it will move to another place
		free_places << my_place;
		//Change the place of the agent
		my_place <- one_of(free_places);
		location <- my_place.location; 
		//Remove the new place from the free places
		free_places >> my_place;
	}
	
	aspect default{ 
		draw circle (0.5) color: color; 
	}
}



experiment schelling type: gui {	
	output {
		display Segregation {
			species people;
		}	
		display Charts  type: 2d {
			chart "Proportion of happiness" type: pie background: #gray style: exploded position: {0,0} size: {1.0,0.5}{
				data "Unhappy" value: number_of_people - sum_happy_people color: #green;
				data "Happy" value: sum_happy_people color: #yellow;
			}
			chart "Global happiness and similarity" type: series background: #gray axes: #white position: {0,0.5} size: {1.0,0.5} {
				data "happy" color: #blue value:  (sum_happy_people / number_of_people) * 100 style: spline ;
				data "similarity" color: #red value:  (sum_similar_neighbours / sum_total_neighbours) * 100 style: step ;
			}
		}
	}
}