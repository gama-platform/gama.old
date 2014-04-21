/**
 * This model demonstrates the multi-level modeling feature.
 * 
 * It models the pedestrians walking over two corridors.
 * 
 * In the upper corridor, pedestrians are represented in one level of organization, i.e., "pedestrian" species.
 * In the lower corridor, pedestrians are presented in two levels of orgnizations :
 * 		+ "pedestrian" species when they are not in the corridor;
 * 		+ "capture_pedestrian" specues when they are in the corridor.
 */
model pedestrian_corridor_Hybrid_vs_ABM

global {
	int environment_width <- 200 const: true;
	int environment_height <- 200 const: true;
	geometry shape <- rectangle(environment_width, environment_height);	
	
	rgb pedestrian_green <- rgb ('green');
	rgb pedestrian_red <- rgb ('red');
	
	float pedestrian_size <- 1.0;
	geometry pedestrian_shape <- circle (pedestrian_size) ;
	float pedestrian_speed <- 2.0;
 
	rgb corridor_color <- rgb ('blue');
	int corridor_width <- 160 depends_on: [environment_width];
	int corridor_height <- (int(environment_height * 0.05));

	point corridor_location_0 <- {environment_width / 2, environment_height / 4};
	geometry corridor_shape_0 <- ( (rectangle ({corridor_width, corridor_height})) at_location corridor_location_0) ;

	point corridor_location_1 <- {environment_width / 2, environment_height * 0.75};
	geometry corridor_shape_1 <- ( (rectangle ({corridor_width, corridor_height})) at_location corridor_location_1) ;

	int new_pedestian_generate_frequency <- 8;
	point pedestrian_source_0 <- {0, corridor_location_0.y} ;
	point pedestrian_source_1 <- {0, corridor_location_1.y} ;
	
	int red_pedestrian_frequency <- 5;
	 
	init {
		create corridor number: 2 returns: new_corridors;
		
		ask (new_corridors at 0) {
			do init_corridor corridor_shape: corridor_shape_0 is_hybrid: false;
		}

		ask (new_corridors at 1) {
			do init_corridor corridor_shape: corridor_shape_1 is_hybrid: true; 
		}
	}

	reflex generate_pedestrians when: ( (cycle mod new_pedestian_generate_frequency) = 0 ) { 
		create pedestrian number: 2 returns: new_pedestrians {
			if ( (cycle mod (red_pedestrian_frequency * new_pedestian_generate_frequency) ) = 0 ) {
				color <- pedestrian_red;
			}
			else {
				color <- pedestrian_green;
			}				
		}
		
		ask (new_pedestrians at 0) {
			do init_location loc: pedestrian_source_0;
		}
		
		ask (new_pedestrians at 1) {
			do init_location loc: pedestrian_source_1;
		}
	}	
}

species pedestrian skills: [moving] {
	geometry shape <- circle(pedestrian_size);
	rgb color;
	corridor last_corridor;
	
	point target_location;
	float outgoing_density;
	
	action init_location (point loc) {
		location <- loc;
		target_location <- {environment_width, location.y};
		heading <- self towards (target_location);
	}
	
	reflex move_left {
		do action: move heading: (self) towards (target_location); 
		
		if ( (target_location.x - location.x) <= speed ) {
			do die;
		}
	}
	 
	aspect default {
		draw shape color: color;
	}
}

