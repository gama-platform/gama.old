model segregation

import "../include/Common Schelling Segregation.gaml"

global {
	list<space> free_places <- [] ;
	list<space> all_places <- [] ;
	geometry shape <- square(dimensions);
	
	action initialize_people { 
		create people number: number_of_people; 
		all_people <- people as list ;  
	} 
	action initialize_places { 
		all_places <- shuffle (space);
		free_places <- all_places;  
	} 
}
entities {
	grid space width: dimensions height: dimensions neighbours: 8 use_regular_agents: false frequency: 0{
		const color type: rgb <- black;
	}

	species people parent: base  {
		const color type: rgb <- colors at (rnd (number_of_groups - 1));
		list<people> my_neighbours -> {people at_distance neighbours_distance} ;
		space my_place;
		init {
			my_place <- one_of(free_places);
			location <- my_place.location; 
			remove my_place from: free_places;
		} 
		reflex migrate when: !is_happy {
			add my_place to: free_places;
			my_place <- one_of(free_places);
			location <- my_place.location; 
			remove my_place from: free_places;
		}
		
		aspect default{ 
			draw circle (0.5) color: color; 
		}
	}
}


experiment schelling type: gui {	
	output {
		display Segregation {
			species people;
		}	
		display Charts {
			chart name: "Proportion of happiness" type: pie background: rgb("gray") style: exploded position: {0,0} size: {1.0,0.5}{
				data "Unhappy" value: number_of_people - sum_happy_people color: rgb("green");
				data "Happy" value: sum_happy_people color: rgb("yellow");
			}
			chart name: "Global happiness and similarity" type: series background: rgb("gray") axes: rgb("white") position: {0,0.5} size: {1.0,0.5} {
				data "happy" color: rgb("blue") value:  (sum_happy_people / number_of_people) * 100 style: spline ;
				data "similarity" color: rgb("red") value:  (sum_similar_neighbours / sum_total_neighbours) * 100 style: step ;
			}
		}
	}
}