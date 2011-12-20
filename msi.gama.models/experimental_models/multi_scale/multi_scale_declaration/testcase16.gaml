/**
 * Purpose: Test a multi-spatial scale model with four nested-species.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome:
 * 		1. Four agents of species A, B, C, D respectively are created and display on the screen.
 * 		2. Micro-agents are ensured to be in the bounds of macro-agent it their size are smaller then macro-agent's size.
 */
model testcase16

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
	}
	
	var scheduling type: list value: (list (A));
	
}

entities {
	species A skills: situated {
		var shape type: geometry init: (square (50.0)) at_location {50, 50};
		
		var scheduling type: list value: list (B);
		
		init {
			create species: B number: 1;
		}
		
		reflex {
			do action: write {
				arg name: message value: name + ' has members = ' + (string (members)); 
			}

			do action: write {
				arg name: message value: name + ' has agents = ' + (string (agents)); 
			}

			do action: write {
				arg name: message value: name + ' has host = ' + (string (host)); 
			}
		}
		
		species B skills: situated {
			var shape type: geometry init: square (20.0) at_location {60, 50};
			
			var scheduling type: list value: list (C);

			init {
				create species: C number: 1;
			}
			
			species C skills: situated {
				var shape type: geometry init: square (10.0) at_location {60, 50};
				
				var scheduling type: list value: list (D);
				
				init {
					create species: D number: 1;
				}
				
				species D skills: situated {
					var shape type: geometry init: circle (3.0);

					aspect default {
						draw shape: geometry color: rgb ('yellow');
					}
				}
				 
				aspect default {
					draw shape: geometry color: rgb ('blue');
				}
			}
			
			aspect default {
				draw shape: geometry color: rgb ('red');
			}
		}

		aspect default {
			draw shape: geometry color: rgb ('green');
		}
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		display default {
			species A transparency: 0.5 {
				species B transparency: 0.5 {
					species C transparency: 0.5 {
						species D transparency: 0.5;
					}
				}
			}
		}
	}
}