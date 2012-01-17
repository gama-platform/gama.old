model prey_predator
//Model 6 of the predator/prey tutorial

global {
	var nb_preys_init type: int init: 200 min: 1 max: 1000 parameter: 'Initial number of preys: ' category: 'Prey' ;
	var nb_predator_init type: int init: 20 min: 0 max: 200 parameter: 'Initial number of predators ' category: 'Predator' ;
	var prey_max_energy type: float init: 1 parameter: 'Prey max energy: ' category: 'Prey' ;
	var prey_max_transfert type: float init: 0.1 parameter: 'Prey max transfert: ' category: 'Prey' ;
	var prey_energy_consum type: float init: 0.05 parameter: 'Prey energy consumption: ' category: 'Prey' ;
	var prey_energy_reproduce type: float init: 0.5 parameter: 'Prey energy reproduce: ' category: 'Prey' ;
	var prey_proba_reproduce type: float init: 0.01 parameter: 'Prey probability reproduce: ' category: 'Prey' ;
	var prey_nb_max_offsprings type: int init: 5 parameter: 'Prey nb max offsprings: ' category: 'Prey' ;
	var predator_max_energy type: float init: 1 parameter: 'Predator max energy: ' category: 'Predator' ;
	var predator_energy_transfert type: float init: 0.5 parameter: 'Predator energy transfert: ' category: 'Predator' ;
	var predator_energy_consum type: float init: 0.02 parameter: 'Predator energy consumption: ' category: 'Predator' ;
	var predator_energy_reproduce type: float init: 0.5 parameter: 'Predator energy reproduce: ' category: 'Predator' ;
	var predator_proba_reproduce type: float init: 0.01 parameter: 'Predator probability reproduce: ' category: 'Predator' ;
	var predator_nb_max_offsprings type: int init: 3 parameter: 'Predator nb max offsprings: ' category: 'Predator' ;
	var nb_preys type: int value: length (prey as list) init: nb_preys_init ;
	var nb_predators type: int value: length (predator as list) init: nb_predator_init ;
	init {
		create species: prey number: nb_preys_init ;
		create species: predator number: nb_predator_init ;
	}
}
entities {
	species generic_species {
		const size type: float init: 2 ;
		const color type: rgb;
		const max_energy type: float;
		const energy_consum type: float;
		const energy_reproduce type: float ;
		const proba_reproduce type: float ;
		const nb_max_offsprings type: int ;
		var myCell type: vegetation_cell init: one_of (vegetation_cell as list) ;
		var energy type: float init: (rnd(1000) / 1000) * max_energy  value: energy - energy_consum max: max_energy ;
		init {
			set location value: myCell.location ;
			set energy value: (rnd(1000) / 1000) * max_energy ;
		}
		reflex basic_move {
			set myCell value: one_of (myCell.neighbours) ;
			set location value: myCell.location ;
		}
		reflex die when: energy <= 0 {
			do action: die ;
		}
		reflex reproduce when: (energy >= energy_reproduce) and (flip(proba_reproduce)) {
			let nb_offsprings type: int value: 1 + rnd(nb_max_offsprings -1);
			create species: species(self) number: nb_offsprings {
				set myCell value: myself.myCell ;
				set location value: myCell.location ;
				set energy value: myself.energy / nb_offsprings ;
			}
			set energy value: energy / nb_offsprings ;
		}
		aspect base {
			draw shape: circle size: size color: color ;
		}
	}
	species prey parent: generic_species {
		const color type: rgb init: 'blue' ;
		const max_energy type: float init: prey_max_energy ;
		const max_transfert type: float init: prey_max_transfert ;
		const energy_consum type: float init: prey_energy_consum ;
		const energy_reproduce type: float init: prey_energy_reproduce ;
		const proba_reproduce type: float init: prey_proba_reproduce ;
		const nb_max_offsprings type: int init: prey_nb_max_offsprings ;
		reflex eat when: myCell.food > 0 {
			let energy_transfert value: min [max_transfert, myCell.food] ;
			set myCell.food value: myCell.food - energy_transfert ;
			set energy value: energy + energy_transfert ;
		}
	}
	species predator parent: generic_species {
		const color type: rgb init: 'red' ;
		const max_energy type: float init: predator_max_energy ;
		const energy_transfert type: float init: predator_energy_transfert ;
		const energy_consum type: float init: predator_energy_consum ;
		const energy_reproduce type: float init: predator_energy_reproduce ;
		const proba_reproduce type: float init: predator_proba_reproduce ;
		const nb_max_offsprings type: int init: predator_nb_max_offsprings ;
		reflex eat when: !(empty (agents_inside(myCell) of_species prey)) {
			ask target: one_of (agents_inside(myCell) of_species prey) {
				do action: die ;
			}
			set energy value: energy + energy_transfert ;
		}
	}
}
environment width: 100 height: 100 {
	grid vegetation_cell width: 50 height: 50 neighbours: 4 {
		const maxFood type: float init: 1.0 ;
		const foodProd type: float init: (rnd(1000) / 1000) * 0.01 ;
		var food type: float init: (rnd(1000) / 1000) value: min [maxFood, food + foodProd] ;
		var color type: rgb value: [255 * (1 - food), 255, 255 * (1 - food)] init: [255 * (1 - food), 255, 255 * (1 - food)] ;
		var neighbours type: list of: vegetation_cell init: (self neighbours_at 2) of_species vegetation_cell;
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
