model prey_predator
//Model 14 of the predator/prey tutorial
global {
	file map_init <- file("../includes/gis/vegetation.shp");
	int nb_preys_init <- 200 min: 1 max: 1000;
	int nb_predators_init <- 20 min: 0 max: 200;
	float prey_max_energy <- 1.0;
	float prey_max_transfert <- 0.1;
	float prey_energy_consum <- 0.05;
	float prey_proba_reproduce <- 0.01;
	int prey_nb_max_offsprings <- 5;
	float prey_energy_reproduce <- 0.5;
	float prey_speed <- 10.0;
	float predator_max_energy <- 1.0;
	float predator_energy_transfert <- 0.5;
	float predator_energy_consum <- 0.02;
	float predator_proba_reproduce <- 0.01;
	int predator_nb_max_offsprings <- 3;
	float predator_energy_reproduce <- 0.5;
	float predator_range <- 10.0;
	float predator_speed <- 10.0;
	int nb_preys -> { length(prey) };
	int nb_predators -> { length(predator) };
	geometry shape <- envelope(map_init);
	init {
		create vegetation from: map_init with: [food::float(read("FOOD")), foodProd::float(read("FOOD_PROD"))];
		create prey number: nb_preys_init {
			myPatch <- one_of(vegetation);
			location <- any_location_in(myPatch);
		}

		create predator number: nb_predators_init {
			myPatch <- one_of(vegetation);
			location <- any_location_in(myPatch);
		}

	}

	reflex stop_simulation when: (nb_preys = 0) or (nb_predators = 0) {
		do halt;
	}

	reflex save_result when: (nb_preys > 0) and (nb_predators > 0){
		save ("cycle: "+ time + "; nbPreys: " + nb_preys
			 + "; minEnergyPreys: " + ((prey as list) min_of each.energy)
			 + "; maxSizePreys: " + ((prey as list) max_of each.energy) 
	   		 + "; nbPredators: " + nb_predators           
	   		 + "; minEnergyPredators: " + ((predator as list) min_of each.energy)          
	   		 + "; maxSizePredators: " + ((predator as list) max_of each.energy)) 
	   		 to: "results.txt" type: "text" ;
	}
}

entities {
	species vegetation {
		float max_food <- 100.0;
		float foodProd <- 10.0;
		float food max:max_food update: food + foodProd;
		rgb color <- rgb(int(255 * (1 - food)), 255, int(255 * (1 - food))) update: rgb(int(255 * (1 - food)), 255, int(255 *(1 - food))) ;
		aspect base {
			draw shape color: color;
		}

	}

	species generic_species skills: [moving] {
		const size type: float <- 4.0;
		const color type: rgb <- rgb("blue");
		const max_energy type: float <- prey_max_energy;
		const max_transfert type: float <- prey_max_transfert;
		const energy_consum type: float <- prey_energy_consum;
		const proba_reproduce type: float;
		const nb_max_offsprings type: int;
		const energy_reproduce type: float;
		const my_icon type: file;
		vegetation myPatch <- nil;
		float speed;
		float energy <- (rnd(1000) / 1000) * max_energy update: energy - energy_consum max: max_energy;
		reflex basic_move {
			do wander bounds: myPatch speed: speed;
		}

		reflex die when: energy <= 0 {
			do die;
		}

		reflex reproduce when: (energy >= energy_reproduce) and (flip(proba_reproduce)) {
			int nb_offsprings <- 1 + rnd(nb_max_offsprings - 1);
			create species(self) number: nb_offsprings {
				myPatch <- myself.myPatch;
				location <- myself.location + { 0.5 - rnd(1000) / 1000, 0.5 - rnd(1000) / 1000 };
				energy <- myself.energy / nb_offsprings;
			}

			energy <- energy / nb_offsprings;
		}

		aspect base {
			draw circle(size) color: color;
		}

		aspect icon {
			draw my_icon size: size;
		}

		aspect info {
			draw square(size) color: color;
			draw string(energy with_precision 2) size: 3 color: rgb("black");
		}

	}

	species prey parent: generic_species {
		const color type: rgb <- rgb("blue");
		const max_energy type: float <- prey_max_energy;
		const max_transfert type: float <- prey_max_transfert;
		const energy_consum type: float <- prey_energy_consum;
		const proba_reproduce type: float <- prey_proba_reproduce;
		const nb_max_offsprings type: int <- prey_nb_max_offsprings;
		const energy_reproduce type: float <- prey_energy_reproduce;
		const my_icon type: file <- file("../includes/data/sheep.png");
		const speed type: float <- prey_speed;
		reflex eat when: myPatch.food > 0 {
			float energy_transfert <- min([max_transfert, myPatch.food]);
			myPatch.food <- myPatch.food - energy_transfert;
			energy <- energy + energy_transfert;
		}

	}

	species predator parent: generic_species {
		const color type: rgb <- rgb("red");
		const max_energy type: float <- predator_max_energy;
		const energy_transfert type: float <- predator_energy_transfert;
		const energy_consum type: float <- predator_energy_consum;
		const proba_reproduce type: float <- predator_proba_reproduce;
		const nb_max_offsprings type: int <- predator_nb_max_offsprings;
		const energy_reproduce type: float <- predator_energy_reproduce;
		const my_icon type: file <- file("../includes/data/wolf.png");
		const speed type: float <- predator_speed;
		list<prey> reachable_preys update: prey at_distance predator_range;
		reflex eat when: !(empty(reachable_preys)) {
			ask one_of(reachable_preys) {
				do die;
			}
			energy <- energy + energy_transfert;
		}

	}

}

