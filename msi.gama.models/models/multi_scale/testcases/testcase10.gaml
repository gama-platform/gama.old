/**
 * Purpose: Verify that a species can not have the same name with micro-species of other species.
 * 
 * Action(s):
 * 		1. Open the model in the editor.
 * 
 * Expected outcome:
 * 		1.An error is displayed in the editor indicating the duplicating species problem.
 * 
 */
model testcase10

global {
	
}

entities {
	species A {
		species B {
		}
	}
	
	species B {
		
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}