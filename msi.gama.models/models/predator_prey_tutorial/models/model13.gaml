
model prey_predator
//Model 13 of the predator/prey tutorial

global {
	file map_init <- file('../includes/gis/vegetation.shp') parameter: 'Initial environement: ' category: 'Environment' ;
	int nb_preys_init <- 200 min: 1 max: 1000 parameter: 'Initial number of preys: ' category: 'Prey' ;
	int nb_predator_init <- 20 min: 0 max: 200 parameter: 'Initial number of predators ' category: 'Predator' ;
	float prey_max_energy <- 1.0 parameter: 'Prey max energy: ' category: 'Prey' ;
	float prey_max_transfert <- 0.1 parameter: 'Prey max transfert: ' category: 'Prey' ;
	float prey_energy_consum <- 0.05 parameter: 'Prey energy consumption: ' category: 'Prey' ;
	float prey_proba_reproduce <- 0.01 parameter: 'Prey probability reproduce: ' category: 'Prey' ;
	int prey_nb_max_offsprings <- 5 parameter: 'Prey nb max offsprings: ' category: 'Prey' ;
	float prey_energy_reproduce <- 0.5 parameter: 'Prey energy reproduce: ' category: 'Prey' ;
	float prey_speed <- 10.0 parameter: 'Prey speed: ' category: 'Prey' ;
	float predator_max_energy <- 1.0 parameter: 'Predator max energy: ' category: 'Predator' ;
	float predator_energy_transfert <- 0.5 parameter: 'Predator energy transfert: ' category: 'Predator' ;
	float predator_energy_consum <- 0.02 parameter: 'Predator energy consumption: ' category: 'Predator' ;
	float predator_proba_reproduce <- 0.01 parameter: 'Predator probability reproduce: ' category: 'Predator' ;
	int predator_nb_max_offsprings <- 3 parameter: 'Predator nb max offsprings: ' category: 'Predator' ;
	float predator_energy_reproduce <- 0.5 parameter: 'Predator energy reproduce: ' category: 'Predator' ;
	float predator_range <- 10.0 parameter: 'Predator range: ' category: 'Predator' ;
	float predator_speed <- 10.0 parameter: 'Predator speed: ' category: 'Predator' ;
	
	int nb_preys function: {length (prey as list)};
	int nb_predators function: {length (predator as list)};
	
	init {
		create vegetation from: map_init with: [food::read ('FOOD'), foodProd::read ('FOOD_PROD')] ;
		create prey number: nb_preys_init {
			set myPatch <- one_of(vegetation as list);
			set location <- any_location_in(myPatch);
		}
		create predator number: nb_predator_init { 
			set myPatch <- one_of(vegetation as list);
			set location <- any_location_in(myPatch);
		}
	}
	
	reflex stop_simulation when: (nb_preys = 0) or (nb_predators = 0) {
		do halt ;
	} 
}
entities {
	species vegetation {
		float max_food <- 100.0 ;
		float foodProd <- 10.0 ;
		float food update: min([max_food, food + foodProd] );
		rgb color update: rgb([255 * ((max_food - food) / max_food), 255, 255 * ((max_food - food) / max_food)]) ;
		aspect base {
			draw shape: geometry color: color ;
		}
	}
	species generic_species skills: [moving]{
		const size type: float <- 4.0 ;
		const color type: rgb <- rgb('blue') ;
		const max_energy type: float init: prey_max_energy ;
		const max_transfert type: float init: prey_max_transfert ;
		const energy_consum type: float init: prey_energy_consum ;
		const proba_reproduce type: float ;
		const nb_max_offsprings type: int ;
		const energy_reproduce type: float ;
		const my_icon type: string;
		
		vegetation myPatch <- nil ;
		float speed;
		float energy <- (rnd(1000) / 1000) * max_energy  update: energy - energy_consum max: max_energy ;
		
		reflex basic_move {
			do wander bounds: myPatch speed: speed;
		}
		
		reflex die when: energy <= 0 {
			do die ;
		} 
		reflex reproduce when: (energy >= energy_reproduce) and (flip(proba_reproduce)) {
			let nb_offsprings type: int <- 1 + rnd(nb_max_offsprings -1);
			create species(self) number: nb_offsprings {
				set myPatch <- myself.myPatch ;
				set location <- myself.location + {0.5 - rnd(1000)/1000, 0.5 - rnd(1000)/1000} ;
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
		const speed type: float <- prey_speed;
		
		reflex eat when: myPatch.food > 0 {
			let energy_transfert type: float <- min ([max_transfert, myPatch.food]) ;
			set myPatch.food <- myPatch.food - energy_transfert ;
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
		const my_icon type: string <- '../includes/data/wolf.png' ;
		const speed type: float <- predator_speed;
		
		reflex eat when: !(empty ((self neighbours_at predator_range) of_species prey)) {
			ask one_of ((self neighbours_at predator_range) of_species prey) {
				do die ;
			}
			set energy <- energy + energy_transfert ;
		}
	}
}

environment bounds: map_init ;

output {
	display main_display {
		image name: '../includes/data/soil.jpg' ;
		species vegetation aspect: base ;
		species prey aspect: base ;
		species predator aspect: base ;
	}
	display Population_information refresh_every: 5 {
		chart name: 'Species evolution' type: series background: rgb('white') size: {1,0.4} position: {0, 0.05} {
			data number_of_preys value: nb_preys color: rgb('blue') ;
			data number_of_predator value: nb_predators color: rgb('red') ;
		}
		chart name: 'Prey Energy Distribution' type: histogram background: rgb('lightGray') size: {0.5,0.4} position: {0, 0.5} {
			data name:"]0;0.25]" value: (prey as list) count (each.energy <= 0.25) ;
			data name:"]0.25;0.5]" value: (prey as list) count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
			data name:"]0.5;0.75]" value: (prey as list) count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
			data name:"]0.75;1]" value: (prey as list) count (each.energy > 0.75) ;
		}
		chart name: 'Predator Energy Distribution' type: histogram background: rgb('lightGray') size: {0.5,0.4} position: {0.5, 0.5} {
			data name:"]0;0.25]" value: (predator as list) count (each.energy <= 0.25) ;
			data name:"]0.25;0.5]" value: (predator as list) count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
			data name:"]0.5;0.75]" value: (predator as list) count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
			data name:"]0.75;1]" value: (predator as list) count (each.energy > 0.75) ;
		}
	}
	file name: 'results' type: text data: 'cycle: '+ time
					            + '; nbPreys: ' + nb_preys
					            + '; minEnergyPreys: ' + ((prey as list) min_of each.energy)
							    + '; maxSizePreys: ' + ((prey as list) max_of each.energy) 
   							    + '; nbPredators: ' + nb_predators           
   							    + '; minEnergyPredators: ' + ((predator as list) min_of each.energy)          
   							    + '; maxSizePredators: ' + ((predator as list) max_of each.energy) ;
	monitor number_of_preys value: nb_preys refresh_every: 1 ;
	monitor number_of_predators value: nb_predators refresh_every: 1 ;
}


