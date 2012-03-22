/**
 * Purpose: Verify that an agent can not create agent(s) whose species is micro-species of its species' peer.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1. An error message in the "Errors" view indicating that the "C" species is not visible from the A species.
 */
model testcase6

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
	}
	
	species B {
		species C {
			
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}