model recursive_species

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: species1 number: 1;
	}
}

environment width: 100 height: 100;

entities {
	species species1 skills: situated {
		
		init {
			create species: species2 number: 1; // this command is added 3 times to the "init" reflex
		}
		
		species species2 skills: situated {
			/*
			init {
				create species: species3 number: 1;
			}
			
			species species3 skills: situated {
				
			}
			*/
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
}

experiment default_expr type: gui {
	output {
		display default_display {
			species species1 {
//				micro_layer species2;
			}
		}
	}
}