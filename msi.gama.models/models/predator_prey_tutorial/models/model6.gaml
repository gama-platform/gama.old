
model prey_predator
//Model 6 of the predator/prey tutorial

global {
	int nb_preys_init <- 200 min: 1 max: 1000 ;
	int nb_predators_init <- 20 min: 0 max: 200;
	float prey_max_energy <- 1.0;
	float prey_max_transfert <- 0.1 ;
	float prey_energy_consum <- 0.05;
	float predator_max_energy <- 1.0;
	float predator_energy_transfert <- 0.5;
	float predator_energy_consum <- 0.02;
	float prey_proba_reproduce <- 0.01;
	int prey_nb_max_offsprings <- 5; 
	float prey_energy_reproduce <- 0.5; 
	float predator_proba_reproduce <- 0.01;
	int predator_nb_max_offsprings <- 3;
	float predator_energy_reproduce <- 0.5;
	int nb_preys function: {length (prey as list)};
	int nb_predators function: {length (predator as list)};
	
	init {
		create prey number: nb_preys_init ;
		create predator number: nb_predators_init ;
	}
}
entities {
	species generic_species {
		const size type: float <- 2.0 ;
		const color type: rgb <- rgb('blue') ;
		const max_energy type: float init: prey_max_energy ;
		const max_transfert type: float init: prey_max_transfert ;
		const energy_consum type: float init: prey_energy_consum ;
		const proba_reproduce type: float ;
		const nb_max_offsprings type: int ;
		const energy_reproduce type: float ;
		vegetation_cell myCell <- one_of (vegetation_cell as list) ;
		float energy <- (rnd(1000) / 1000) * max_energy  update: energy - energy_consum max: max_energy ;
		init {
			set location <- myCell.location;
		}
		reflex basic_move {
			set myCell <- one_of (myCell.neighbours) ;
			set location <- myCell.location ;
		}
		reflex die when: energy <= 0 {
			do die ;
		} 
		reflex reproduce when: (energy >= energy_reproduce) and (flip(proba_reproduce)) {
			let nb_offsprings type: int <- 1 + rnd(nb_max_offsprings -1);
			create species: species(self) number: nb_offsprings {
				set myCell <- myself.myCell ;
				set location <- myCell.location ;
				set energy <- myself.energy / nb_offsprings ;
			}
			set energy <- energy / nb_offsprings ;
		}
		aspect base {
			draw shape: circle size: size color: color ;
		}
	}
	species prey parent: generic_species {
		const color type: rgb <- rgb('blue') ;
		const max_energy type: float <- prey_max_energy ;
		const max_transfert type: float <- prey_max_transfert ;
		const energy_consum type: float <- prey_energy_consum ;
		const proba_reproduce type: float <- prey_proba_reproduce ;
		const nb_max_offsprings type: int <- prey_nb_max_offsprings ;
		const energy_reproduce type: float <- prey_energy_reproduce ;
		
		reflex eat when: myCell.food > 0 {
			let energy_transfert type: float <- min([max_transfert, myCell.food]) ;
			set myCell.food <- myCell.food - energy_transfert ;
			set energy <- energy + energy_transfert ;
		}
	}
	species predator parent: generic_species {
		const color type: rgb <- rgb('red') ;
		const max_energy type: float <- predator_max_energy ;
		const energy_transfert type: float <- predator_energy_transfert ;
		const energy_consum type: float <- predator_energy_consum ;
		const proba_reproduce type: float <- predator_proba_reproduce ;
		const nb_max_offsprings type: int <- predator_nb_max_offsprings ;
		const energy_reproduce type: float <- predator_energy_reproduce ;
		list reachable_preys of: prey update: prey inside (myCell);
		
		reflex eat when: ! empty(reachable_preys) {
			ask one_of (reachable_preys) {
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
		rgb color <- rgb([255 * (1 - food), 255, 255 * (1 - food)]) update: rgb([255 * (1 - food), 255, 255 * (1 - food)]) ;
		list neighbours of: vegetation_cell <- (self neighbours_at 2) of_species vegetation_cell;
	}
}
 
experiment prey_predator type: gui {
	parameter 'Initial number of preys: ' var: nb_preys_init category: 'Prey' ;
	parameter 'Prey max energy: ' var: prey_max_energy category: 'Prey' ;
	parameter 'Prey max transfert: ' var: prey_max_transfert  category: 'Prey' ;
	parameter 'Prey energy consumption: ' var: prey_energy_consum  category: 'Prey' ;
	parameter 'Initial number of predators: ' var: nb_predators_init category: 'Predator' ;
	parameter 'Predator max energy: ' var: predator_max_energy category: 'Predator' ;
	parameter 'Predator energy transfert: ' var: predator_energy_transfert  category: 'Predator' ;
	parameter 'Predator energy consumption: ' var: predator_energy_consum  category: 'Predator' ;
	parameter 'Prey probability reproduce: ' var: prey_proba_reproduce category: 'Prey' ;
	parameter 'Prey nb max offsprings: ' var: prey_nb_max_offsprings category: 'Prey' ;
	parameter 'Prey energy reproduce: ' var: prey_energy_reproduce category: 'Prey' ;
	parameter 'Predator probability reproduce: ' var: predator_proba_reproduce category: 'Predator' ;
	parameter 'Predator nb max offsprings: ' var: predator_nb_max_offsprings category: 'Predator' ;
	parameter 'Predator energy reproduce: ' var: predator_energy_reproduce category: 'Predator' ;
	
	output {
		display main_display {
			grid vegetation_cell lines: rgb('black') ;
			species prey aspect: base ;
			species predator aspect: base ;
		}
		monitor number_of_preys value: nb_preys refresh_every: 1 ;
		monitor number_of_predators value: nb_predators refresh_every: 1 ;
	}
}
 


