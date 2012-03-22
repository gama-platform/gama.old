/**
 * Purpose: test that an agent can not create agents of species that don't belong to its direct micro-species.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		A GamaRuntimeException is thrown in the "Errors" view indicating that that "micro_species" is unknown in the context of World agent.
 */

model testcase2

global {
	init {
		create micro_species;
	}
}

entities {
	species macro_species {
		species micro_species {
			
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}