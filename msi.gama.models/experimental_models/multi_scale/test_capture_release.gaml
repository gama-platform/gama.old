model pedestrian_flow

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: micro_spec number: 5;
	}
	
	reflex when: (time = 2) {
		create species: macro_spec number: 1 {
			capture target: list (micro_spec) delegation: toto;
		}
	}
}

entities {
	species micro_spec skills: situated {
		
		reflex when: (time = 7) {
			do action: die;
		}
		
		aspect default {
			draw shape: circle size: 2.0 color: rgb ('pink');
		}
	}
	
	species macro_spec skills: situated {
		var shape type: geometry value: polygon ((list (toto)) collect (each.location));
		
		delegation toto species: micro_spec {
			
			aspect default {
				draw shape: square size: 2.0 color: rgb ('magenta');
			}
		}
		
		reflex when: (time = 4) {
			let tobe_released value: 2 among (list (toto));
			release target: tobe_released;
			
			set (micro_spec (tobe_released at 0)).location value: {10, 10};
			set (micro_spec (tobe_released at 1)).location value: {20, 20};
		}
		
		aspect default {
			draw shape: geometry color: rgb ('blue');
		}		
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		display default_display {
			species micro_spec;
			species macro_spec transparency: 0.5 {
				micro_layer toto;
			}
		}
		
		monitor micro_agents value: length (list (micro_spec));
	}
}
