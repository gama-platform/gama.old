/**
* Name: Multi-Level Architecture with Preys and Predators
* Author: 
* Description: This model shows how to use multi-level architecture. In this model, prey and predators agents move randomly 
*	 in the environment. When a prey agent perceive a predator, it flees trying to go to the closest shelters while the predator 
* 	 agent chases it. The shelters capture the prey agents fleeing, changing them into prey_in_shelter species, that predator 
* 	can't chase during a certain time. They are released after that time in an invisible state so that they can wander in the  
* 	environment without being chase by the predator, but also, for a certain time.
* Tags: multi_level, agent_movement
*/

model preys_predators_shelters

global { 
	//Parameters for the prey species
	rgb prey_color <- #green const: true;
	float prey_perception <- 20.0;
	float prey_size <- 2.0 const: true;
	float prey_speed <- 1.0;
	rgb prey_flee_color <- #orange;
	float prey_invisible_speed <- 3 * prey_speed;
	rgb prey_invisible_color <- #darkgray;  
	int prey_in_shelter_max_time min: 1 init: 200;
	int prey_invisible_max_time min: 1 max: 100 init: 70; 
	int number_of_preys min: 1 max: 1000 init: 500;
	
	//Parameters for the predator species
	rgb predator_color <- #red const: true;
	float predator_perception <- 3.0;
	float predator_size <- 4.0;
	float predator_speed <- 1.5;
	int number_of_predators min: 1 max: 100 init: 30; 
	geometry shape <- square(400);
	
	rgb predator_in_shelter_color <- #yellow const: true;
	 
	//Parameters for the shelter species
	rgb shelter_color <- #blue const: true; 
	float shelter_speed <- 1.5 const: true;
	geometry shelter_shape <- square (50.0);
	int number_of_shelter <- 2 const: true;
	
	
	init {
		create prey number: number_of_preys;
		create predator number: number_of_predators; 
		create shelter number: number_of_shelter {
			shape <- shelter_shape at_location any_point_in(world);
		}
	}
}
//Species prey which can move using the skill moving and its operators
species prey skills: [moving] control: fsm {
	geometry shape <- square (prey_size);
	rgb color <- prey_color;
	
	//List of all predators inside the perception of the prey
	list nearby_predators update:  predator overlapping (shape + prey_perception) ;
	int invisible_time min: 1 <- int(time);

	shelter nearest_shelter;		

	//State to make the prey move randomly when there isn't any predator, if so, change the state to flee
	state move_around initial: true {
		enter {
			color <- prey_color;
		}
		do wander speed: prey_speed; 
		
		transition to: flee_predator when: !(empty (nearby_predators)); 
	}
	
	//State to make the prey move to the closest shelter to flee from the predator, if no predator are perceived, change the state to move aroung
	state flee_predator {
		enter {
			color <- prey_flee_color;
			nearest_shelter <- shelter closest_to self;
		}
		if !(empty (nearby_predators)) { do move heading: (self) towards (nearest_shelter) speed: prey_speed;}
		
		transition to: move_around when: (empty (nearby_predators));
	}
	//State to make the prey invisible during a certain time when it is released by the shelters
	state invisible {
		enter {
			color <- prey_invisible_color;
			invisible_time <- int(time);
			heading <- rnd (360.0) ;
		}

		do move speed:prey_invisible_speed heading: heading ;
		transition to: move_around when: ( (time - invisible_time) > prey_invisible_max_time );
	}
	
	aspect default {
		draw  shape color: color;
	}
}

//Species predator which can move using the skill moving
species predator skills: [moving] schedules: shuffle (list (predator)) {
	geometry shape <- square (predator_size);
	prey target_prey update: self.choose_target_prey();
	
	//Change the target prey according to the prey who aren't fleeing if it doesn't have any yet
	action choose_target_prey type: prey {
		if ( (target_prey = nil) or (dead (target_prey) ) ) {
			return one_of ( (list (prey)) where (each.state = 'move_around') );
		}
		
		return target_prey;
	}
	//Reflex to move randomly when no prey are perceived
	reflex move_around when: (target_prey = nil) { do wander speed: predator_speed; }
	
	//Reflex to make the predator chase a prey
	reflex chase_prey when: (target_prey != nil) { do move heading: self towards target_prey speed: predator_speed;}
	
	aspect default {
		draw shape color: predator_color;
	} 
} 

//Species shelter that will capture prey agents
species shelter skills: [moving]  frequency: 2 {
	geometry shape <- (square (50.0)) at_location {250, 250};
	
	//List of all preys which are being chased and inside the shelter but not captured yet
	list<prey> chased_preys update: (prey) where ( (each.shape intersects shape) and (each.state = 'flee_predator') );
	
	//Capture all the chased preys inside the shelter and change their species to prey_in_shelter
	reflex capture_chased_preys when: !(empty (chased_preys)) { 
		capture chased_preys as: prey_in_shelter {
			state <- 'in_shelter'; 
			shape <- ( triangle (4.0) ) at_location location;
		}
	}
	
	//Release all the prey_in_shelter after a certain time and change their state to invisible after making their species returned to prey
	reflex release_member_preys {
		list<prey_in_shelter> to_be_released <- (prey_in_shelter) where ( (time - each.in_shelter_time) > prey_in_shelter_max_time );
		 
		release to_be_released in: world as: prey { 
			state <- 'invisible';
			shape <-  at_location (square (prey_size), self.location);   
		}
	} 
	
	//Subspecies prey_in_shelter that will represent the prey agents captured by the shelter
	species prey_in_shelter parent: prey frequency: 2 schedules: ( ( int ( (length (prey_in_shelter)) / 2 ) ) among (list (prey_in_shelter)) ) {
		int in_shelter_time <- int(time);
		
		state in_shelter {
			do wander speed: shelter_speed;
		}
		
		aspect default {
			draw  shape color: predator_in_shelter_color;
		} 
	}
	
	aspect default {
		draw shape color: shelter_color;
		draw (string (length ((members)))) color: rgb ('white') size: 6 at: location anchor: #center font: font("Helvetica", 12*#zoom, #bold);
	}
}

experiment default_experiment type: gui {
	output {
		display default_display background: #black{
			species prey aspect: default;
			species predator transparency: 0.5 aspect: default;
			species shelter transparency: 0.5 aspect: default { 
				species prey_in_shelter aspect: default;
			}
		}
	}
}