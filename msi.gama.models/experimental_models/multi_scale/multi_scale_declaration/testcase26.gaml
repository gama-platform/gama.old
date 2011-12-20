/**
 * Purpose: Test the inheritance of behaviours between a species (A) and another sub-species (C) declared as a micro-species of A's peer (B). 
 */
model testcase26

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1 returns: A_agents;
		set (A_agents at 0).location value: {15, 15};
		
		create species: C number: 1;	
	}
}

entities {
	species A skills: situated {
		var shape type: geometry init: square (20.0);
		
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
	
	species C skills: situated {
		var shape type: geometry init: square (30.0) at_location {70, 70}; 
		
		species D skills: situated parent: A {
		}
		
		reflex when: (time = 5) {
			capture target: (list (A)) at 0 as: D;
		}
		
		aspect default {
			draw shape: geometry color: rgb ('red');
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		display default {
			species A transparency: 0.5 {
				species B transparency: 0.5 {
					
				}
			}
			
			species C transparency: 0.5 {
				species D transparency: 0.5 {
					species B transparency: 0.5 {
						
					}
				}
			}
		}
	}
}
