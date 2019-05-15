
model prey_predator
//Model 9 of the predator/prey tutorial

global {
	int nb_preys_init <- 200 min: 1 max: 1000  parameter: 'Initial number of preys: ' category: 'Prey' ;
	int nb_predator_init <- 20 min: 0 max: 20000  parameter: 'Initial number of predators ' category: 'Predator' ;
	float prey_max_energy <- 1.0 parameter: 'Prey max energy: ' category: 'Prey' ;
	float prey_max_transfert <- 0.1 parameter: 'Prey max transfert: ' category: 'Prey' ;
	float prey_energy_consum <- 0.05 parameter: 'Prey energy consumption: ' category: 'Prey' ;
	float prey_proba_reproduce <- 0.01 parameter: 'Prey probability reproduce: ' category: 'Prey' ;
	int prey_nb_max_offsprings <- 5 parameter: 'Prey nb max offsprings: ' category: 'Prey' ;
	float prey_energy_reproduce <- 0.5 parameter: 'Prey energy reproduce: ' category: 'Prey' ;
	float predator_max_energy <- 1.0 parameter: 'Predator max energy: ' category: 'Predator' ;
	float predator_energy_transfert <- 0.5 parameter: 'Predator energy transfert: ' category: 'Predator' ;
	float predator_energy_consum <- 0.02 parameter: 'Predator energy consumption: ' category: 'Predator' ;
	float predator_proba_reproduce <- 0.01 parameter: 'Predator probability reproduce: ' category: 'Predator' ;
	int predator_nb_max_offsprings <- 3 parameter: 'Predator nb max offsprings: ' category: 'Predator' ;
	float predator_energy_reproduce <- 0.5 parameter: 'Predator energy reproduce: ' category: 'Predator' ;
	int nb_preys function: {length (prey as list)};
	int nb_predators function: {length (predator as list)};
	
	init {
		create prey number: nb_preys_init ;
		create predator number: nb_predator_init ;
do write {
				arg message value: 'nb predatir...' + nb_predator_init ;
			}
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
		reflex eat when: myCell.food > 0 {
			let energy_transfert type: float <- min([max_transfert, myCell.food]) ;
			set myCell.food <- myCell.food - energy_transfert ;
			set energy <- energy + energy_transfert ;
		}
		reflex die when: energy <= 0 {
			do die ;
		} 

		reflex write when: false
		{
			do write {
				arg message value: 'Timetable loading...' + time ;
			}
	
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
			draw circle(size) color: color ;
		}
		aspect icon {
			draw image: my_icon size: size ;
		}
		aspect info {
			draw square(size) color: color ;
			draw text: "" + energy with_precision 2 size: 3 color: rgb('black') ;
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
		
		reflex eat when: !(empty (agents_inside(myCell) of_species prey)) {
			ask one_of (agents_inside(myCell) of_species prey) {
				do die ;
			}
			set energy <- energy + energy_transfert ;
		}
		action choose_cell {
			let myCell_tmp type: vegetation_cell <- shuffle(myCell.neighbours) first_with (!(empty (agents_inside(each) of_species prey)));
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

experiment preyPred type: gui {
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
