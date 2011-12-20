/**
 * Purpose: Test the parents of two direct micro/macro-species can ???
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome: 
 * 		1. The model can be loaded successfully.
 */
model testcase45

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated {
			
			species B {
			}
			
			species C parent: B {
				species F {}
			}
			
			species D parent: B {
				species E parent: C {
					
				}
			}
			
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}