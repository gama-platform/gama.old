/**
 * A three scales model with:
 * 	Level 1 species: ball, group, cloud
 * 	Level 2 species: ball_delegation, group_delegation
 * 	Level 3 species: ball_delegation, copied from "group", in group_delegation
 * 
 * Load and step the simulation.
 * 
 * 1. Create one "group" agent with a "ball_delegation" agent as micro-agent.
 * 2. Create one "cloud" agent.
 * 
 * Verification:
 * 	1. Verify that "ball_delegation" is copied correctly from "group" species to "group_delegation" species.
 * 	2. Verify that the "group" agent and its micro-agent is captured correctly by the "cloud" agent at step 3 of the simulation.
 * 	3. Verify that the "cloud" agent releases correctly its members at step 5 of the simulation.  
 */
model testcase1

global {
	init {
		create species: group;
		create species: cloud;
	}
	
	
	reflex a when: (time = 3) {
		ask target: (list(cloud)) at 0 {
			capture target: list(group) as: group_delegation;
		}
	}
	 
	// TODO test release command also
	
	reflex b when: (time = 5) {
		ask target: (list(cloud)) {
			release target: members;
		}
	}
}

environment width: 100 height: 100;

entities {
	species ball control: fsm {
		state initial_state initial: true;
		
		reflex print_state {
			do write { 
				arg message value: 'At time: ' + (string(time)) + ': ' + name + ' with current state: ' + state;
			}
		}
	}
	
	species group {
		init {
			create species: ball_delegation returns: bds;
		}
		
		reflex name: print_info {
			do write {
				arg message value: 'At time: ' + (string(time)) + ': ' + name + ' with direct microAgents: ' + (string(members)) + ': macro-agent: ' + (string(host));
			}	
		}
		
		species ball_delegation parent: ball {
			do write {
				arg message value: 'At time: ' + (string(time)) + ': ' + name + ' with macro-agent: ' + (string(host));
			}
		}
	}
	
	species cloud {
		
		reflex name: print_info {
			do write {
				arg message value: 'At time: ' + (string(time)) + ': ' + name + ' with direct microAgents: ' + (string(members)) + ': macro-agent: ' + (string(host));
			}
		}
		
		species group_delegation parent: group {
			
		}
	}
}

experiment default_expr type: gui {
	
}