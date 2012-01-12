model prey_predator
//Model 1 of the predator/prey tutorial

global {
	var nb_preys_init type: int init: 200 min: 1 max: 1000 parameter: 'Initial number of preys: ' category: 'Prey' ;
	var nb_preys type: int value: length (prey as list) init: nb_preys_init ;
	init {
		create species: prey number: nb_preys ;
	}
}
entities {
	species prey {
		const size type: float init: 2 ;
		const color type: rgb init: 'blue' ;
		
		aspect base {
			draw shape: circle size: size color: color ;
		}
	}
}
environment width: 100 height: 100 ;
output {
	display main_display {
		species prey aspect: base ;
	}
}
