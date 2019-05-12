/**
* Name: Ant Sorting
* Author: 
* Description: This model is loosely based on the behavior of ants sorting different elements in their nest. A of mobile agents - the ants - is placed on a grid. 
* 	The grid itself contains cells of different colors. Each step, the agents move randomly. If they enter a colored cell, they pick this color if its density in the 
* 	neighbourhood is less than *number_of_objects_around*. If they have picked a color, they drop it on a black cell if they have encountered at least 
* 	*number_of_objects_in_history* cells with the same color.\n After a while, colors begin to be aggregated.
* Tags: gui, skill, grid
*/

model ant_sort

global  {
	// Parameters 
	int number_of_different_colors <- 5 max: 9 ;
	int density_percent <- 30 min: 0 max: 99 ;
	int number_of_objects_in_history <- 3 min: 0 ;
	int number_of_objects_around  <- 5 min: 0 max: 8;
	int width_and_height_of_grid <- 128 max: 400 min: 10 ;  
	int ants <- 20 min: 1 ;
	list<rgb> colors <- [#yellow,#red, #orange, #blue, #green,#cyan, #gray,#pink,#magenta] ;

	init { 
		create ant number: ants;
	} 
}
//Species ant that will move and follow a final state machine
species ant skills: [ moving ] control: fsm { 
	rgb color <- #white ; 
	ant_grid place -> ant_grid (location) ;
	
	//Reflex to make the ant wander
	reflex wandering { 
		do wander amplitude: 120.0;
	}
	//Initial state that will change to full
	state empty initial: true {
		transition to: full when: (place.color != #black) and ( (place.neighbors count (each.color = place.color)) < (rnd(number_of_objects_around))) {
			color <- place.color ;
			place.color <- #black ; 
		}
	}
	//State full that will change to black if the place color is empty and drop the color inside it
	state full {
		enter { 
			int encountered <- 0; 
		}
		if place.color = color { 
			encountered <- encountered + 1 ;
		}
		transition to: empty when: (place.color = #black) and (encountered > number_of_objects_in_history) {
			place.color <- color ;
			color <- #black ;
		}
	}
}
//Grid that will use the density to determine the color
grid ant_grid width: width_and_height_of_grid height: width_and_height_of_grid neighbors: 8 use_regular_agents: false frequency: 0{
	rgb color <- (rnd(100)) < density_percent ? (colors at rnd(number_of_different_colors - 1)) : #black ;
}


	
experiment "Color sort" type: gui{
	parameter "Number of colors:" var: number_of_different_colors category: "Environment" ;
	parameter "Density of colors:" var: density_percent category: "Environment" ;
	parameter "Number of similar colors in memory necessary to put down:" var: number_of_objects_in_history category: "Agents" ;
	parameter "Number of similar colors in perception necessary to pick up:" var: number_of_objects_around category: "Agents" ;
	parameter "Width and height of the grid:" var: width_and_height_of_grid category: "Environment" ;
	parameter "Number of agents:" var: ants category: "Agents" ;
	
	output {
		display OpenGL type: opengl  {
			grid ant_grid ;
			species ant transparency: 0.2 {
				draw circle(5) empty: true color: color;
			}
		}
	}
}


