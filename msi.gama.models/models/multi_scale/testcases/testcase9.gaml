/**
 * Purpose: Verify that a species can not have the same name with in-direct micro-species.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1.An error is displayed in the editor indicating the duplicating species problem.
 */
model testcase9

global {
	
}

entities {
	species A {
		species B {
			species A {
				
			}
		} 
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}