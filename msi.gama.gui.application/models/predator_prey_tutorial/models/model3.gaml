model prey_predator
// gen by Xml2Gaml
import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	var nb_preys_init type: int init: 200 min: 1 max: 1000 parameter: 'Initial number of preys: ' category: 'Prey' ;
	var prey_max_energy type: float init: 1 parameter: 'Prey max energy: ' category: 'Prey' ;
	var prey_max_transfert type: float init: 0.1 parameter: 'Prey max transfert: ' category: 'Prey' ;
	var prey_energy_consum type: float init: 0.05 parameter: 'Prey energy consumption: ' category: 'Prey' ;
	var nb_preys type: int value: length (prey as list) init: nb_preys_init ;
	init {
		create species: prey number: nb_preys ;
	}
}
entities {
	species prey {
		const size type: float init: 2 ;
		const color type: rgb init: 'blue' ;
		const max_energy type: float init: prey_max_energy ;
		const max_transfert type: float init: prey_max_transfert ;
		const energy_consum type: float init: prey_energy_consum ;
		var myCell type: the_grid init: one_of (the_grid as list) ;
		var energy type: float init: (rnd(1000) / 1000) * max_energy  value: energy - energy_consum max: max_energy ;
		init {
			set location value: myCell.location;
		}
		reflex basic_move {
			set myCell value: one_of (myCell.neighbours) ;
			set location value: myCell.location ;
		}
		reflex eat when: myCell.food > 0 {
			let energy_transfert type: float value: min [max_transfert, myCell.food] ;
			set myCell.food value: myCell.food - energy_transfert ;
			set energy value: energy + energy_transfert ;
		}
		reflex die when: energy <= 0 {
			do action: die ;
		}
		aspect base {
			draw shape: circle size: size color: color ;
		}
	}
}
environment width: 100 height: 100 {
	grid the_grid width: 50 height: 50 neighbours: 4 {
		const maxFood type: float init: 1.0 ;
		const foodProd type: float init: (rnd(1000) / 1000) * 0.01 ;
		var food type: float init: (rnd(1000) / 1000) value: min [maxFood, food + foodProd] ;
		var color type: rgb value: [255 * (1 - food), 255, 255 * (1 - food)] init: [255 * (1 - food), 255, 255 * (1 - food)] ;
		var neighbours type: list of: the_grid init: (self neighbours_at 2) of_species the_grid;
	}
}
output {
	display main_display {
		grid the_grid lines: 'black' ;
		species prey aspect: base ;
	}
}
