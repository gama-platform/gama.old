/**
 *  testmodel
 *  Author: cheikh
 *  Description: 
 */

model testmodel

global {
	/** Insert the global definitions, variables and actions here */
	file shape_file_metro <- file(project_path + 'model_evacuation_fire/includes/metro.shp');
	int reachToGoal <- 0;
	int died <-0;
	int max_fire_duration <- 30 parameter: 'the maximum duration of fire';
	int number_of_people <- 1 parameter: 'number of people present in a building';
	float max_sensible_ability <-8.0 parameter: 'the maximum of sensibility';
	int propagation_range <- 4 parameter: 'the initial propagation of range';
	bool is_ringing <- false parameter: ' if the alarm is ringing or not';
	int max_ring_duration <- 500 parameter: 'the maximum duration of a ringing';
	int max_passed_list <- 20 parameter: 'the maximum possible in a list';
	int total_escaped_time <- 0;
	float total_escaped_power <- 0.0;
	float init_range <- 17.0;
	float speed <- 2.0 parameter: 'the initial speed of agents people ;the min speed msut be 1.0 ';
	float init_speed <- 2.0;
	float min_speed <- 1.0;
	float init_smoke_speed <- 5.0 parameter: ' the initial speed of a smoke';
	file peoples_data <- file(project_path + 'model_evacuation_fire/includes/people_location.data') ;
	matrix people_data <- peoples_data as matrix;
	metro the_metro;
	bool group_one <- false;
	bool group_two <- false;
	int blindWanderStrategy <- 0;
	int blindStraightStrategy <- 0;
	int blindWallTrackingtrategy <- 0;
	int approchAvoidPositionStrategy <- 0;
	
	
	
	/* Reflex for the end of simulation after time = 251 */
	reflex stop_simulation when: time = 100 {
		do halt;
	} 
	init {
		create metro from: shape_file_metro;
		set the_metro <- first(metro as list);
		
		 let list_goal_location <-[{40,2},{130,2},{255,2},{2,120},{2,248},{375,70},{375,145},{375,248}];
		 let indice <- 1;
		 loop loc over: list_goal_location {
		 	create 	principalGoal number: 1 {
				set location <- loc;   //point(40::2);
				set priority <- 1;
				set name <- 'exit'+indice;
				set indice <- indice + 1;
			}
		 }
		let list_instruction_location <-[{24,42},{64,42},{80,120},{80,186},{315,47},{315,105},{317,184}];
		 loop loc over: list_instruction_location {
		 	create instruction number:1 {
				set location <- loc;
				set direction <- list(principalGoal) with_min_of (each distance_to self);
			}
		 }
		/* Creation agents which represent goals */		
		loop i from: 0 to: 7 {
			create instruction number:1 {
				set location <- point(20::65+15.5*i);
				set direction <- list(principalGoal) with_min_of (each distance_to self);
			}
			create instruction number:1 {
				set location <- point(50::65+15.5*i);
				set direction <- list(principalGoal) with_min_of (each distance_to self);
			}
		}
		loop i from: 0 to: 2 {
			create instruction number:1 {
				set location <- point(20::192+15.5*i);
				set direction <- list(principalGoal) with_min_of (each distance_to self);
			}
			create instruction number:1 {
				set location <- point(50::192+15.5*i);
				set direction <- list(principalGoal) with_min_of (each distance_to self);
			}
		}
		loop i from: 0 to: 11 {
			let list_oordonne <- [70,100,135,165,200,230];
			loop ord over: list_oordonne {
				create instruction number:1 {
					set location <- point(103.5 + (17.5*i)::ord);
					set direction <- list(principalGoal) with_min_of (each distance_to self);
					// optimisation of agents
					if (direction.name = "exit7") {
						set direction <- one_of(principalGoal);
					}
				}
			}
			
		}
		loop i from: 0 to: 5 {
			create instruction number:1 {
				set location <- point(20 + 30*i::252);
				set direction <- list(principalGoal) with_min_of (each distance_to self);
			}
			create instruction number:1 {
				set location <- point(200 + 30*i::252);
				set direction <- list(principalGoal) with_min_of (each distance_to self);
			}
		}
		/* Creation agents which represent fire */
		/**create fire number:3 {
			set location <- point(180::150);
		}*/ 
			
		/* Creation agents of people in a building length(rows_list(people_data))*/
		loop i from: 0 to: length(rows_list(people_data)) {
			 create people number: 1 {
			 	let var_x type: float <- people_data at {0,i};
			 	let var_y type: float <- people_data at {1,i};
				set location <- point({var_x,var_y});
				if ((var_x >= 130) and (var_x <= 170) and (var_y <= 210) and (var_y >= 190)) {
					set self.color <- rgb('red');
					set self.numberGroup <- 1;
					if (group_one = false) {
						set chief_of_group <- true;
						set group_one <- true;
					}
				}
				if ((var_x >= 210) and (var_x <= 240) and (var_y <= 120) and (var_y >= 100)) {
					set self.color <- rgb('red');
					set self.numberGroup <- 2;
					if (group_two = false) {
						set self.chief_of_group <- true;
						set group_two <- true;
					}
				}
				if (chief_of_group) {
					set self.color <- rgb('white');
					set target <- one_of (list (principalGoal));
				} 
				//set location <-  any_location_in (the_metro.shape buffer (0.3));//point({375,78});//
				if (self.numberGroup = 0) or(self.numberGroup = 2) {
			 		set target <-  (list (principalGoal)) with_min_of (each distance_to self);//one_of (list (principalGoal));
			 	}
			 	set target.color <- rgb('magenta');
			 	set passedList <- [self.location];
			 	set passedSigns <- nil;
			 }
		}	
		let list_location_agent_responsable <- [{103,185},{247,116},{90,115},{258,205}];
			loop loc over: list_location_agent_responsable {
				create people number:1 {
					set self.numberGroup <- 10;
					set self.color <- rgb('green');
					set self.location <-loc;
					set self.size <- 3.0;
					set self.is_responsible <- true;
					set target <- (list (principalGoal)) with_min_of (each distance_to self);
				}
			}
			
		save species: instruction to: (project_path + 'model_evacuation_fire/includes/instruction-generated.shp') type: "shp" ;
	}
}

