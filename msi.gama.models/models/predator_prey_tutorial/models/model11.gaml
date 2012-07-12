
model prey_predator
//Model 11 of the predator/prey tutorial

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
	
	reflex stop_simulation when: (nb_preys = 0) or (nb_predators = 0) {
		do halt ;
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
		const my_icon type: string;
		vegetation_cell myCell <- one_of (vegetation_cell as list) ;
		float energy <- (rnd(1000) / 1000) * max_energy  update: energy - energy_consum max: max_energy ;
		init {
			set location <- myCell.location;
		}
		reflex basic_move {
			do choose_cell ;
			set location <- myCell.location ;
		}
		action choose_cell ;
		reflex die when: energy <= 0 {
			do die ;
		} 
		reflex reproduce when: (energy >= energy_reproduce) and (flip(proba_reproduce)) {
			let nb_offsprings type: int <- 1 + rnd(nb_max_offsprings -1);
			create species(self) number: nb_offsprings {
				set myCell <- myself.myCell ;
				set location <- myCell.location ;
				set energy <- myself.energy / nb_offsprings ;
			}
			set energy <- energy / nb_offsprings ;
		}
		aspect base {
			draw shape: circle size: size color: color ;
		}
		aspect icon {
			draw image: my_icon size: size ;
		}
		aspect info {
			draw shape: square size: size color: color ;
			draw text: string(energy with_precision 2) size: 3 color: rgb('black') ;
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
		const my_icon type: string <- '../includes/data/sheep.png' ;
		
		reflex eat when: myCell.food > 0 {
			let energy_transfert type: float <- min([max_transfert, myCell.food]) ;
			set myCell.food <- myCell.food - energy_transfert ;
			set energy <- energy + energy_transfert ;
		}
		
		action choose_cell {
			set myCell <- (myCell.neighbours) with_max_of (each.food);
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
		const my_icon type: string <- '../includes/data/wolf.png' ;
		
		list reachable_preys of: prey update: prey inside (myCell);
		
		reflex eat when: ! empty(reachable_preys) {
			ask one_of (reachable_preys) {
				do die ;
			}
			set energy <- energy + energy_transfert ;
		}
		action choose_cell {
			let myCell_tmp type: vegetation_cell <- shuffle(myCell.neighbours) first_with (!(empty (prey inside (each))));
			if myCell_tmp != nil {
				set myCell <- myCell_tmp;
			} else {
				set myCell <- one_of (myCell.neighbours);
			} 
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
			species prey aspect: icon ;
			species predator aspect: icon ;
		}
		display Population_information refresh_every: 5 {
			chart "Species evolution" type: series background: rgb('white') size: {1,0.4} position: {0, 0.05} {
				data number_of_preys value: nb_preys color: rgb('blue') ;
				data number_of_predator value: nb_predators color: rgb('red') ;
			}
			chart "Prey Energy Distribution" type: histogram background: rgb('lightGray') size: {0.5,0.4} position: {0, 0.5} {
				data "]0;0.25]" value: (prey as list) count (each.energy <= 0.25) ;
				data "]0.25;0.5]" value: (prey as list) count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
				data "]0.5;0.75]" value: (prey as list) count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
				data "]0.75;1]" value: (prey as list) count (each.energy > 0.75) ;
			}
			chart "Predator Energy Distribution" type: histogram background: rgb('lightGray') size: {0.5,0.4} position: {0.5, 0.5} {
				data "]0;0.25]" value: (predator as list) count (each.energy <= 0.25) ;
				data "]0.25;0.5]" value: (predator as list) count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
				data "]0.5;0.75]" value: (predator as list) count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
				data "]0.75;1]" value: (predator as list) count (each.energy > 0.75) ;
			}
		}
		file  "results" type: text data: 'cycle: '+ time
						            + '; nbPreys: ' + nb_preys
						            + '; minEnergyPreys: ' + ((prey as list) min_of each.energy)
								    + '; maxSizePreys: ' + ((prey as list) max_of each.energy) 
	   							    + '; nbPredators: ' + nb_predators           
	   							    + '; minEnergyPredators: ' + ((predator as list) min_of each.energy)          
	   							    + '; maxSizePredators: ' + ((predator as list) max_of each.energy) ;
		monitor number_of_preys value: nb_preys refresh_every: 1 ;
		monitor number_of_predators value: nb_predators refresh_every: 1 ;
	}
}