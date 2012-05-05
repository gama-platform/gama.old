/**
 * A three scales model with:
 * Level 1 species: ball, group, cloud
 * Level 2 species: ball_delegation, group_delegation
 * 
 * Level 3 species: ball_delegation in group_delegation
 * 
 * Load and step the simulation.
 * 
 * Verification
 * 	1. 
 */
model testcase2

global {
	init {
		create species: group;
	}
}

environment width: 100 height: 100;

entities {
	species ball control: fsm  {
//	species ball { // control: fsm {
		state initial_state initial: true;
		
	}
	
	species group {
		init {
			create species: ball_delegation;	
		}
		
		species ball_delegation parent: ball {
			int dummy_var <- 1;
		}
	}
	
	species cloud {
		
		reflex when: (time = 2) {
			
			// TODO why the description of "capture" command belong to "group"?
			
			capture target: list(group) as: group_delegation {
				// TODO: remove "target" facet name
				// TODO use "transform" command instead
				capture target: list(members) as: ball_delegation_sub_spec;
			}
		}
		
//		species group_delegation { // parent: group {
		// TODO the possibility to re-define micro-species of parent species in a sub-species??? 
		// NO because it will raise problem of compatibility when "capture" and "release".
		species group_delegation parent: group {
			// TODO copy micro-species of parent before adding my micro-species!!!
			// TODO add a species to make micro-agents migrate from one species to other micro-species			
			species ball_delegation_sub_spec parent: ball_delegation {
				do write {
					// TODO test that the value of dummy_var is correct?
					// HINT complete vs. uncomplete species 
					arg message value: dummy_var;
				}
			}
		}
	}
}

experiment default_expr type: gui {
	
}