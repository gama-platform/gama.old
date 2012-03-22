/**
 * Purpose: Test the species visibility.
 * 
 * An agent can "see" the following species:
 * 		1. its direct micro-species;
 * 		2. its species; peers of its species;
 * 		3. its direct&in-direct macro-species and their peers.
 * 
 * Action(s):
 * 		1. Load the model.
 * 		2. Step the simulation.
 * 
 * Expected outcome:
 * 		1. Five lines are printed in the "Console" view at simulation step indicating that:
 * 			a. "world_species0" agent can see agents of A species, of D species (direct micro-species).
 * 			b. "A0" agent can see agents of A species (its species), of B species (direct micro-species), of D species (peer of its species)
 * 			c. "C0" agent can see agents of A species (its in-direct macro-species), of B species (it direct macro-species), of D species (peer of ins in-direct macro-species).
 * 			d. "C0" agent can see agents of A species (peer of its species), of D species (its species).
 * 
 * TODO implements the MetaPopulation
 */
model testcase13

global {
	init {
		create A;
		create D;
	}
	
	reflex {
		do action: write {
			arg name: message value: 'agent: ' + name + ' with list (A): ' + (string (list (A))) + ', list (B): ' + (string (list (B))) + ', list (C):' + (string (list (C))) + ', list (D): ' + (string (list (D)));
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
						arg name: message value: 'agent: ' + name + ' with list (A): ' + (string (list (A))) + ', list (B): ' + (string (list (B))) + ', list (C):' + (string (list (C))) + ', list (D): ' + (string (list (D)));
					}
				}
			}

			reflex {
				do action: write {
					arg name: message value: 'agent: ' + name + ' with list (A): ' + (string (list (A))) + ', list (B): ' + (string (list (B))) + ', list (C):' + (string (list (C))) + ', list (D): ' + (string (list (D)));
				}
			}
		}
		
		reflex {
			do action: write {
				arg name: message value: 'agent: ' + name + ' with list (A): ' + (string (list (A))) + ', list (B): ' + (string (list (B))) + ', list (C):' + (string (list (C))) + ', list (D): ' + (string (list (D)));
			}
		}
	}
	
	species D {
		reflex {
			do action: write {
				arg name: message value: 'agent: ' + name + ' with list (A): ' + (string (list (A))) + ', list (B): ' + (string (list (B))) + ', list (C):' + (string (list (C))) + ', list (D): ' + (string (list (D)));
			}
		}
	}
}


environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}