/**
* Name: Predator agents (parent species)
* Author:
* Description: 5th part of the tutorial : Predator Prey
* Tags: inheritance
*/
model prey_predator

global {
	int nb_preys_init <- 200;
	int nb_predators_init <- 20;
	float prey_max_energy <- 1.0;
	float prey_max_transfer <- 0.1;
	float prey_energy_consum <- 0.05;
	float predator_max_energy <- 1.0;
	float predator_energy_transfer <- 0.5;
	float predator_energy_consum <- 0.02;
	int nb_preys -> {length(prey)};
	int nb_predators -> {length(predator)};

	init {
		create prey number: nb_preys_init;
		create predator number: nb_predators_init;
	}
}

species generic_species {
	float size <- 1.0;
	rgb color;
	float max_energy;
	float max_transfer;
	float energy_consum;
	vegetation_cell my_cell <- one_of (vegetation_cell);
	float energy <- rnd(max_energy) update: energy - energy_consum max: max_energy;
	
	init {
		location <- my_cell.location;
	}

	reflex basic_move {
		my_cell <- one_of(my_cell.neighbors2);
		location <- my_cell.location;
	}

	reflex eat {
		energy <- energy + energy_from_eat();
	}

	reflex die when: energy <= 0 {
		do die;
	}

	float energy_from_eat {
		return 0.0;
	} 

	aspect base {
		draw circle(size) color: color;
	}
}

species prey parent: generic_species {
	rgb color <- #blue;
	float max_energy <- prey_max_energy;
	float max_transfer <- prey_max_transfer;
	float energy_consum <- prey_energy_consum;
	
	float energy_from_eat {
		float energy_transfer <- 0.0;
		if(my_cell.food > 0) {
			energy_transfer <- min([max_transfer, my_cell.food]);
			my_cell.food <- my_cell.food - energy_transfer;
		} 			
		return energy_transfer;
	}
}

species predator parent: generic_species {
	rgb color <- #red;
	float max_energy <- predator_max_energy;
	float energy_transfer <- predator_energy_transfer;
	float energy_consum <- predator_energy_consum;
		
	float energy_from_eat {
		list<prey> reachable_preys <- prey inside (my_cell);	
		if(! empty(reachable_preys)) {
			ask one_of (reachable_preys) {
				do die;
			}
			return energy_transfer;
		}
		return 0.0;
	}		
}

grid vegetation_cell width: 50 height: 50 neighbors: 4 {
	float max_food <- 1.0;
	float food_prod <- rnd(0.01);
	float food <- rnd(1.0) max: max_food update: food + food_prod;
	rgb color <- rgb(int(255 * (1 - food)), 255, int(255 * (1 - food))) update: rgb(int(255 * (1 - food)), 255, int(255 *(1 - food)));
	list<vegetation_cell> neighbors2  <- (self neighbors_at 2); 
}

experiment prey_predator type: gui {
	parameter "Initial number of preys: " var: nb_preys_init min: 0 max: 1000 category: "Prey";
	parameter "Prey max energy: " var: prey_max_energy category: "Prey";
	parameter "Prey max transfer: " var: prey_max_transfer category: "Prey";
	parameter "Prey energy consumption: " var: prey_energy_consum category: "Prey";
	parameter "Initial number of predators: " var: nb_predators_init min: 0 max: 200 category: "Predator";
	parameter "Predator max energy: " var: predator_max_energy category: "Predator";
	parameter "Predator energy transfer: " var: predator_energy_transfer category: "Predator";
	parameter "Predator energy consumption: " var: predator_energy_consum category: "Predator";
	
	output {
		display main_display {
			grid vegetation_cell border: #black;
			species prey aspect: base;
			species predator aspect: base;
		}

		monitor "Number of preys" value: nb_preys;
		monitor "Number of predators" value: nb_predators;
	}
}
