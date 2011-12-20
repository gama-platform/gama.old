/**
 * Purpose: Test that parents indirect micro/macro-species can't share a indirect parent-species having micro-species.
 * 
 * Action(s):
 * 		1. Right click the mouse in the editor to parse and compile the model.
 * 
 * Expected outcome: 
 * 		1. Click on the "No experiment available (select to see errors)" menu.
 * 			The error message indicates that G and I can't have B and D as parents
 * 			because they will both inherit the F micro-species which will make
 * 			the reference to F from with I context ambiguous.
 */
model testcase45

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
			
			species G parent: D {
				species H {
					species I parent: B {
						
					}
				}
			}
			
	}

}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	
}