species corridor  {
	bool capture_pedestrians;
	
	action init_corridor (geometry corridor_shape, bool is_hybrid) {
		shape <- corridor_shape;
		capture_pedestrians <- is_hybrid;
	}

	float max_speed <- pedestrian_speed; // Vmax (formula 5) MAKE IT BE PARAMETER 
	float macro_length min: 0.0 <- float(corridor_width); // the length of macro_patch
	
	float incoming_density; // Pr (formula 5)
	float incoming_average_speed;
	
	float Pr <- incoming_density;
	float Pl <- 0.0 const: true; // formula 5
	float Pmax <- 1.0; // the maximum number of micro-agents can enter macro-agent at the same time
	
	species captured_pedestrian parent: pedestrian schedules: [] {
		float released_time;  
		
		aspect default { }
	}
	
	init { 
		create corridor_info_drawer number: 1 with: [target :: self];
	}
	
	
	reflex aggregate when: capture_pedestrians {
		list<pedestrian> tobe_captured_pedestrians <- (pedestrian overlapping shape) where ( (each.last_corridor != self) and ((each.location).x < (self.location).x) ) ; // BUG
		
		if !(empty (tobe_captured_pedestrians)) {
			capture tobe_captured_pedestrians as: captured_pedestrian returns: cps { 
				last_corridor <- myself;
			}
			
			if !(empty (cps)) {
				
				float average_speed <- 0.0;
				loop micro_a over: cps {
					average_speed <- average_speed + (micro_a.speed);
				}
				
				incoming_average_speed <- average_speed / (length (cps));
				incoming_density <- (length (cps)) / average_speed;
				
				Pr <- incoming_density;
				
				float group_outgoing_time <- time + (corridor_width / (incoming_average_speed) ); 
				
				float bound1 <- ( (max_speed * ( (1 - (2 * Pl) / Pmax ) ) ) * group_outgoing_time ); //
				float bound2 <- ( (max_speed * ( (1 - (2 * Pr) / Pmax ) ) ) * group_outgoing_time ); //
				
				float pedestrian_outgoing_density <- 0.0;
				if (macro_length <= bound1 ) {
					pedestrian_outgoing_density <- Pl;
					
				}
				else {
					
					if ( (macro_length > bound1) and (macro_length < bound2) ) {
						pedestrian_outgoing_density <- (Pmax * max_speed) / ( (2 * max_speed) + Pmax );
						
					}
					else {
						if (macro_length > bound2) {
							pedestrian_outgoing_density <- Pr;
						}	
					} 
				}
				
				float outgoing_speed <- max_speed * ( 1 - (pedestrian_outgoing_density / Pmax));
				float released_number <- pedestrian_outgoing_density * outgoing_speed;
				
				int pedestrian_k <- 0;					
				
				loop cp over: cps {
					
					if ( (pedestrian_k = 0) or (released_number = 0)) {
						cp.released_time <- group_outgoing_time;
					}
					else {
						cp.released_time <- group_outgoing_time + ( TGauss([pedestrian_k, released_number]) ); // gamma(k, lamda) : lamda == released_number
						}
						
						cp.outgoing_density <- pedestrian_outgoing_density;
						pedestrian_k <- pedestrian_k + 1;
					}
 				}
			}
		}
		
	reflex disaggregate  {
		list tobe_released_pedestrians <- members where (time >= (captured_pedestrian (each)).released_time);
		
		if !(empty (tobe_released_pedestrians)) {
			
			release tobe_released_pedestrians as: pedestrian in: world returns: released_pedestrians;
			
			loop rp over: released_pedestrians {
				float outgoing_speed <- max_speed * ( 1 - (rp.outgoing_density / Pmax));
				float sigma <- outgoing_speed / 10;
				
				rp.speed <- outgoing_speed + ( gauss({0, sigma}));
				rp.location <- {((environment_width / 2) + (corridor_width / 2)), ((corridor_shape_1).location).y};
			}
		}
	}
		
	aspect default {
		draw shape color: corridor_color;
	}
}
 
species corridor_info_drawer {
	corridor target;
	
	aspect base {
		if target.capture_pedestrians {
			draw text: 'Hybrid model (coupling: ABM and Mathematical Model)' color: rgb('blue') size: 7 at: {(target.location).x - 90, (target.location).y - 10};
			draw text: 'Aggregated agents: ' + string(length(target.members)) color: rgb('black') size: 7 at: {(target.location).x - 30, (target.location).y + 2};
		} else {
			draw text: 'Agent-Based Model (ABM)' color: rgb('blue') size: 7 at: {(target.location).x - 40, (target.location).y - 10};
		}
	}
}

experiment default_experiment type: gui {
	output {
		display default_display {
			species pedestrian;
			species corridor transparency: 0.8;
			species corridor_info_drawer aspect: base;
		}
	}
}