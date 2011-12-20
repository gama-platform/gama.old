/**
 * Purpose: Test the "grid" topology.
 */
model testcase22

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: grid_container number: 1;
	}
}

entities {
	species grid_container skills: situated {
		var shape type: geometry init: (square (50.0)) at_location {50, 50};
		
		grid grid_sample width: 100 height: 100 {
			var color type: rgb init: ( (rnd (5)) > 3) ? (rgb ('green')) : (rgb ('blue'));
		}
	}
}

environment width: 100 height: 100 {
}

experiment default_expr type: gui {
	output {
		display default {
			species grid_container transparency: 0.5 {
				grid grid_sample;
			}
		}
	}
}