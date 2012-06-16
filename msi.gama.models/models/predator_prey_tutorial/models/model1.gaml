model prey_predator
//Model 1 of the predator/prey tutorial

global {
	int nb_preys_init <- 200 min: 1 max: 1000 ;
	init {
		create prey number: nb_preys_init ;
	}
}
entities {
	species prey {
		const size type: float <- 2.0 ;
		const color type: rgb <- rgb('blue');
		
		aspect base {
			draw shape: circle size: size color: color ;
		}
	}
} 
environment width: 100 height: 100 ;

experiment prey_predator type: gui {
	parameter 'Initial number of preys: ' var: nb_preys_init category: 'Prey' ;
	output {
		display main_display {
			species prey aspect: base ;
		}
	}
}

 