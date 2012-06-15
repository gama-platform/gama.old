model preys_predators_shelters

global { 
	var prey_color type: rgb init: rgb ('green') const: true;
	var prey_perception type: float init: 3;
	var prey_size type: float init: 2.0 const: true;
	var prey_speed type: float init: 1;
	var prey_flee_color type: rgb init: rgb ('orange');
	var prey_invisible_speed type: float init: 3 * 	prey_speed;
	var prey_invisible_color type: rgb init: rgb ('black');  
	var prey_in_shelter_max_time type: int min: 1 init: 200;
	var prey_invisible_max_time type: int min: 1 max: 100 init: 70; 
	var number_of_prey type: int min: 1 max: 1000 init: 100;
	
	var predator_color type: rgb init: rgb ('red') const: true;
	var predator_perception type: float init: 3;
	var predator_size type: float init: 4.0;
	var predator_speed type: float init: 1;
	var number_of_predator type: int min: 1 max: 100 init: 30; 
	
	var predator_in_shelter_color type: rgb init: rgb ('yellow') const: true;
	 
	var shelter_color type: rgb init: rgb ('blue') const: true; 
	var shelter_speed type: float init: 1.5 const: true;
	var shelter_shape type: geometry init: square (50.0);
	var number_of_shelter type: int init: 2 const: true;
	
	
	init {
		create prey number: number_of_prey;
		create predator number: number_of_predator; 
		create shelter number: number_of_shelter returns: shelters;
		set (shelters at 0).shape value: shelter_shape at_location {150, 250};
		set (shelters at 1).shape value: shelter_shape at_location {350, 250};
	}
}

entities {  
	species prey skills: [moving] control: fsm {
		var shape type: geometry init: circle (prey_size);
		var color type: rgb init: prey_color;
		var nearby_predators type: list of: predator value: (agents_overlapping (shape + prey_perception)) of_species predator;
		var invisible_time type: int min: 1 init: time;

		var nearest_shelter type: shelter init: nil;		

		state move_around initial: true {
			enter {
				set speed value: prey_speed;
				set color value: prey_color;
			}
			do wander; 
			transition to: flee_predator when: !(empty (nearby_predators)); 
		}
		
		
		state flee_predator {
			enter {
				set color value: prey_flee_color;
				set nearest_shelter value: first ( (list (shelter)) sort_by ( each distance_to (self)) );
			}
			if !(empty (nearby_predators)) { do move heading: (self) towards (nearest_shelter) speed: prey_speed;}
			transition to: move_around when: (empty (nearby_predators));
		}
		
		state invisible {
			enter {
				set speed value: prey_invisible_speed;
				set color value: prey_invisible_color;
				set invisible_time value: time;
				set heading value: rnd (359) ;
			}
			do move;
			transition to: move_around when: ( (time - invisible_time) > prey_invisible_max_time );
		}
		
		aspect default {
			draw shape: geometry color: color;
		}
	}
	
	species predator skills: [moving] schedules: shuffle (list (predator)) {
		var shape type: geometry init: circle (predator_size);
		var target_prey type: prey value: self choose_target_prey [];
		
		action choose_target_prey type: prey {
			if ( (target_prey = nil) or (dead (target_prey) ) ) {
				return one_of ( (list (prey)) where (each.state = 'move_around') );
			}
			return target_prey;
		}
		reflex move_around when: (target_prey = nil) { do wander speed: predator_speed; }
		reflex chase_prey when: (target_prey != nil) { do move heading: self towards target_prey speed: predator_speed;}
		
		aspect default {
			draw shape: geometry color: predator_color;
		} 
	}
	
	species shelter skills: [situated, moving]  frequency: 2 {
		var shape type: geometry init: (circle (50.0)) at_location {250, 250};
		var chased_preys type: list of: prey value: (list (prey)) where ( (each.shape intersects shape) and (each.state = 'flee_predator') );
		
		reflex move_around {
			do wander speed: shelter_speed; 
		}
		 
		reflex capture_chased_preys when: !(empty (chased_preys)) { 
			capture chased_preys as: prey_in_shelter {
				set state value: 'in_shelter'; 
				set shape value: ( triangle (4.0) ) at_location location;
			}
		}
		
		reflex release_member_preys {
			let to_be_released type: list of: prey_in_shelter value: (list (prey_in_shelter)) where ( (time - each.in_shelter_time) > prey_in_shelter_max_time );
			 
			release to_be_released in: world as: prey { 
				set state value: 'invisible';
				set shape value:  at_location (circle (prey_size), self.location);   
			}
		} 
		
		
		species prey_in_shelter parent: prey frequency: 2 schedules: ( ( int ( (length (prey_in_shelter)) / 2 ) ) among (list (prey_in_shelter)) ) {
			var in_shelter_time type: int init: time;
			state in_shelter {
				do wander speed: shelter_speed;
			}
			aspect default {
				draw shape: geometry color: predator_in_shelter_color;
			}
		}
		aspect default {
			draw shape: geometry color: shelter_color;
			draw text: 'Members: ' + (string (length ((members)))) color: rgb ('white') size: 8 at: {(location).x - 20, (location).y};
		}
	}

}

environment width: 500 height: 500;

experiment default_experiment type: gui {
	output {
		display default_display {
			species prey;
			species predator transparency: 0.5;
			species shelter transparency: 0.5 {
				species prey_in_shelter;
			}
		}
	}
}