/**
 * Purpose: Test the spatial_level attribute. 
 */
model testcase19

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A;
		create species: B;
	}
}

entities {
	species A skills: [situated, moving] {
		var shape type: geometry init: circle (2.0);
		
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
		var shape type: geometry init: (square (20.0)) at_location {50, 50};
		
		species C spatial_level: 0 parent: A {
			var shape type: geometry init: square (3.0);	
		}
		
		reflex {
			do action: write {
				arg name: message value: 'list (A) = ' + (string (list (A)));
			}
		}
		
		reflex when: (time = 5) {
			do action: write {
				arg name: message value: name + " captures " + (string (list (A)));
			}
			
			capture target: list (A) as: C;
		}
		
		reflex when: (time = 10) {
			do action: write {
				arg name: message value: name + " releases " + (string (list (C)));
			}

			release target: list (C);
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
			species A;
			
			species B {
				species C;
			}
		}
	}
}