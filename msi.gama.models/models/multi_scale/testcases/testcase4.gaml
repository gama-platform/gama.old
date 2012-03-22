/**
 * Purpose: Verify that an agent can create agents belonging to the following species:
 * 				1. its species;
 * 				2. peer species ot its species;
 * 				3. its direct&indirect macro-species and their peers;
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1. Verify the "Agents" menu: agents' hierarchy is correctly populated.
 */
model testcase4

global {
	init {
		create macro_species_A;
	}
}

entities {
	species macro_species_A {
		init {
			create meso_species_A;
			
			create macro_species_B;
		}
		
		species meso_species_A {
			init {
				create meso_species_B;
				
				create macro_species_B;
				
				create micro_species_A;
			}
			
			species micro_species_A {
				
				init {
					create macro_species_B;
				}
			}
		}
		
		species meso_species_B {
			
		}
	}
	
	species macro_species_B {
		
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}