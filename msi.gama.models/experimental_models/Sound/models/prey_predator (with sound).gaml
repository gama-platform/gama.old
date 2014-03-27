
model prey_predator
//Model 9 of the predator/prey tutorial

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
	
	int nb_preys -> {length (prey)};
	int nb_predators -> {length (predator)};
	
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
		const size type: float <- 1.0 ;
		const color type: rgb <- rgb("blue") ;
		const max_energy type: float init: prey_max_energy ;
		const max_transfert type: float init: prey_max_transfert ;
		const energy_consum type: float init: prey_energy_consum ;
		vegetation_cell myCell <- one_of (vegetation_cell) ;
		float energy <- (rnd(1000) / 1000) * max_energy  update: energy - energy_consum max: max_energy ;
		const proba_reproduce type: float ;
		const nb_max_offsprings type: int ;
		const energy_reproduce type: float ;
		const my_icon type: file;
		
		init {
			location <- myCell.location;
		}
		reflex basic_move {
			myCell <- self choose_cell() ;
			location <- myCell.location ;
		} 
		vegetation_cell choose_cell {return nil;}
		reflex die when: energy <= 0 {
			do die ;
		}
		reflex reproduce when: (energy >= energy_reproduce) and (flip(proba_reproduce)) {
			int nb_offsprings <- 1 + rnd(nb_max_offsprings -1);
			create species(self) number: nb_offsprings {
				myCell <- myself.myCell ;
				location <- myCell.location ;
				energy <- myself.energy / nb_offsprings ;
			}
			energy <- energy / nb_offsprings ;
		}
		aspect base {
			draw circle(size) color: color ;
		}
		aspect icon {
			draw my_icon size: 2 * size ;
		}
		aspect info {
			draw square(size) color: color ;
			draw string(energy with_precision 2) size: 3 color: rgb("black") ;
		}
	}
	species prey parent: generic_species {
		const color type: rgb <- rgb("blue") ;
		const max_energy type: float <- prey_max_energy ;
		const max_transfert type: float <- prey_max_transfert ;
		const energy_consum type: float <- prey_energy_consum ;
		const proba_reproduce type: float <- prey_proba_reproduce ;
		const nb_max_offsprings type: int <- prey_nb_max_offsprings ;
		const energy_reproduce type: float <- prey_energy_reproduce ;
		const my_icon type: file <- file("../includes/data/sheep.png") ;
		
		reflex eat when: myCell.food > 0 {
			float energy_transfert <- min([max_transfert, myCell.food]) ;
			myCell.food <- myCell.food - energy_transfert ;
			energy <- energy + energy_transfert ;
		}
		
		vegetation_cell choose_cell {
			return (myCell.neighbours) with_max_of (each.food);
		}
	}
	species predator parent: generic_species {
		const color type: rgb <- rgb("red") ;
		const max_energy type: float <- predator_max_energy ;
		const energy_transfert type: float <- predator_energy_transfert ;
		const energy_consum type: float <- predator_energy_consum ;
		const proba_reproduce type: float <- predator_proba_reproduce ;
		const nb_max_offsprings type: int <- predator_nb_max_offsprings ;
		const energy_reproduce type: float <- predator_energy_reproduce ;
		const my_icon type: file <- file("../includes/data/wolf.png") ;
		list<prey> reachable_preys update: prey inside (myCell);
		
		reflex eat when: ! empty(reachable_preys) {
			ask one_of (reachable_preys) {
				do die ;
			}
			energy <- energy + energy_transfert ;
			
			start_sound source: '../includes/sounds/predator_eat_prey_sound.mp3';
		}
		
		vegetation_cell choose_cell {
			vegetation_cell myCell_tmp <- shuffle(myCell.neighbours) first_with (!(empty (prey inside (each))));
			if myCell_tmp != nil {
				return myCell_tmp;
			} else {
				return one_of (myCell.neighbours);
			} 
		}
	}
	grid vegetation_cell width: 50 height: 50 neighbours: 4 {
		float maxFood <- 1.0 ;
		float foodProd <- (rnd(1000) / 1000) * 0.01 ;
		float food <- (rnd(1000) / 1000) max: maxFood update: food + foodProd ;
		rgb color <- rgb(int(255 * (1 - food)), 255, int(255 * (1 - food))) update: rgb(int(255 * (1 - food)), 255, int(255 *(1 - food))) ;
		list<vegetation_cell> neighbours  <- (self neighbours_at 2);
	}
}

experiment prey_predator_sound type: gui {
	parameter "Initial number of preys: " var: nb_preys_init category: "Prey" ;
	parameter "Prey max energy: " var: prey_max_energy category: "Prey" ;
	parameter "Prey max transfert: " var: prey_max_transfert  category: "Prey" ;
	parameter "Prey energy consumption: " var: prey_energy_consum  category: "Prey" ;
	parameter "Initial number of predators: " var: nb_predators_init category: "Predator" ;
	parameter "Predator max energy: " var: predator_max_energy category: "Predator" ;
	parameter "Predator energy transfert: " var: predator_energy_transfert  category: "Predator" ;
	parameter "Predator energy consumption: " var: predator_energy_consum  category: "Predator" ;
	parameter 'Prey probability reproduce: ' var: prey_proba_reproduce category: 'Prey' ;
	parameter 'Prey nb max offsprings: ' var: prey_nb_max_offsprings category: 'Prey' ;
	parameter 'Prey energy reproduce: ' var: prey_energy_reproduce category: 'Prey' ;
	parameter 'Predator probability reproduce: ' var: predator_proba_reproduce category: 'Predator' ;
	parameter 'Predator nb max offsprings: ' var: predator_nb_max_offsprings category: 'Predator' ;
	parameter 'Predator energy reproduce: ' var: predator_energy_reproduce category: 'Predator' ;
	
	output {
		display main_display {
			grid vegetation_cell lines: rgb("black") ;
			species prey aspect: icon ;
			species predator aspect: icon ;
		}
		display info_display {
			grid vegetation_cell lines: rgb("black") ;
			species prey aspect: info ;
			species predator aspect: info ;
		}
		monitor number_of_preys value: nb_preys refresh_every: 1 ;
		monitor number_of_predators value: nb_predators refresh_every: 1 ;
	}
}