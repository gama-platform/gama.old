model pedestrian_corridor_Hybrid_vs_ABM

global {
	int environment_width <- 200 const: true;
	int environment_height <- 200 const: true;
	
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
			do init_corridor {
				arg name: corridor_shape value: corridor_shape_0;
				arg name: is_hybrid value: false;
			}
		}

		ask (new_corridors at 1) {
			do init_corridor {
				arg name: corridor_shape value: corridor_shape_1;
				arg name: is_hybrid value: true;
			}
		}
	}

	reflex generate_pedestrians when: ( (time mod new_pedestian_generate_frequency) = 0 ) { // and (time < 4){
		create pedestrian number: 2 returns: new_pedestrians {
			if ( (time mod (red_pedestrian_frequency * new_pedestian_generate_frequency) ) = 0 ) {
				set color value: pedestrian_red;

			}
			else {
				set color value: pedestrian_green;
			}				
		}
		
		ask (new_pedestrians at 0) {
			do init_location {
				arg name: loc value: pedestrian_source_0;
			}
		}
		
		ask (new_pedestrians at 1) {
			do init_location {
				arg name: loc value: pedestrian_source_1;
			}
		}
	}	
}

entities {
	species pedestrian skills: [moving] {
//		var shape type: geometry init: copy (pedestrian_shape);
		geometry shape init: circle(pedestrian_size);
		rgb color;
		corridor last_corridor;

//		int heading;
//		var speed type: float init: pedestrian_speed;
		
		point target_location;
		float outgoing_density;
		
		action init_location {
			arg loc type: point;
			
			set location value: loc;
			set target_location value: {environment_width, location.y};
			set heading value: (self) towards (target_location);
		}
		
		reflex move_left {
			do action: move {
				arg name: heading value: (self) towards (target_location);
			} 
			
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
		
		action init_corridor {
			arg corridor_shape type: geometry;
			arg is_hybrid type: bool;
			
			set shape value: corridor_shape;
			set capture_pedestrians value: is_hybrid;
		}

		float max_speed value: pedestrian_speed; // Vmax (formula 5) MAKE IT BE PARAMETER 
		float macro_length min: 0.0 <- float(corridor_width); // the length of macro_patch
		
		float incoming_density; // Pr (formula 5)
		float incoming_average_speed;
		
		float Pr value: incoming_density;
		float Pl <- 0.0 const: true; // formula 5
		float Pmax <- 1.0; // the maximum number of micro-agents can enter macro-agent at the same time
		
		species captured_pedestrian parent: pedestrian schedules: [] {
			int released_time;  
			
			aspect default { }
		}
		
		init { 
			create corridor_info_drawer number: 1 with: [target :: self];
		}
		
		
		reflex aggregate when: capture_pedestrians {
//			let tobe_captured_pedestrians type: list value: (pedestrian overlapping shape) where (each.last_corridor != self);
			let tobe_captured_pedestrians type: list value: (pedestrian overlapping shape) where ( (each.last_corridor != self) and ((each.location).x < (self.location).x) ) ; // BUG
			
			if !(empty (tobe_captured_pedestrians)) {
				capture tobe_captured_pedestrians as: captured_pedestrian returns: cps { 
					set last_corridor value: myself;
				}
				
				if !(empty (cps)) {
					
					let average_speed type: float value: 0;
					loop micro_a over: cps {
						set average_speed value: average_speed + (micro_a.speed);
					}
					
					set incoming_average_speed value: average_speed / (length (cps));
					set incoming_density value: (length (cps)) / average_speed;
					
					set Pr value: incoming_density;
					
					let group_outgoing_time type: float value: time + (corridor_width / (incoming_average_speed) ); 
					
					let bound1 type: float value:  ( (max_speed * ( (1 - (2 * Pl) / Pmax ) ) ) * group_outgoing_time ); //
					let bound2 type: float value: ( (max_speed * ( (1 - (2 * Pr) / Pmax ) ) ) * group_outgoing_time ); //
					
					let pedestrian_outgoing_density type: float value: 0;
					if condition: (macro_length <= bound1 ) {
						set pedestrian_outgoing_density value: Pl;
						
					}
					else {
						
						if condition: ( (macro_length > bound1) and (macro_length < bound2) ) {
							set pedestrian_outgoing_density value: (Pmax * max_speed) / ( (2 * max_speed) + Pmax );
							
						}
						else {
							if condition: (macro_length > bound2) {
								set pedestrian_outgoing_density value: Pr;
							}	
						} 
					}
					
					let outgoing_speed type: float value: max_speed * ( 1 - (pedestrian_outgoing_density / Pmax));
					let released_number type: float value: pedestrian_outgoing_density * outgoing_speed;
					
					
					let pedestrian_k type: int value: 0;					
					
					loop cp over: cps {
						
						if condition: ( (pedestrian_k = 0) or (released_number = 0)) {
							set cp.released_time value: group_outgoing_time;
						}
						else {
//								set cp.released_time value: group_outgoing_time + ( pedestrian_k gamma released_number ); // gamma(k, lamda) : lamda == released_number
							set cp.released_time value: group_outgoing_time + ( TGauss([pedestrian_k, released_number]) ); // gamma(k, lamda) : lamda == released_number
						}
						
						set cp.outgoing_density value: pedestrian_outgoing_density;
						set pedestrian_k value: pedestrian_k + 1;
					}
 				}
			}
		}
		
		reflex disaggregate  {
			let tobe_released_pedestrians type: list value: (list (members)) where (time >= (captured_pedestrian (each)).released_time);
			
			if !(empty (tobe_released_pedestrians)) {
				
				release tobe_released_pedestrians as: pedestrian in: world returns: released_pedestrians;
				
				loop rp over: released_pedestrians {
					let outgoing_speed type: float value: max_speed * ( 1 - ((pedestrian (rp)).outgoing_density / Pmax));
					let sigma value: outgoing_speed / 10;
					
					set (pedestrian (rp)).speed value: outgoing_speed + ( gauss({0, sigma}));
					set (pedestrian (rp)).location value: {((environment_width / 2) + (corridor_width / 2)), ((corridor_shape_1).location).y};
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

//		aspect default {
//			if condition: target.capture_pedestrians {
//				draw text: 'Captured pedestrians: ' + (string (length (target.members))) color: rgb ('blue') size: 7 at: {(target.location).x - 48, (target.location).y};
//				draw text: 'Green: ' + (length ( (target.members) where ((pedestrian (each)).color = (pedestrian_green)))) color: rgb ('green') size: 7 at: {(target.location).x - 40, (target.location).y + 10};
//				draw text: 'Red: ' + (length ( (target.members) where ((pedestrian (each)).color = (pedestrian_red)))) color: rgb ('red') size: 7 at: {(target.location).x - 40, (target.location).y + 20};
//			}
//			else {
//				let intersecting_pedestrians type: list of: pedestrian value: (list (pedestrian)) overlapping target;
//				draw text: 'Intersecting pedestrians: ' + (length (intersecting_pedestrians)) color: rgb ('blue') size: 7 at: {(target.location).x - 48, (target.location).y + 10};
//				draw text: 'Green: ' + (length ( intersecting_pedestrians where (each.color = pedestrian_green)) ) color: rgb ('green') size: 7 at: {(target.location).x - 40, (target.location).y + 20};
//				draw text: 'Red: ' + (length ( intersecting_pedestrians where (each.color = pedestrian_red)) ) color: rgb ('red') size: 7 at: {(target.location).x - 40, (target.location).y + 30};
//			}
//		}
	}
}

environment width: environment_width height: environment_height;

experiment default_experiment type: gui {
	output {
		display default_display {
			species pedestrian;
			species corridor transparency: 0.8;
			species corridor_info_drawer aspect: base;
		}
	}
}