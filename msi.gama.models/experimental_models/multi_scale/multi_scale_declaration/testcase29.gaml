/**
 * Purpose: Test the inheritance of micro-species.
 */
model testcase29

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
	}
}

entities {
	species A skills: situated {
		species B skills: situated {
			species C skills: situated {
				
			}
			
			species D skills: situated {
				
			}
		}
	}
	
	species E skills: situated {
		species F skills: situated parent: A {
			species G skills: situated {
				
			}
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
	}
}