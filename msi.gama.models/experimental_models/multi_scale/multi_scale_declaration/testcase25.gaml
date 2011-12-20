/**
 * Purpose: Test the inheritance of behaviours between a species (A) and another sub-species (C) declared as a micro-species of A's peer (B). 
 */
model testcase25

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: B;
	}
}

entities {
	species A skills: [situated, moving] {
		var shape type: geometry init: circle (2.0) at_location {50, 50};
		
		reflex move {
			do action: wander {
				arg name: speed value: 1.0;
			}
		}
		
		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
	
	species B skills: situated {
		var shape type: geometry init: square (50.0) at_location {50, 50};
		
		init {
			create species: C;
		}
		
		species C skills: situated parent: A {
			var shape type: geometry init: triangle (2.0) at_location {70, 70};
			
			aspect default {
				draw shape: geometry color: rgb ('red');
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
			species B transparency: 0.5 {
				species C;
			}
		}
	}
}