environment bounds: shape_file_metro;

entities {
	/** Insert here the definition of the species of agents */
	species metro {
		rgb color <- rgb('yellow');
		aspect base {
         draw geometry: shape color: color ;
      }
	}	
	/* Agents for signs */
	species principalGoal {
		rgb color <- rgb('red');
		int priority <- 1; 
		string name;
		aspect base {
        	draw geometry: rectangle({10,6}) color: color ;
     	}
	}
	species instruction {
		rgb color <- rgb('green');
		principalGoal direction;
		aspect base {
        	draw geometry: rectangle({11,2}) color: color ;
     	}
	}
	
	/* Agents who represent fire */
	species fire {
		rgb color <- rgb([255,rnd(255),0]);
		float size <- 12.0;
		int duration <- rnd(max_fire_duration);
		int propagating_threshold <- 10;
		// propagating_threshold <- 10;
		int sign1;
		aspect base {
        	draw shape: circle size: size color: color ;
     	}
     	
		// define reflex and actions 
		reflex burning {
			set color <- rgb([255, rnd(255),0]);
			
			if (self neighbours_at (3) contains (one_of(principalGoal))) {
				do die;
			}
			create smoke number:8 with: [location::self.location]{	
					set sign1 <- rnd(3);
					if (sign1 = 0) {
						set self.location <- {self.location.x - self.size,self.location.y - self.size};
					}
					if (sign1 = 1) {
						set self.location <- {self.location.x - self.size,self.location.y + self.size};
					}
					if (sign1 = 2) {
						set self.location <- {self.location.x + self.size,self.location.y - self.size};
					}
					if (sign1 = 3) {
						set self.location <- {self.location.x + self.size,self.location.y + self.size};
					}
					
		  }
		  set duration <- duration - 1;
		  if (duration <= 0 ) {
		  	do die;
		  }
		  if (duration = propagating_threshold) {
		  	create fire number: rnd(propagation_range) with: [location::self.location]{
		  		set sign1 <- rnd(3);
		  		if (sign1 = 0) {
						set self.location <- {self.location.x - self.size,self.location.y - self.size};
					}
					if (sign1 = 1) {
						set self.location <- {self.location.x - self.size,self.location.y + self.size};
					}
					if (sign1 = 2) {
						set self.location <- {self.location.x + self.size,self.location.y - self.size};
					}
					if (sign1 = 3) {
						set self.location <- {self.location.x + self.size,self.location.y + self.size};
					}		
		  	}
		  	
		  }
		}
	}
	
	/* Agents who represent smoke effect */
	species smoke skills: [humanmoving] {
		rgb color <- rgb('black');
		float speed <- init_smoke_speed;
		float size <- 0.2;
		int sign;
		int sign1;
		point local;
		// define reflex for this agents
		/* reflex for a propagation of smoke */
		
		reflex for_move {
				do move speed: speed  heading: rnd(350) bounds: circle(20);
				if (self neighbours_at (3) contains (one_of(principalGoal))) {
					do die;
				}
			}
	}
	
	/* Agents who represent people */
	species people skills: [humanmoving] {
		rgb color <- rgb('blue');
		principalGoal target;
		float size <- 3.0;
		float range <- init_range;
		bool is_escaped <- false;
		list passedList <- nil;
		list passedSigns <- nil;
		float smoke_intensity <- 0.0;
		float sensible_ability <- 7.5;
		float max_power <- 1.0;
		float power <- max_power;
		int start_escape_time <- -1;
		instruction founded_instruction;
		float vitesse <- speed;
		bool changeDirection <- false;
		int direction <- 6;
		int numberGroup <- 0;
		bool chief_of_group <- false;
		bool is_responsible <- false;
		int is_in_loop <- 0;
		aspect base {
        	draw shape: circle size: size color: color ;
     	}
     	     	
     	// reflex of this agent
		reflex perception {
			if (self.numberGroup = 1) and (self.chief_of_group = false) {
				set target <-first((list(people) where ((people(each).chief_of_group = true) and (people(each).numberGroup = 1)) ));	
			}
			if (self.numberGroup = 2) and (self.chief_of_group = false) {
				set target <-first((list(people) where ((people(each).chief_of_group = true) and (people(each).numberGroup = 2)) ));	
			}
			if (time = 10) {
				if (start_escape_time = -1) {
					set start_escape_time <- time;
					set is_escaped <- true;
				}
			}
			if ((self.changeDirection = true)) {
				//write("il doit entrer ici 3 fois au max fois et quand ya boucle seulement"+agent);
				if (time >= 100) {
					if (self.chief_of_group = true) or (self.numberGroup = 0) {
						set target <-  (list (principalGoal)) with_min_of (each distance_to self);//one_of(principalGoal) ;
						}
				}
				if (self.chief_of_group = true) or (self.numberGroup = 0){
					set self.target <-  (list (principalGoal)) with_min_of (each distance_to self);
				}// one_of (list (principalGoal));
				set self.changeDirection <- false;
			}
			if (self neighbours_at (4) contains self.target) {
				set reachToGoal <- reachToGoal + 1;
				set total_escaped_time <- total_escaped_time + time - start_escape_time;
				set total_escaped_power <- total_escaped_power + self.power/self.max_power;
				 if (self.chief_of_group) {
				 	/*if (length( (list(people) where (people(each).numberGroup = self.numberGroup) ) at_distance 60) = 1) {
				 		write('you can kill this chief agent' + (length( (list(people) where (people(each).numberGroup = self.numberGroup) ) at_distance 40)));
				 		do die;
				 	}*/
				 	
	  		    } else {
	  		    	if (list(principalGoal) contains self.target){
				 		do die;
				 	}
				 	if (self neighbours_at (5) contains one_of(list(principalGoal))) { write("un des agents à cote est une porte"); do die;}
	  		    }
				
			}
			// a implementation of passedList
		/*	if (self.passedList contains self.location) {
				set is_in_loop <- is_in_loop + 1;
			}
			if (is_in_loop > 15) and (time >20) {
				write("changement de goal pour cause de boucle");
				set self.target <- one_of(principalGoal);
			}
			set passedList <- [self.location] + self.passedList;
			if (length(self.passedList) > max_passed_list) {
				set self.passedList <- self.passedList copy_between{0,max_passed_list};
			}*/
			//*****************
			set founded_instruction <- one_of (self neighbours_at (range) of_species instruction);
			if (founded_instruction != nil) {
				if (!(founded_instruction in passedSigns)) {
					if (self.chief_of_group = true) or (self.numberGroup = 0) {
						set target <- founded_instruction.direction;
					}
					set passedSigns <- [founded_instruction] + self.passedSigns;
					if (length(passedSigns)> 4) {
						set passedSigns <- self.passedSigns copy_between{0,4};
					}
				}
			}
			
			set founded_instruction <- nil;
			
			/* perception of fire */
			if (length (self neighbours_at range of_species fire) >= 1){
				if (start_escape_time = -1) {
					set start_escape_time <- time;
					set is_escaped <- true;				
				}	
			}
	  	}
	  	
     	/* reflex for escaping */
	  	reflex escaping when: (is_escaped and ( (numberGroup = 0) or ( numberGroup = 1 and chief_of_group = false) or  ( numberGroup = 2 and chief_of_group = false) or (length( (list(people) where (people(each).numberGroup = self.numberGroup) ) at_distance 10) > 1) or (length((self neighbours_at 25) of_species (species (smoke))) > 0)or (length((self neighbours_at 25) of_species (species (people))) <= 1))) {
	  		  
	  		 do blindWallTracking2 background: the_metro agent_size: 2  speed: 5;
	  		 if (self.range <= (self.size + 1)) {
	  				// do blindWander2 background: the_metro agent_size: 2  speed: 5;
	  				 write("this agent is blind") ;
	  				 set  blindWanderStrategy <- blindWanderStrategy + 1;
	  		 }
	  		 else {
	  		// do approachAvoidPassedPosition2 background: the_metro agent_size: 2  speed: 2;
	  		// do blindWander2 background: the_metro agent_size: 2  speed: 5;
	  		//  do blindStraightWander2 background: the_metro agent_size: 2  speed: 5;
	  		   set blindStraightStrategy <- blindStraightStrategy + 1;
	  		// do blindWallTracking2 background: the_metro agent_size: 2  speed: 5;
	  		}
	  		 do tired reduce_power: (0.001)*(self.sensible_ability/max_sensible_ability);
	  	}
     	 /* reflex for a smoke */
		reflex in_smoke {
	  		set smoke_intensity <- (length (self neighbours_at (5*size) of_species smoke))/30;
	  		if (smoke_intensity > 1.0) {
	  			set smoke_intensity <- 1.0;
	  		}
	  	set range <- 0.1+((1-smoke_intensity)*init_range);
	  	set speed <- min_speed+((1-smoke_intensity)*(init_speed-min_speed)*(power/max_power));
	  	
	  	if ( (length (self neighbours_at (5*size) of_species smoke))>= 1 ) {
	  		do tired reduce_power: (length (self neighbours_at (5*size) of_species smoke))*(((0.01)*self.sensible_ability) / max_sensible_ability);
	  	}
	  }
	  /* reflex for a fire */
	  reflex in_fire {
	  	if (length (self neighbours_at (5*size) of_species fire) > 0) {
	  		do tired reduce_power: (length (self neighbours_at (5*size) of_species fire))*(((0.02)*self.sensible_ability) / max_sensible_ability);
	  	}  	
	  }
	  action tired {
	  	arg reduce_power type: float default: 0.0;
	  	if ((self.power - reduce_power) > 0) {
	  		set self.power <- self.power - reduce_power;
	  	}
	  	else {
	  		set died <- died +1;
	  			do die;
	  	}
	  }
	}
	
}

