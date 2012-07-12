model pedestrian_corridor_Hybrid_vs_ABM

global {
	const environment_width type: int init: 200;
	const environment_height type: int init: 200;
	
	const pedestrian_green type: rgb init: rgb ('green');
	const pedestrian_red type: rgb init: rgb ('red');
	
	const pedestrian_size type: float init: 1.0;
	const pedestrian_shape type: geometry init: circle (pedestrian_size) depends_on: pedestrian_size;
	const pedestrian_speed type: float init: 2.0;

	const corridor_color type: rgb init: rgb ('blue');
	const corridor_width type: int init: int(environment_width / 2) depends_on: [environment_width];
	const corridor_height type: int init: (int(environment_height * 0.4)) depends_on: [environment_height];

	const corridor_location_0 type: point init: {environment_width / 2, environment_height / 4} depends_on: [environment_width, environment_height];
	const corridor_shape_0 type: geometry init: ( (rectangle ({corridor_width, corridor_height})) at_location corridor_location_0) depends_on: [corridor_width, corridor_height, corridor_location_0];

	const corridor_location_1 type: point init: {environment_width / 2, environment_height * 0.75} depends_on: [environment_width, environment_height];
	const corridor_shape_1 type: geometry init: ( (rectangle ({corridor_width, corridor_height})) at_location corridor_location_1) depends_on: [corridor_width, corridor_height, corridor_location_1];

	const new_pedestian_generate_frequency type: int init: 8;
	const pedestrian_source_0 type: point init: {0, corridor_location_0.y} depends_on: [corridor_location_0];
	const pedestrian_source_1 type: point init: {0, corridor_location_1.y} depends_on: [corridor_location_1];
	
	const red_pedestrian_frequency type: int init: 5;
	 
	init {
		create corridor number: 2 returns: new_corridors;
		
		ask target: (new_corridors at 0) {
			do action: init_corridor {
				arg name: corridor_shape value: corridor_shape_0;
				arg name: is_hybrid value: true;
			}
		}

		ask target: (new_corridors at 1) {
			do action: init_corridor {
				arg name: corridor_shape value: corridor_shape_1;
				arg name: is_hybrid value: false;
			}
		}
	}

	reflex generate_pedestrians when: ( (time mod new_pedestian_generate_frequency) = 0 ) { // and (time < 4){
		create species: pedestrian number: 2 returns: new_pedestrians {
			if condition: ( (time mod (red_pedestrian_frequency * new_pedestian_generate_frequency) ) = 0 ) {
				set color value: pedestrian_red;

			}
			else {
				set color value: pedestrian_green;
			}				
		}
		
		ask target:  (new_pedestrians at 0) {
			do action: init_location {
				arg name: loc value: pedestrian_source_0;
			}
		}
		
		ask target:  (new_pedestrians at 1) {
			do action: init_location {
				arg name: loc value: pedestrian_source_1;
			}
		}
	}	
}

entities {
	species pedestrian skills: moving {
//		var shape type: geometry init: copy (pedestrian_shape);
		var shape type: geometry init: circle(pedestrian_size);
		var color type: rgb;
		var last_corridor type: corridor;

		var heading type: int;
		var speed type: float init: pedestrian_speed;
		
		var target_location type: point;
		
		var outgoing_density type: float;
		
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
			
			if condition: ( (target_location.x - location.x) <= speed ) {
				do action: die;
			}
		}
		 
		aspect name: default {
			draw shape: geometry color: color;
		}
	}

	species corridor skills: situated {
		var capture_pedestrians type: bool;
		
		action init_corridor {
			arg corridor_shape type: shape;
			arg is_hybrid type: bool;
			
			set shape value: corridor_shape;
			set capture_pedestrians value: is_hybrid;
		}

		var max_speed type: float value: pedestrian_speed; // Vmax (formula 5) MAKE IT BE PARAMETER 
		var macro_length type: float min: 0 init: corridor_width; // the length of macro_patch
		
		var incoming_density type: float; // Pr (formula 5)
		var incoming_average_speed type: float;
		
		var Pr type: float value: incoming_density;
		var Pl type: float init: 0 const: true; // formula 5
		var Pmax type: float init: 1.0; // the maximum number of micro-agents can enter macro-agent at the same time
		
		species captured_pedestrian parent: pedestrian schedules: [] {
			var released_time type: int;  
			
			aspect name: default { }
		}
		
		init { 
			create corridor_info_drawer number: 1 with: [target :: self];
		}
		
		
		reflex aggregate when: capture_pedestrians {
			let tobe_captured_pedestrians type: list value: (pedestrian overlapping shape) where (each.last_corridor != self);
			
			if condition: !(empty (tobe_captured_pedestrians)) {
				capture target: tobe_captured_pedestrians as: captured_pedestrian returns: cps { 
					set last_corridor value: myself;
				}
				
				if condition: !(empty (cps)) {
					
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
			
			let one_member type: captured_pedestrian value: one_of(members);
			
			if condition: !(empty (tobe_released_pedestrians)) {
				
				release tobe_released_pedestrians as: pedestrian in: world returns: released_pedestrians;
				
				loop rp over: released_pedestrians {
					let outgoing_speed type: float value: max_speed * ( 1 - ((pedestrian (rp)).outgoing_density / Pmax));
					let sigma value: outgoing_speed / 10;
					
					set (pedestrian (rp)).speed value: outgoing_speed + ( gauss({0, sigma}));
					set (pedestrian (rp)).location value: {((environment_width / 2) + (corridor_width / 2)), ((corridor_shape_0).location).y};
				}
			}
		}
		
		aspect name: default {
			draw shape: geometry color: corridor_color;
		}
	}
 
	species corridor_info_drawer {
		var target type: corridor;
		
		aspect default {
			if condition: target.capture_pedestrians {
				draw text: 'Captured pedestrians: ' + (string (length (target.members))) color: rgb ('blue') size: 7 at: {(target.location).x - 48, (target.location).y};
				draw text: 'Green: ' + (length ( (target.members) where ((pedestrian (each)).color = (pedestrian_green)))) color: rgb ('green') size: 7 at: {(target.location).x - 40, (target.location).y + 10};
				draw text: 'Red: ' + (length ( (target.members) where ((pedestrian (each)).color = (pedestrian_red)))) color: rgb ('red') size: 7 at: {(target.location).x - 40, (target.location).y + 20};
			}
			else {
				let intersecting_pedestrians type: list of: pedestrian value: (list (pedestrian)) overlapping target;
				draw text: 'Intersecting pedestrians: ' + (length (intersecting_pedestrians)) color: rgb ('blue') size: 7 at: {(target.location).x - 48, (target.location).y + 10};
				draw text: 'Green: ' + (length ( intersecting_pedestrians where (each.color = pedestrian_green)) ) color: rgb ('green') size: 7 at: {(target.location).x - 40, (target.location).y + 20};
				draw text: 'Red: ' + (length ( intersecting_pedestrians where (each.color = pedestrian_red)) ) color: rgb ('red') size: 7 at: {(target.location).x - 40, (target.location).y + 30};
			}
		}
	}
}

environment width: environment_width height: environment_height;

experiment default_expr type: gui {
	output {
		display default_display {
			species pedestrian;
			species corridor transparency: 0.8;
			species corridor_info_drawer;
		}
	}
}