experiment prey_predator type: gui {
	parameter "Initial number of preys: " var: nb_preys_init category: "Prey";
	parameter "Prey max energy: " var: prey_max_energy category: "Prey";
	parameter "Prey max transfert: " var: prey_max_transfert category: "Prey";
	parameter "Prey energy consumption: " var: prey_energy_consum category: "Prey";
	parameter "Initial number of predators: " var: nb_predators_init category: "Predator";
	parameter "Predator max energy: " var: predator_max_energy category: "Predator";
	parameter "Predator energy transfert: " var: predator_energy_transfert category: "Predator";
	parameter "Predator energy consumption: " var: predator_energy_consum category: "Predator";
	parameter "Prey probability reproduce: " var: prey_proba_reproduce category: "Prey";
	parameter "Prey nb max offsprings: " var: prey_nb_max_offsprings category: "Prey";
	parameter "Prey energy reproduce: " var: prey_energy_reproduce category: "Prey";
	parameter "Predator probability reproduce: " var: predator_proba_reproduce category: "Predator";
	parameter "Predator nb max offsprings: " var: predator_nb_max_offsprings category: "Predator";
	parameter "Predator energy reproduce: " var: predator_energy_reproduce category: "Predator";
	parameter "Initial environement: " var: map_init category: "Environment";
	parameter "Prey speed: " var: prey_speed category: "Prey";
	parameter "Predator range: " var: predator_range category: "Predator";
	parameter "Predator speed: " var: predator_speed category: "Predator";
	output {
		display main_display {
			image  "../includes/data/soil.jpg";
			species vegetation aspect: base;
			species prey aspect: base;
			species predator aspect: base;
		}

		display Population_information refresh_every: 5 {
			chart "Species evolution" type: series background: rgb("white") size: {1,0.4} position: {0, 0.05} {
				data "number_of_preys" value: nb_preys color: rgb("blue") ;
				data "number_of_predator" value: nb_predators color: rgb("red") ;
			}
			chart "Prey Energy Distribution" type: histogram background: rgb("lightGray") size: {0.5,0.4} position: {0, 0.5} {
				data "]0;0.25]" value: prey count (each.energy <= 0.25) ;
				data "]0.25;0.5]" value: prey count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
				data "]0.5;0.75]" value: prey count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
				data "]0.75;1]" value: prey count (each.energy > 0.75) ;
			}
			chart "Predator Energy Distribution" type: histogram background: rgb("lightGray") size: {0.5,0.4} position: {0.5, 0.5} {
				data "]0;0.25]" value: predator count (each.energy <= 0.25) ;
				data "]0.25;0.5]" value: predator count ((each.energy > 0.25) and (each.energy <= 0.5)) ;
				data "]0.5;0.75]" value: predator count ((each.energy > 0.5) and (each.energy <= 0.75)) ;
				data "]0.75;1]" value: predator count (each.energy > 0.75) ;
			}
		}
		monitor number_of_preys value: nb_preys refresh_every: 1 ;
		monitor number_of_predators value: nb_predators refresh_every: 1 ;
	}

}


