/**
 * Purpose: Test that the nested "create" commands run correctly.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1. Verify the "Agents" menu: agents' hierarchy is correctly populated.
 */
model testcase3

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: macro_species number: 1 {
			create species: meso_species number: 1 {
				create species: micro_species number: 1;
			}
		}
	}
}

entities {
	species macro_species {
		species meso_species {
			species micro_species {
				
			}
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}