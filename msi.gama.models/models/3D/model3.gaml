model single_species

global {
	float energy_transform <- 0.8 const: true;
	float energy_birth <- 40;
	float birth_probabality_A <- 0.8;
	float birth_probabality_B <- 0.6;
	float max_food <- 10;
	
	float evolution_probability <- 0.7;
	
	int number_of_A value: length(list(A));
	int number_of_B value: length(list(B));
	
	init {
		create A number: 50;
		create B number: 50;
	}

	reflex save_result {
		save [number_of_A, number_of_B] type: "csv" to: "results.csv";
	}
}

entities {
	species A skills: [moving] {
		int energy <- rnd(100);
		int my_speed <- ( 1 + ( rnd(2) ) ); // toc do
		grid_env my_place value: grid_env(location);
		geometry shape <- circle (1);
		
		reflex move_around {
			do wander with: [ speed :: my_speed ];
			set energy <- energy - 1;
		}
		
		reflex eat {
			let others type: int value: length(my_place.agents);
			
			if (others > 0) and (my_place.food > 0) {
				let comsuming_energy type: float value: (my_place.food * energy_transform) / others; 
				set energy <- energy + comsuming_energy;
				set my_place.food <- my_place.food - comsuming_energy;
			}
		}
		
		reflex reproduce {
			if ( rnd (1) < birth_probabality_A ) and (energy > energy_birth) {
				create A {
					set energy <- (myself.energy / 2);
					set location <- myself.location;
				}
				
				set energy <- energy /2;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb('red');
		}
	}
	
	species B skills: [moving] {
		int energy <- rnd(100);
		int my_speed <- ( 1 + ( rnd(2) ) ); // toc do
		grid_env my_place value: grid_env(location);
		geometry shape <- square(1);
		
		reflex move_around {
			do wander with: [ speed :: my_speed ];
			set energy <- energy - 1;
		}
		
		reflex eat {
			let others type: int value: length(my_place.agents);
			
			if (others > 0) and (my_place.food > 0) {
				let comsuming_energy type: float value: (my_place.food * energy_transform) / others; 
				set energy <- energy + comsuming_energy;
				set my_place.food <- my_place.food - comsuming_energy;
			}
		}
		
		reflex reproduce {
			if ( rnd (1) < birth_probabality_B ) and (energy > energy_birth) {
				create B {
					set energy <- (myself.energy / 2);
					set location <- myself.location;
				}
				
				set energy <- energy /2;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb('blue');
		}
	}
}

environment width: 100 height: 100 {
	grid grid_env width: 100 height: 100 {
		int food <- rnd(max_food);
		rgb color <- ( food > 0 ) ? rgb ('green') : rgb ('white');
		
		reflex evolution {
			if (rnd (1) < evolution_probability) {
				set food <- food + rnd (6);
			} else {
				set food <- (food > 2) ? (food - 2) : 0;
			}
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			grid grid_env;
			species A;
			species B;
		}
		
		monitor A_agents value: length(list(A));
		
		display population_graph {
			chart "Species evolution" type: series background: rgb('white') {
				data number_of_A value: number_of_A color: rgb('blue') ;
				data number_of_B value: number_of_B color: rgb('red') ;
			}
		}
	}
}
