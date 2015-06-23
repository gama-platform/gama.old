/**
 *  bdirescue
 *  Author: carole
 *  Description: 
 */

model bdirescue

import "bdimodel.gaml"

/* Insert your model definition here */


global {
	int displatTextSize <-4;
	
	int dimensions <- 100;
	
	init {
		create base number: 1;
		create fireArea number:3;
		create waterArea number:1;
		create helicopter number: 1;
	}
}

grid space width: dimensions height: dimensions neighbours: 8 use_regular_agents: false frequency: 0{
		const color type: rgb <- rgb("black");
	}

species helicopter skills: [moving]  parent: bdiagent {

	rgb color;

	// level of water of the helicopter
	float waterValue;
	
	// does the helicopter need to take water
	bool needToTakeWAter function: { bool(waterValue<5)};
	
	bool target_fire_extinguish <- false;
	
	// target destination of helicopter
	point target <- any(base).location;
	
	// predicate telling if the helicopter is patrolling
	predicate patrol_predicate <- new_predicate("patrol") with_priority 1;
	
	// predicate telling if the helicopter perceives a fire
	predicate fire_predicate <- new_predicate("seefire") ;
	predicate nofire_predicate <- new_predicate("notseefire"); //,["value"::false]);
	
	// predicate telling if the helicopter has water
	predicate haswater_predicate <- new_predicate("has water", ["value"::true]) with_priority 3;
	predicate nowater_predicate <- new_predicate("not has water", ["value"::true]);
	
	// predicate telling if the helicopter sees water
	predicate seeswater_predicate <- new_predicate("sees water");
	
	//predicate no_water_predicate <- new_predicate("has water", ["value"::false]) ;
	
	// implication predicate (for rules)
	predicate canfight_predicate <- new_predicate("can fight fire");
	predicate implic1 <- new_predicate("rule1",["type"::"implication","premise"::haswater_predicate,"conclu"::canfight_predicate]);

	init {
		// initial water value = full tank
		waterValue <-10.0;
		
		// initial position on base
		location <- any(base).location;
		
		//needToTakeWAter <-true;
		//do add_belief(haswater_predicate);
		
		// initial desire = patrolling
		//write "*** 0) D:" + length(desire_base) + ":" + desire_base ; 
		
		do add_desire(patrol_predicate);
		//write "*** 1) D:" + length(desire_base) + ":" + desire_base ; 
		
		do add_desire(seeswater_predicate);
		//write "*** 2) D:" + length(desire_base) + ":" + desire_base ; 
		
		do add_desire(nofire_predicate);
		//write "*** 3) D:" + length(desire_base) + ":" + desire_base ; 
		
		//do add_desire(haswater_predicate);
		
		// implication rule
		do add_belief(implic1);
		
	}


	reflex perception {
		write 'perception';
		
		loop fire over: fireArea at_distance 10 {
			if (not has_belief(new_predicate("firelocation", ["value"::fire.location]))) {
				do add_belief(new_predicate("firelocation", ["value"::fire.location,"extinguish"::false], 2));		
				//do add_desire(new_predicate("fire", ["value"::fire.location, "extinguish"::true], 2));
				//do remove_intention(patrol_desire,false);	
			}
		}
		
		// remove belief fire if too far
		if empty(fireArea at_distance 10) {
			do remove_belief(fire_predicate);
			do add_belief(nofire_predicate);
		}
		else {
			do add_belief(fire_predicate);
			do remove_belief(nofire_predicate);
		}
		
		// handle water perception
		loop water over: waterArea at_distance 10 {
			if (not has_belief(new_predicate("waterlocation", ["value"::water.location]))) {
				// todo: add limited amount of water in area
				do add_belief(new_predicate("waterlocation", ["value"::water.location], 2));
			}
		}
		
		// no water in sight
		if empty(waterArea at_distance 10) {
			do remove_belief(seeswater_predicate);
		}
		else {
			write 'see water !!';
			do add_belief(seeswater_predicate);
			
			write "---> D:" + length(desire_base) + ":" + desire_base ; 
			write "---> B:" + length(belief_base) + ":" + belief_base ; 
		
		}
		
	}

	// modus ponens	
	reflex inference {
		write 'inference';
		do update_kb;
	}
	
	reflex update {
		write 'update';
		
		if (waterValue>0) {
			do add_belief(haswater_predicate);
		}
		else {
			do remove_belief(haswater_predicate);
		}
		
		// update desire to take water depending on current level
		if (needToTakeWAter) {
			do add_desire(haswater_predicate);
		} 
		else {
			do remove_desire(haswater_predicate);
		}
		
		// update desire to extinguish fire depending on perceptions
		if (target_fire_extinguish) {
			do add_desire(nofire_predicate);
		}
		
		// consistency between beliefs fire/no fire 
		// FIXME: en attendant des predicats avec valeur de verite
		if (fire_predicate in belief_base) {
			do remove_belief(nofire_predicate);
		}
		if (nofire_predicate in belief_base) {
			do remove_belief(fire_predicate);
		}
		
		// negation water / no water
		if (has_belief(haswater_predicate)) {
			do remove_belief(nowater_predicate);
		}
		if (has_belief(nowater_predicate)) {
			do remove_belief(haswater_predicate);
		}
				
	}
	
	action test {}

	// patrol from border to border of world
	plan smart_patrol when: is_current_intention(patrol_predicate) or is_current_intention(seeswater_predicate)
						priority:1 finished_when: target != nil and self distance_to target < 0.1 {
			
		write 'smart patrol';
		do remove_desire(patrol_predicate);
		// si positionne sur le contour
		//write 'contour = '+world.shape.contour;
		if (self distance_to world.shape.contour) < 0.1 or target = nil or target = any(base).location {
			// quand atteint contour, vise un autre point
			target <- any_location_in(world.shape.contour); //(world.shape farthest_point_to self.location);
		}
		do goto target: target;
			
	}
	
	
/* 	plan patrolling when: is_current_intention(patrol_predicate) or is_current_intention(seeswater_predicate) 
					finished_when: has_belief(nofire_predicate) priority: 0.1{
		write "patrolling";
		do remove_desire(patrol_predicate);
		do wander;
	}
*/

	
	plan gotoTakeWater when: is_current_intention(haswater_predicate) finished_when: has_belief(haswater_predicate) {
    	write "gotoTakeWater";
    	waterArea wa <- first(waterArea);
    	do goto target: wa ;
    	if (self distance_to wa < 1) {
    		waterValue <- waterValue + 1.0;
    		wa.size <- wa.size - 1.0;
    		if ( wa.size = 0) {
				ask  wa {do die;}
			}
			do add_belief(haswater_predicate);	
			//do remove_belief(no_water_predicate);	
			do remove_intention(haswater_predicate,true);
		}
    }
	

	// fight fire until extinguished or out of water
	plan stopFire when: is_current_intention(nofire_predicate) finished_when: target_fire_extinguish or has_belief(nowater_predicate){
		write "stopFire";
		point target_fire <- point(get_current_intention() );
		// if in position: fight
		if (self distance_to target_fire < 1) {
			fireArea current_fire <- fireArea first_with (each.location = target_fire);
			if (current_fire != nil) {
				 waterValue <- waterValue - 0.5;
				 if (waterValue = 0) {
				 	// FIXME could/should be automatic
				 	do remove_belief(haswater_predicate);
				 	do add_belief(nowater_predicate);
				 	do add_desire(haswater_predicate);
				 }
				 current_fire.size <-  current_fire.size - 1;
				 if ( current_fire.size = 0) {
					ask  current_fire {do die;}
					do remove_belief(new_predicate("fire", ["value"::target_fire, "extinguish"::false], 2));
					do remove_intention(fire_predicate,true);
					target_fire_extinguish <- true;
				}	
				else {
					target_fire_extinguish <- false;
				}	
			}
			
		} 
		// if not at the position of the fire yet: go there
		else {
			do goto target: target_fire;
		}
	} 


	aspect base {
		draw circle(1) color: rgb(0,0,waterValue);	
	}
	
	aspect bdi {
		draw circle(1) color: rgb(0,0,waterValue);
		draw ("B:" + length(belief_base) + ":" + belief_base) color:#black size:displatTextSize; 
		draw ("D:" + length(desire_base) + ":" + desire_base) color:#black size:displatTextSize at:{location.x,location.y+displatTextSize}; 
		draw ("I:" + length(intention_base) + ":" + intention_base) color:#black size:displatTextSize at:{location.x,location.y+2*displatTextSize}; 
		draw ("curIntention:" + get_current_intention()) color:#black size:displatTextSize at:{location.x,location.y+3*displatTextSize};
		//write "curIntention:" + get_current_intention(); 	
	}
	
	
	 
	 	
	aspect emo {
		draw circle(1) color: rgb(0,0,waterValue);
		//draw("E:"+test_emotion) color:#black size:displatTextSize at:{location.x,location.y+3*displatTextSize} ;
		//draw ("Em:" + length(emotion_base) + ":" + emotion_base) color:#black size:displatTextSize at:{location.x-2*displatTextSize,location.y+2*displatTextSize};
		//write "Emax:"+test_emotion;
		draw ("Em:" + length(emotion_base) + ":" + ((emotion_base where !dead(each as emotion)) collect (each as emotion).name)) 
				color:#black size:displatTextSize at: {10,10};  //{location.x-2*displatTextSize,location.y+2*displatTextSize};
		draw ("curIntention:" + get_current_intention()) color:#black size:displatTextSize at:{10,20};
	}
	 
	
	
}



species fireArea{
	float size <-5.0;
	aspect base {
	  draw circle(size) color: #red;
	}
}

species waterArea{
	float size <-10.0;
	aspect base {
	  draw circle(size) color: #blue;		
	}
}

species base {
	float size <- 5.0;
	aspect base {
		draw square(size) color: #orange;
	}
}

experiment K_RESCUE type: gui {

	output {		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display view1 { //type: opengl { 
			species fireArea aspect:base;
			species waterArea aspect:base;
			species base aspect: base;
			species helicopter aspect: emo;
		}
	}
}
