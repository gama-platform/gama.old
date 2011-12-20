/**
 * Purpose: Test that two direct micro/macro-species can't share a parent-species having micro-species.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that E and D can't have B and C as parents
 * 			because they will both inherit F micro-species which will make the reference to this species ambiguous 
 * 			from within the context of E species.
 */
model testcase50

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	species A skills: situated {
			
			species B {
				species F {
					
				}
			}
			
			species C parent: B {
				
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


