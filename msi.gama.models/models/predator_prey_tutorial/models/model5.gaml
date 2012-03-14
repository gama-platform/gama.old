
model prey_predator
//Model 5 of the predator/prey tutorial

global {
	int nb_preys_init <- 200 min: 1 max: 1000 parameter: 'Initial number of preys: ' category: 'Prey' ;
	int nb_predator_init <- 20 min: 0 max: 200 parameter: 'Initial number of predators ' category: 'Predator' ;
	float prey_max_energy <- 1 parameter: 'Prey max energy: ' category: 'Prey' ;
	float prey_max_transfert <- 0.1 parameter: 'Prey max transfert: ' category: 'Prey' ;
	float prey_energy_consum <- 0.05 parameter: 'Prey energy consumption: ' category: 'Prey' ;
	float predator_max_energy <- 1 parameter: 'Predator max energy: ' category: 'Predator' ;
	float predator_energy_transfert <- 0.5 parameter: 'Predator energy transfert: ' category: 'Predator' ;
	float predator_energy_consum <- 0.02 parameter: 'Predator energy consumption: ' category: 'Predator' ;
	int nb_preys function: {length (prey as list)};
	int nb_predators function: {length (predator as list)};
	
	init {
		create prey number: nb_preys_init ;
		create predator number: nb_predator_init ;
	}
}
entities {
	species generic_species {
		const size type: float <- 2 ;
		const color type: rgb <- 'blue' ;
		const max_energy type: float init: prey_max_energy ;
		const max_transfert type: float init: prey_max_transfert ;
		const energy_consum type: float init: prey_energy_consum ;
		
		vegetation_cell myCell <- one_of (vegetation_cell as list) ;
		float energy <- (rnd(1000) / 1000) * max_energy  update: energy - energy_consum max: max_energy ;
		
		init {
			set location <- myCell.location;
		}
		
		reflex basic_move {
			set myCell <- one_of (myCell.neighbours) ;
			set location <- myCell.location ;
		}
		reflex eat when: myCell.food > 0 {
			let energy_transfert type: float <- min([max_transfert, myCell.food]) ;
			set myCell.food <- myCell.food - energy_transfert ;
			set energy <- energy + energy_transfert ;
		}
		reflex die when: energy <= 0 {
			do die ;
		}

		aspect base {
			draw shape: circle size: size color: color ;
		}
	}
	species prey parent: generic_species {
		const color type: rgb <- 'blue' ;
		const max_energy type: float <- prey_max_energy ;
		const max_transfert type: float <- prey_max_transfert ;
		const energy_consum type: float <- prey_energy_consum ;
		
		reflex eat when: myCell.food > 0 {
			let energy_transfert type: float <- min([max_transfert, myCell.food]) ;
			set myCell.food <- myCell.food - energy_transfert ;
			set energy <- energy + energy_transfert ;
		}
	}
	species predator parent: generic_species {
		const color type: rgb <- 'red' ;
		const max_energy type: float <- predator_max_energy ;
		const energy_transfert type: float <- predator_energy_transfert ;
		const energy_consum type: float <- predator_energy_consum ;
		reflex eat when: !(empty (agents_inside(myCell) of_species prey)) {
			ask one_of (agents_inside(myCell) of_species prey) {
				do die ;
			}
			set energy <- energy + energy_transfert ;
		}
	}
}
environment width: 100 height: 100 {
	grid vegetation_cell width: 50 height: 50 neighbours: 4 {
		float maxFood <- 1.0 ;
		float foodProd <- (rnd(1000) / 1000) * 0.01 ;
		float food <- (rnd(1000) / 1000) update: min([maxFood, food + foodProd]) ;
		rgb color <- [255 * (1 - food), 255, 255 * (1 - food)] update: [255 * (1 - food), 255, 255 * (1 - food)] ;
		list neighbours of: vegetation_cell <- (self neighbours_at 2) of_species vegetation_cell;
	}
}
output {
	display main_display {
		grid vegetation_cell lines: 'black' ;
		species prey aspect: base ;
		species predator aspect: base ;
	}
	monitor number_of_preys value: nb_preys refresh_every: 1 ;
	monitor number_of_predators value: nb_predators refresh_every: 1 ;
}
 

