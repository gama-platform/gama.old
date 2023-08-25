/**
* Name: segregationGrid
* Author: 
*  Description: A model showing the segregation of the people just by putting a similarity wanted parameter using cells 
* 	to represent the individuals
* Tags: grid
*/
model segregation

//Importation of the Common Schelling Segregation model
import "../includes/Common Schelling Segregation.gaml"

//Define the environment as torus
global torus: true{
	//List of all the free places
	list<space> free_places ;
	//List of all the places
	list<space> all_places  ;
	//List of all the people
	list<space> all_people;
	//Shape of the environment
	geometry shape <- square(dimensions);
	
	//Action to initialize the places
	action initialize_places {
		all_places <- shuffle(space);
		free_places <- shuffle(all_places);
	}
	//Action to initialize the people agents
	action initialize_people {
		//Place all the people agent in the cellular automata
		loop i from: 0 to: number_of_people - 1 {
			space pp <- all_places at i;
			remove pp from: free_places;
			add pp to: all_people;
			pp.color <- colors at (rnd(number_of_groups - 1));
		}

	}
	//Reflex to migrate all the people agents
	reflex migrate {
		ask copy(all_people) {
			do migrate;
		}

	}

}

//Grid species representing the places and the people in each cell
grid space parent: base width: dimensions height: dimensions neighbors: 8  {
	rgb color <- #black;
	//List of the neighbours of the places
	list<space> my_neighbours <- self neighbors_at neighbours_distance;
	//Action to migrate the agent in another cell if it is not happy
	action migrate {
		if !is_happy {
			//Change the space of the agent to a free space
			space pp <- any(my_neighbours where (each.color = #black));
			if (pp != nil) {
				free_places <+ self;
				free_places >- pp;
				all_people >- self;
				all_people << pp;
				pp.color <- color;
				color <- #black;
			}
		}
	}
}


experiment schelling type: gui {
	output {
		display Segregation type:2d antialias:false{
			grid space;
		}

		display Charts  type: 2d {
			chart "Proportion of happiness" type: pie background: #lightgray style: exploded position: { 0, 0 } size: { 1.0, 0.5 } {
				data "Unhappy" value: number_of_people - sum_happy_people color: #green;
				data "Happy" value: sum_happy_people color: #yellow;
			}

			chart "Global happiness and similarity" type: series background: #lightgray axes: #white position: { 0, 0.5 } size: { 1.0, 0.5 }  x_range: 50{
				data "happy" color: #blue value: (sum_happy_people / number_of_people) * 100 style: spline;
				data "similarity" color: #red value: (sum_similar_neighbours / sum_total_neighbours) * 100 style: step;
			}

		}

	}

}
