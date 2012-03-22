model canibal_spider


global {
	init {
		create spider number: 2;
	}
}

environment width: 100 height: 100;
 
entities {
	species spider skills: moving { 
		geometry shape <- square (20) at_location ({rnd(100), rnd(100)});
		
		init {
//			create spider_in_spider; this command will create a StackOverflowException because the "init" is inherit from parent species
		}
		
		reflex {
//			create spider_in_spider; //this command will create a StackOverflowException because the "init" is inherit from parent species
		}
		
		reflex move_around {
			do wander;
		}
		
		reflex capture_another_spider {
			
			let another_spider type: spider value: one_of(list(spider) - self);
			do write {
				arg message value: string(self) + ' runs capture_another_spider with another_spider: ' + (string(another_spider));
			}
			
			if (another_spider != nil) {
				do write {
					arg message value: name + ' captures: ' + (string(another_spider));
				}

				capture another_spider as: spider_in_spider;
				
			}
		}
		
		aspect base {
			draw shape: geometry color: rgb('green');
		}

		species spider_in_spider parent: spider {
			geometry shape <- circle(3);
			
			// This species contains the "spider_in_spider" as micro-species
			// create spider_in_spider??? WHICH "spider_in_spider" to create in fact????
			// agent of micro-species of itself????
			
			/*
			species spider_in_spider parent: spider {
			}
			 * 
			 */
			
			/*
			init {
				create spider_in_spider;
			}
			*/
			
			// capture: detect that a spider_in_spider agent doesn't capture another spider_in_spider agent
			// situated higher in the hierarchy
			
			aspect base {
				draw shape: geometry color: rgb('blue');
			}
			
			
		}
		
	}
}

experiment capture_experiment type: gui {
	output {
		display default_display {
			species spider aspect: base transparency: 0.5 {
				species spider_in_spider aspect: base;
			}
		}
	}
}