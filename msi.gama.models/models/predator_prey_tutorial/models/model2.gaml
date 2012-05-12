
model prey_predator
//Model 2 of the predator/prey tutorial

global {
	int nb_preys_init <- 200 min: 1 max: 1000 parameter: 'Initial number of preys: ' category: 'Prey' ;
	init {
		create prey number: nb_preys_init ;
	}
}
entities {
	species prey {
		const size type: float <- 2.0 ;
		const color type: rgb <- rgb('blue') ;
		vegetation_cell myCell <- one_of (vegetation_cell as list) ;
		
		init {
			set location <- myCell.location;
		}
		
		aspect base {
			draw shape: circle size: size color: color ;
		}
	}
}
environment width: 100 height: 100 {
	grid vegetation_cell width: 50 height: 50 neighbours: 4 {
		float maxFood <- 1.0 ;
		float foodProd <- (rnd(1000) / 1000) * 0.01 ;
		float food <- (rnd(1000) / 1000) update: min([maxFood, food + foodProd]) ;
		rgb color <- rgb([255 * (1 - food), 255, 255 * (1 - food)]) update: rgb([255 * (1 - food), 255, 255 * (1 - food)]) ;
	}
}
output {
	display main_display {
		grid vegetation_cell lines: rgb('black') ;
		species prey aspect: base ;
	}
}
 