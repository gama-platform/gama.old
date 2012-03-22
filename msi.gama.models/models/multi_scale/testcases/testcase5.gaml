/**
 * Purpose: Verify that an agent (executor) can not create another agent whose species is not a direct micro-species of the executor.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1. A GamaRuntimeException in the "Errors" view indicating that the "C" species is not visible from the A species.
 *  
 */
model testcase5

global {
	init {
		create A;
	}
}

entities {
	species A {
		
		init {
			create C;
		}
		
		species B {
			species C {
				
			}
		}
		
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}
