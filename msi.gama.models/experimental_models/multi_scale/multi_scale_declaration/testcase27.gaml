/**
 * Purpose: Test the movement of a micro-agent inside a macro-agent.
 */
model testcase27

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
	}
}

entities {
	species A skills: situated {
		var shape type: geometry init: (square (50.0)) at_location {40, 40};
		
		init {
			create species: B number: 1;
		}
		
		species B skills: [situated, moving] {
			var shape type: geometry init: circle (2.0);
			
			reflex move_around {
				do action: wander {
					arg name: speed value: 1.0;
				}
			}
			
			aspect default {
				draw shape: geometry color: rgb ('green');
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('blue');
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		display default {
			species A transparency: 0.5 {
				species B;
			}
		}
	}
}

