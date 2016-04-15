/**
* Name: Ant Foraging (Multi-Simulation)
* Author: 
* Description: Toy Model ant using the question of how ants search food and use pheromons to return to their 
* nest once they did find food. 
* Tags: gui, skill, grid, multi_simulation, diffusion
*/
model ants

global {
	//Evaporation value per cycle of the pheromons
	float evaporation_per_cycle <- 5.0 min: 0.0 max: 240.0 parameter: 'Evaporation of the signal (unit/cycle):' category: 'Signals';
	//Diffusion rate of the pheromons
	float diffusion_rate <- 1.0 min: 0.0 max: 1.0 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Signals';
	//Size of the grid
	int gridsize <- 100 min: 30 parameter: 'Width and Height of the grid:' category: 'Environment and Population';
	//Number of ants that will be created
	int ants_number <- 50 min: 1 parameter: 'Number of ants:' category: 'Environment and Population';
	//Number of food places to create
	int number_of_food_places <- 5 min: 1 parameter: 'Number of food depots:' category: 'Environment and Population';
	float grid_transparency <- 1.0;
	const ant_shape_empty type: file <- file('../icons/ant.png');
	const ant_shape_full type: string <- '../icons/full_ant.png';
	const center type: point <- { round(gridsize / 2), round(gridsize / 2) };
	var food_gathered type: int <- 1;
	var food_placed type: int <- 1;
	const background type: rgb <- rgb(#99CC66);
	const food_color type: rgb <- rgb(#312200);
	const nest_color type: rgb <- rgb(#000000); 

	geometry shape <- square(gridsize);
	init {
		//Creation of the food places placed randomly with a certain distance between each
		loop times: number_of_food_places {
			point loc <- { rnd(gridsize - 10) + 5, rnd(gridsize - 10) + 5 };
			list<ant_grid> food_places <- (ant_grid where ((each distance_to loc) < 5));
			ask food_places {
				if food = 0 {
					food <- 5;
					food_placed <- food_placed + 5;
					color <- food_color;  
				}                                           
			}
		}
		//Creation of the ants that will be placed in the nest
		create ant number: ants_number with: (location: center);
		//Write the index of the simulation
		write "Simulation " + int(self) + " created";
	}
	//Reflex to diffuse the pheromon among the grid
	reflex diffuse {
      diffuse var:road on:ant_grid proportion: diffusion_rate radius:2 propagation: gradient;
   }
  

  
} 

//Grid used to discretize the space to place food
grid ant_grid width: gridsize height: gridsize neighbors: 8 /*frequency: grid_frequency*/ use_regular_agents: false use_individual_shapes: false{
	const is_nest type: bool <- (topology(ant_grid) distance_between [self, center]) < 4;
	float road <- 0.0 max:240.0 update: (road<=evaporation_per_cycle) ? 0.0 : road-evaporation_per_cycle;
	rgb color <- is_nest ? nest_color : ((food > 0) ? food_color : ((road < 0.001) ? background : rgb(#009900) + int(road * 5))) update: is_nest ? nest_color : ((food > 0) ?
	food_color : ((road < 0.001) ? background : rgb(#009900) + int(road * 5)));
	int food <- 0;
}
//Species ant that will move and follow a final state machine
species ant skills: [moving] control: fsm {
	float speed <- 1.0;
	bool has_food <- false;
	
	//Reflex to place a pheromon stock in the cell
	reflex diffuse_road when:has_food=true{
      ant_grid(location).road <- ant_grid(location).road + 100.0;
   }
	//Action to pick food
	action pick (int amount) {
		has_food <- true;
		ant_grid place <- ant_grid(location);
		place.food <- place.food - amount;
	}
	//Action to drop food
	action drop {
		food_gathered <- food_gathered + 1;
		has_food <- false;
		heading <- heading - 180;
	}
	//Action to find the best place in the neighborhood cells	
	point choose_best_place {
		list<ant_grid> list_places <- ant_grid(location).neighbors;
		if (list_places count (each.food > 0)) > 0 {
			return point(list_places first_with (each.food > 0));
		} else {
			list_places <- (list_places where ((each.road > 0) and ((each distance_to center) > (self distance_to center)))) sort_by (each.road);
			return point(last(list_places));
		}
	}
	//Reflex to drop food once the ant is in the nest	
	reflex drop when: has_food and (ant_grid(location)).is_nest {
		do drop();
	}
	//Reflex to pick food when there is one at the same location
	reflex pick when: !has_food and (ant_grid(location)).food > 0 {
		do pick(1);
	}
	//Initial state to make the ant wander 
	state wandering initial: true {
		do wander(amplitude: 90);
		float pr <- (ant_grid(location)).road;
		transition to: carryingFood when: has_food;
		transition to: followingRoad when: (pr > 0.05) and (pr < 4);
	}
	//State to carry food once it has been found
	state carryingFood {
		do goto(target: center);
		transition to: wandering when: !has_food;
	}
	//State to follow a pheromon road if once has been found
	state followingRoad {
		point next_place <- choose_best_place();
		float pr <- (ant_grid(location)).road;
		location <- next_place;
		transition to: carryingFood when: has_food;
		transition to: wandering when: (pr < 0.05) or (next_place = nil);
	}

	aspect icon {
		draw ant_shape_empty size: {8,6} rotate: my heading + 1;
	}

}	




//Experiment to show how to make multi simulations
experiment "4 Simulations" type: gui {
	parameter name: 'Number:' var: ants_number init: 100 unit: 'ants' category: 'Environment and Population';
	parameter name: 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter name: 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';

	// We create three supplementary simulations using the species name 'ants_model' (automatically created from the name of the model + '_model')

	init {
		create ants_model with: [ants_number::200,evaporation_rate::0.7,diffusion_rate::0.2];
		create ants_model with: [ants_number::50,evaporation_rate::0.05,diffusion_rate::0.8];
		create ants_model with: [ants_number::10,evaporation_rate::0.3,diffusion_rate::0.6];
	}
	
	permanent {
		display Comparison background: #white {
			chart "Food Gathered" type: series {
				loop s over: ants_model {
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


