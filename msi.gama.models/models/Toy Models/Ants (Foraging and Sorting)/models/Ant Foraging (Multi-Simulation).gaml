/**
* Name: Ant Foraging (Multi-Simulation)
* Author: 
* Description: Toy Model ant using the question of how ants search food and use pheromons to return to their 
* nest once they did find food. 
* Tags: gui, skill, grid, multi_simulation, diffusion
*/
model ants

import "Ant Foraging (Complex).gaml"

 


//Experiment to show how to make multi simulations
experiment "4 Simulations" type: gui {
	parameter 'Number:' var: ants_number init: 100 unit: 'ants' category: 'Environment and Population';
	parameter 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';

	// We create three supplementary simulations using the species name 'ants_model' (automatically created from the name of the model + '_model')

	init {
		create ants_model with: [ants_number::200,evaporation_per_cycle::0.7,diffusion_rate::0.2];
		create ants_model with: [ants_number::50,evaporation_per_cycle::0.05,diffusion_rate::0.8];
		create ants_model with: [ants_number::10,evaporation_per_cycle::0.3,diffusion_rate::0.6];
	}
	
	permanent {
		display Comparison background: #white {
			chart "Food Gathered" type: series {
				loop s over: simulations {
					data "Food " + int(s) value: s.food_gathered color: s.color marker: false style: line ;
				}
			}
		}
	}


	output {
		display Ants background: #white type: opengl{
			image '../images/soil.jpg' position: { 0.05, 0.05 } size: { 0.9, 0.9 };
			agents "agents" transparency: 0.5 position: { 0.05, 0.05 } size: { 0.9, 0.9 } value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest));
			species ant position: { 0.05, 0.05 } size: { 0.9, 0.9 } aspect: icon;
		}
	
	}
}


