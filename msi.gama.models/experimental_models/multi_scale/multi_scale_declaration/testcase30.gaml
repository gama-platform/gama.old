/**
 * Purpose: Test the declaration of a "grid" inside an "environment".
 */
model testcase30

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	
}

environment width: 100 height: 100 {
	grid grid_example width: 100 height: 100 {
		
	}
}

experiment default_expr type: gui {
	
}
