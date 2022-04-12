/**
* Name: Behavior of the prey agent
* Author:
* Description: Third part of the tutorial : Predator Prey
* Tags:
*/
model prey_predator

global {
	int nb_preys_init <- 200;
	float prey_max_energy <- 1.0;
	float prey_max_transfer <- 0.1;
	float prey_energy_consum <- 0.05;
	
	init {
		create prey number: nb_preys_init;
	}
}

species prey {
	float size <- 1.0;
	rgb color <- #blue;
	float max_energy <- prey_max_energy;
	float max_transfer <- prey_max_transfer;
	float energy_consum <- prey_energy_consum;
		
	vegetation_cell my_cell <- one_of (vegetation_cell); 
	float energy <- rnd(max_energy)  update: energy - energy_consum max: max_energy;
		
	init { 
		location <- my_cell.location;
	}

	reflex basic_move {
		my_cell <- one_of(my_cell.neighbors2);
		location <- my_cell.location;
	}
	reflex eat when: my_cell.food > 0 { 
		float energy_transfer <- min([max_transfer, my_cell.food]);
		my_cell.food <- my_cell.food - energy_transfer;
		energy <- energy + energy_transfer;
	}
	reflex die when: energy <= 0 {
		do die;
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
	list<vegetation_cell> neighbors2  <- (self neighbors_at 2);
}

experiment prey_predator type: gui {
	parameter "Initial number of preys: " var: nb_preys_init min: 1 max: 1000 category: "Prey";
	parameter "Prey max energy: " var: prey_max_energy category: "Prey";
	parameter "Prey max transfer: " var: prey_max_transfer  category: "Prey";
	parameter "Prey energy consumption: " var: prey_energy_consum  category: "Prey";
	output {
		display main_display {
			grid vegetation_cell border: #black;
			species prey aspect: base;
		}
	}
}
