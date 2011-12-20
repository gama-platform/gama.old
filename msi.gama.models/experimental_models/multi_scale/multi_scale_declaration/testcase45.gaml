/**
 * Purpose: Test that parents of direct micro/macro-species can have direct parent-sub species relationship 
 * 				and micro-species if the inheritance of micro-species doesn't ambiguate the reference
 * 				of inherited micro-species with the context of micro-species. (understand the phrase??? :D )
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
				species F {}
			}
			
			species C parent: B {
			}
			
			species D parent: C {
				species E parent: B {
					
				}
			}
			
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}