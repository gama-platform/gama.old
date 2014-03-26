model preys_predators_shelters_with_sound

global { 
	rgb prey_color <- rgb ('green') const: true;
	float prey_perception <- 20.0;
	float prey_size <- 2.0 const: true;
	float prey_speed <- 1.0;
	rgb prey_flee_color <- rgb ('orange');
	float prey_invisible_speed <- 3 * prey_speed;
	rgb prey_invisible_color <- rgb ('black');  
	int prey_in_shelter_max_time min: 1 init: 200;
	int prey_invisible_max_time min: 1 max: 100 init: 70; 
	int number_of_preys min: 1 max: 1000 init: 4;
	
	rgb predator_color <- rgb ('red') const: true;
	float predator_perception <- 3.0;
	float predator_size <- 4.0;
	float predator_speed <- 1.0;
	int number_of_predators min: 1 max: 10 init: 1; 
	geometry shape <- square(400);
	
	rgb predator_in_shelter_color <- rgb ('yellow') const: true;
	 
	rgb shelter_color <- rgb ('blue') const: true; 
	float shelter_speed <- 1.5 const: true;
	geometry shelter_shape <- square (50.0);
	
	
	init {
		create prey number: number_of_preys;
		create predator number: number_of_predators; 
		create shelter returns: shelters;
		(shelters at 0).shape <- shelter_shape at_location {225, 250};
	}
}

entities {  
	species prey skills: [moving] control: fsm {
		geometry shape <- square (prey_size);
		rgb color <- prey_color;
		list nearby_predators update: (agents_overlapping (shape + prey_perception)) of_species predator depends_on: shape;
		int invisible_time min: 1 <- int(time);

		shelter nearest_shelter;		

		state move_around initial: true {
			enter {
				speed <- prey_speed;
				color <- prey_color;
			}
			do wander; 
			
			transition to: flee_predator when: !(empty (nearby_predators)); 
		}
		
		
		state flee_predator {
			enter {
				color <- prey_flee_color;
				nearest_shelter <- first ( (list (shelter)) sort_by ( each distance_to (self)) );
			}
			if !(empty (nearby_predators)) { do move heading: (self) towards (nearest_shelter) speed: prey_speed;}
			
			transition to: move_around when: (empty (nearby_predators));
		}
		
		state invisible {
			enter {
				speed <- prey_invisible_speed;
				color <- prey_invisible_color;
				invisible_time <- int(time);
				heading <- rnd (359) ;
			}
			do move; 
			transition to: move_around when: ( (time - invisible_time) > prey_invisible_max_time );
		}
		
		aspect default {
			draw geometry: shape color: color;
		}
	}
	
	species predator skills: [moving] schedules: shuffle (list (predator)) {
		geometry shape <- square (predator_size);
		prey target_prey update: self choose_target_prey [];
		
		action choose_target_prey type: prey {
			if ( (target_prey = nil) or (dead (target_prey) ) ) {
				return one_of ( (list (prey)) where (each.state = 'move_around') );
			}
			
			return target_prey;
		}
		
		reflex move_around when: (target_prey = nil) { do wander speed: predator_speed; }
		
		reflex chase_prey when: (target_prey != nil) { do move heading: self towards target_prey speed: predator_speed;}
		
		aspect default {
			draw geometry:shape color: predator_color;
		} 
	} 
	
	species shelter skills: [moving]  frequency: 2 {
		geometry shape <- (square (50.0)) at_location {250, 250};
		list<prey> chased_preys update: (prey) where ( (each.shape intersects shape) and (each.state = 'flee_predator') );
		
		reflex move_around {
			//do wander speed: shelter_speed; 
		}
		 
		reflex capture_chased_preys when: !(empty (chased_preys)) { 
			capture chased_preys as: prey_in_shelter {
				state <- 'in_shelter'; 
				shape <- ( triangle (4.0) ) at_location location;
			}
			
			start_sound source: '../includes/sounds/capture_sound.mp3';
		}
		
		reflex release_member_preys {
			list<prey_in_shelter> to_be_released <- (prey_in_shelter) where ( (time - each.in_shelter_time) > prey_in_shelter_max_time );
			 
			release to_be_released in: world as: prey { 
				state <- 'invisible';
				shape <-  at_location (square (prey_size), self.location);
				
				start_sound source: '../includes/sounds/release_sound.mp3';
			}
		} 
		
		
		species prey_in_shelter parent: prey frequency: 2 schedules: ( ( int ( (length (prey_in_shelter)) / 2 ) ) among (list (prey_in_shelter)) ) {
			var in_shelter_time type: int init: int(time);
			
			state in_shelter {
				do wander speed: shelter_speed;
			}
			
			aspect default {
				draw geometry: shape color: predator_in_shelter_color;
			} 
		}
		
		aspect default {
			draw geometry:shape color: shelter_color;
			draw text: 'Members: ' + (string (length ((members)))) color: rgb ('white') size: 6 at: {(location).x - 20, (location).y};
		}
	}

}


experiment default_experiment type: gui {
	output {
		display default_display type: opengl{
			species prey aspect: default;
			species predator transparency: 0.5 aspect: default;
			species shelter transparency: 0.5 aspect: default { 
				species prey_in_shelter aspect: default;
			}
		}
	}
}