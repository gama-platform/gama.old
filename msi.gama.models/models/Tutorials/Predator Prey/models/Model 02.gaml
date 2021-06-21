/**
* Name: Dynamic of the vegetation (grid)
* Author:
* Description: Second part of the tutorial : Predator Prey
* Tags: grid
*/
model prey_predator

global {
	int nb_preys_init <- 200;
	init {
		create prey number: nb_preys_init;
	}
}

species prey {
	float size <- 1.0;
	rgb color <- #blue;
	vegetation_cell my_cell <- one_of (vegetation_cell);
		
	init {
		location <- my_cell.location;
	}

	aspect base {
		draw circle(size) color: color;
	}
}

grid vegetation_cell width: 50 height: 50 neighbors: 4 {
	float max_food <- 1.0;
	float food_prod <- rnd(0.01);
	float food <- rnd(1.0) max: max_food update: food + food_prod;
	rgb color <- rgb(int(255 * (1 - food)), 255, int(255 * (1 - food))) update: rgb(int(255 * (1 - food)), 255, int(255 * (1 - food)));
}

experiment prey_predator type: gui {
	parameter "Initial number of preys: " var: nb_preys_init min: 1 max: 1000 category: "Prey";
	output {
		display main_display {
			grid vegetation_cell border: #black;
			species prey aspect: base;
		}
	}
}
