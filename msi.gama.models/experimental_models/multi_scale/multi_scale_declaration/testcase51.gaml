/**
 * Purpose: Test that parents indirect micro/macro-species can't have indirect parent-sub species relationship.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that G and I can't have B and D as parents.
 */
model testcase44

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
			
			species D parent: C {
				
			}
			
			species G parent: B {
				species H {
					species I parent: D {
						
					}
				}
			}
			
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}


