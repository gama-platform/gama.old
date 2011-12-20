/**
 * Purpose: Test the "grid" declared inside "environment" section.
 */
model testcase21

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
}

entities {
	
}

environment width: 100 height: 100 {
	grid simple_grid width: 100 height: 100 neighbours: 8 {
		var color type: rgb init: ((grid_x mod 2) = 0) ? rgb ('green') : rgb ('blue');
	}
}

experiment default_expr type: gui {
	output {
		display default {
			grid simple_grid transparency: 0.5;
		}
	}
}