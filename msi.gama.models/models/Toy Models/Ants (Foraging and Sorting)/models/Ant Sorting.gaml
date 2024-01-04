/**
* Name: Ant Sorting
* Author: 
* Description: This model is loosely based on the behavior of ants sorting different elements in their nest. 
*	
* 	A group of mobile agents - the ants - is placed on a grid. 
* 	
* 	The grid itself contains cells of different colors. 
* 	
* 	Each step, the agents move randomly. 
* 	
* 	If they enter a colored cell, they pick this color if its density in the neighbourhood 
* 	is less than *number_of_objects_around*. 
* 	
* 	If they have picked a color, they drop it on a black cell if they have encountered at least 
* 	*number_of_objects_in_history* cells with the same color.
* 	
* 	After a while, colors begin to be aggregated.
* 
* Tags: gui, skill, grid
*/

model ant_sort

global  {
	// Parameters 
	int number_of_different_colors <- 5 max: 9 min:1 ;
	int density_percent <- 30 min: 0 max: 99 ;
	int number_of_objects_in_history <- 3 min: 0 ;
	int number_of_objects_around  <- 4 min: 0 max: 8;
	int width_and_height_of_grid <- 60 max: 400 min: 10 ;  
	int ants <- 200 min: 1 ;
	list<rgb> all_colors <- [#yellow,#red, #orange, #blue, #green,#cyan, #gray,#pink,#magenta] ;
	list<rgb> colors_to_use;

	init { 
		colors_to_use <- number_of_different_colors among all_colors;
		create ant number: ants;
		
		ask ((density_percent / 100)* (width_and_height_of_grid * width_and_height_of_grid)) among ant_grid{
			color <- one_of(colors_to_use);
		}
	} 
}
//Species ant that will move and follow a final state machine
species ant skills: [ moving ] control: fsm { 
	rgb color <- #white ; 
	ant_grid place -> ant_grid (location);
	file img <- image_file("../images/ant.png");
	
	//Reflex to make the ant wander
	reflex wandering { 
		do wander amplitude: 120.0;
	}
	//Initial state that will change to full
	state empty initial: true {
		transition 	to: full 
					when: 	place.color != #black 
							and ( (place.neighbors count (each.color = place.color)) < number_of_objects_around) {
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
	
	aspect default  {
		draw circle(3) color: rgb(color, 0.8) wireframe:true width:3;
		draw img size:1 at:location;
	}
}
//Grid that will use the density to determine the color
grid ant_grid width: width_and_height_of_grid height: width_and_height_of_grid neighbors: 8 use_regular_agents: false frequency: 0{
	rgb color <- #black;
	
	
}


	
experiment "Color sort" type: gui{
	
	
	parameter "Number of colors:" var: number_of_different_colors category: "Environment" ;
	parameter "Density of colors:" var: density_percent category: "Environment" ;
	parameter "Number of similar colors in memory necessary to put down:" var: number_of_objects_in_history category: "Agents" ;
	parameter "Number of similar colors in perception necessary to pick up:" var: number_of_objects_around category: "Agents" ;
	parameter "Width and height of the grid:" var: width_and_height_of_grid category: "Environment" ;
	parameter "Number of agents:" var: ants category: "Agents" ;
	
	output synchronized:true{
		display view type: 3d antialias:false axes:false{
			camera 'default' location: {50.0,50.0022,125.3708} target: {50.0,50.0,0.0};
			grid ant_grid border: #black;
			species ant transparency: 0.05;
		}
	}
}


