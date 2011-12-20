/**
 * Purpose: Test the content of "agents" and "scheduling" lists.
 * 
 * Action(s):
 * 		1. Load the model.
 * 		2. Step the simulation.
 * 
 * Expected outcome:
 * 		1. The "agents" list contains four agent [A0, D0, B0, C0]
 * 		2. The "scheduling" list contains 2 agents [A0, D0] which are the direct micro-agents of "world_species0" agent.
 * 		3. Only three agents are stepped each time: world_species0, A0, D0.
 */
model testcase14

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: A number: 1;
		create species: D number: 1;
	}
	
	var scheduling type: list value: agents;
	
	reflex {
		do action: write {
			arg name: message value: 'agent: ' + name + ' with agents: ' + (string (agents)) + ' and scheduling: ' + (string (scheduling));
		}
	}
}

entities {
	species A {
		init {
			create species: B number: 1;
		}
		
		species B {
			init {
				create species: C number: 1;
			}
			
			species C {
				reflex {
					do action: write {
						arg name: message value: 'agent: ' + name + ' steps';
					}
				}		
			}

			reflex {
				do action: write {
					arg name: message value: 'agent: ' + name + ' steps';
				}
			}		
		}

		reflex {
			do action: write {
				arg name: message value: 'agent: ' + name + ' steps';
			}
		}		
	}
	
	species D {
		reflex {
			do action: write {
				arg name: message value: 'agent: ' + name + ' steps';
			}
		}		
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}