/**
* Name: Ant Foraging (Complex)
* Author: 
* Description: Toy Model ant using the question of how ants search food and use pheromons to return to their 
* nest once they did find food. Two experiments are proposed to show how to use batch : Batch and Genetic.
* Tags: gui, skill, grid, batch, diffusion
*/
model ants

global {
	//Evaporation value per cycle
	float evaporation_per_cycle <- 5.0 min: 0.0 max: 240.0 parameter: 'Evaporation of the signal (unit/cycle):' category: 'Signals';
	//Diffusion rate of the pheromon among the grid
	float diffusion_rate <- 1.0 min: 0.0 max: 1.0 parameter: 'Rate of diffusion of the signal (%/cycle):' category: 'Signals';
	//Size of the grid
	int gridsize <- 50 min: 30 parameter: 'Width and Height of the grid:' category: 'Environment and Population';
	//Number of ants
	int ants_number <- 30 min: 1 parameter: 'Number of ants:' category: 'Environment and Population';
	//Frequency of update of the grid
	int grid_frequency <- 1 min: 1 max: 100 parameter: 'Grid updates itself every:' category: 'Environment and Population';
	//Number of food places among the grid
	int number_of_food_places <- 5 min: 1 parameter: 'Number of food depots:' category: 'Environment and Population';
	float grid_transparency <- 1.0;
	file ant_shape_empty const: true <- obj_file('../images/fire-ant.obj', '../images/fire-ant.mtl', -90::{1,0,0});
	file ant_shape_2 <- obj_file("../images/ant2.obj", -90::{1,0,0});
	image_file terrain <- image_file("../images/soil.jpg");
	matrix<float> grid_values <- matrix<float>(as_matrix(terrain, {gridsize, gridsize}));
	//The center of the grid that will be considered as the nest location
	point center const: true <- { round(gridsize / 2), round(gridsize / 2) };
	int food_gathered <- 1;
	int food_placed <- 1;
	rgb background const: true <- rgb(#99CC66);
	rgb food_color const: true <- rgb(#312200);
	rgb nest_color const: true <- rgb(#000000); 

	geometry shape <- square(gridsize);
	init {
		// Normalization of the grid values
		float min <- min(grid_values);
		float max <- max(grid_values);
		float range <- (max - min) / 2.5;
		write "" + min + " " + max + " " + range;
		loop i from: 0 to: gridsize -1 {
			loop j from: 0 to: gridsize - 1 {
				grid_values[i, j] <- (grid_values[i, j] - min) / range ;
			}
		}
		
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
	}
	//Reflex to diffuse the pheromon among the grid
	reflex diffuse {
      diffuse var:road on:ant_grid proportion: diffusion_rate radius:3 propagation: gradient method:convolution;
   }
  
}

//Grid used to discretize the space to place food
grid ant_grid  width: gridsize height: gridsize neighbors: 8 frequency: grid_frequency use_regular_agents: false use_individual_shapes: false {
	bool is_nest const: true <- (topology(ant_grid) distance_between [self, center]) < 4;
	float road <- 0.0 max: 240.0 update: (road <= evaporation_per_cycle) ? 0.0 : road - evaporation_per_cycle;
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
		container list_places <- ant_grid(location).neighbors;
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
		do wander(amplitude: 75.0);
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
		draw ant_shape_empty  size: {7,5} at: (location  + {0,0,1})  rotate: heading 
	;
	}
}	

//Complete experiment that will inspect all ants in a table
experiment "3D View" type: gui {
	parameter 'Number:' var: ants_number init: 30 unit: 'ants' category: 'Environment and Population';
	parameter 'Grid dimension:' var: gridsize init: 100 unit: '(number of rows and columns)' category: 'Environment and Population';
	parameter 'Number of food depots:' var: number_of_food_places init: 5 min: 1 category: 'Environment and Population';


	output {
		display Ants3D type: opengl show_fps: true{
		grid ant_grid elevation: grid_values  triangulation: true texture: terrain refresh: false;
			agents "Trail" transparency: 0.7 position: { 0.05, 0.05, 0.02 } size: { 0.9, 0.9 } value: (ant_grid as list) where ((each.food > 0) or (each.road > 0) or (each.is_nest)) ;
			species ant position: { 0.05, 0.05, 0.025 } size: { 0.9, 0.9 } aspect: icon;
			light 1 type:point color:#yellow position:{world.shape.width*0.5 - world.shape.width*1.5,world.shape.width*0.5,world.shape.width} draw_light:true ;
			
			}
					
		}
	}



