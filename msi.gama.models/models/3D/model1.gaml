model single_species

global {
	float energy_transform <- 0.8 const: true;
	float energy_birth <- 40;
	float birth_probabality <- 0.8;
	float max_food <- 10;
	
	int number_of_A value: length(list(A));
	
	init {
		create A number: 100;
	}
}

entities {
	species A skills: [moving] {
		int energy <- rnd(100);
		int my_speed <- ( 1 + ( rnd(2) ) ); // toc do
		grid_env my_place value: grid_env(location);
		
		reflex move_around {
			do wander with: [ speed :: my_speed ];
			set energy <- energy - 1;
		}
		
		reflex eat {
			if (my_place.food) {
				set energy <- energy + (my_place.food * energy_transform);
			}
		}
		
		reflex reproduce {
			if ( rnd (1) < birth_probabality ) and (energy > energy_birth) {
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
}

environment width: 100 height: 100 {
	grid grid_env width: 100 height: 100 {
		int food <- rnd(max_food);
		rgb color <- ( food > 0 ) ? rgb ('green') : rgb ('white');
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			grid grid_env;
			species A;
		}
		
		monitor A_agents value: length(list(A));
		
		display population_graph {
			chart "Species evolution" type: series background: rgb('white') {
				data number_of_A value: number_of_A color: rgb('blue') ;
			}
		}
	}
}
