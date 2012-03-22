/**
 * Purpose: Verify that we can not add two peer species having the same name.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1.An error is displayed in the editor indicating the duplicating species problem.
 */
model testcase7

global {
	
}

entities {
	species A {
		
	}

	species A {
		
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}