experiment testmodel type: gui {
	/** Insert here the definition of the input and output of the model */
		output {
		display objects_display refresh_every: 1 {
			species metro aspect: base ;
			species principalGoal aspect: base ;
			species instruction aspect: base ;
			//species fire aspect: base ;
			//species smoke aspect: base ;
			species people aspect: base ;			
		}
		display chart_display refresh_every: 10 {
			chart name: 'Evolution of the fear' type: series background: rgb('lightGray') size: {0.9, 0.4} position: {0.05, 0.05} {
				data 'Saved people' value:(100*reachToGoal)/number_of_people style: line color: rgb('green') ;
				data 'Died people' value: (100*died)/number_of_people style: line color: rgb('red') ;
			}
		}
		monitor number_of_people_quiting value: reachToGoal refresh_every: 10 ;
		monitor number_of_people_died value: died refresh_every: 10 ;
		monitor total_of_escape_time value: total_escaped_time refresh_every: 10 ;
		monitor total_of_power value: total_escaped_power refresh_every: 10 ;
		monitor max_fire_duration value: max_fire_duration refresh_every: 10 ;
		monitor blind_wander_strategy value: blindWanderStrategy refresh_every: 10 ;
		monitor blind_straight_strategy value: blindStraightStrategy refresh_every: 10 ;
		monitor blind_Wall_Tracking_Strategy value: blindWallTrackingtrategy refresh_every: 10 ;
		monitor approch_Avoid_Position_Strategy value: approchAvoidPositionStrategy refresh_every: 10 ;		
	}
}

experiment testmodel3D type: gui {
	/** Insert here the definition of the input and output of the model */
		output {
		display objects_display refresh_every: 4 type:opengl {
			species metro aspect: base ;
			species principalGoal aspect: base ;
			species instruction aspect: base ;
			species fire aspect: base ;
			species smoke aspect: base ;
			species people aspect: base ;			
		}

		monitor number_of_people_quiting value: reachToGoal refresh_every: 10 ;
		monitor number_of_people_died value: died refresh_every: 10 ;
		monitor total_of_escape_time value: total_escaped_time refresh_every: 10 ;
		monitor total_of_power value: total_escaped_power refresh_every: 10 ;
		monitor max_fire_duration value: max_fire_duration refresh_every: 10 ;
	}
}
