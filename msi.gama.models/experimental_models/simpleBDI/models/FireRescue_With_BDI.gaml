/**
 *  new
 *  Author: Arno
 *  Description: 
 */

model FireRescueWithBDI

global {
	int displatTextSize <-4;
	init {
		create fireArea number:3;
		create waterArea number:1;
		create helicopter number: 1;
	}
}


species helicopter skills: [moving] control: simple_bdi{
	rgb color;
	float waterValue;
	bool needToTakeWAter;
	predicate patrol_desire <- new_predicate("patrol") with_priority 1;
	predicate fire_predicate <- new_predicate("fire") ;
	predicate water_predicate <- new_predicate("has water", true) with_priority 3;
	predicate no_water_predicate <- new_predicate("has water", false) ;
	bool target_fire_extinguish <- false;

	init {
		waterValue <-1.0;
		needToTakeWAter <-true;
		do add_belief(water_predicate);
		do add_desire(patrol_desire );
		
	}
	reflex perception {
		loop fire over: fireArea at_distance 10 {
			if (not has_belief(new_predicate("fire", fire.location))) {
				do add_belief(new_predicate("fire", fire.location, ["extinguish"::false], 2));		
				do add_desire(new_predicate("fire", fire.location, ["extinguish"::true], 2));
				do remove_intention(patrol_desire,false);
				
			}
		}
		
	}
	plan patrolling when: is_current_intention(patrol_desire) finished_when: has_belief(fire_predicate){
		write "patrolling";
		do wander;
	}
	
	plan stopFire when: is_current_intention(fire_predicate) finished_when: target_fire_extinguish or has_belief(no_water_predicate){
		write "stopFire";
		point target_fire <- point(get_current_intention().value );
		if (self distance_to target_fire < 1) {
			fireArea current_fire <- fireArea first_with (each.location = target_fire);
			if (current_fire != nil) {
				 waterValue <- waterValue - 0.5;
				 if (waterValue = 0) {
				 	do remove_belief(water_predicate);
				 	do add_belief(no_water_predicate);
				 	do add_desire(water_predicate);
				 }
				 current_fire.size <-  current_fire.size - 1;
				 if ( current_fire.size = 0) {
					ask  current_fire {do die;}
					do remove_belief(new_predicate("fire", target_fire, ["extinguish"::false], 2));
					do remove_intention(fire_predicate,true);
					target_fire_extinguish <- true;
					
				}	else {
					target_fire_extinguish <- false;
				}	
			}
			
		} else {
			do goto target: target_fire;
		}
	} 

    plan gotoTakeWater when: is_current_intention(water_predicate) finished_when: has_belief(water_predicate) {
    	write "gotoTakeWater";
    	waterArea wa <- first(waterArea);
    	do goto target: wa ;
    	if (self distance_to wa < 1) {
    		waterValue <- waterValue + 1.0;
    		wa.size <- wa.size - 1.0;
    		if ( wa.size = 0) {
				ask  wa {do die;}
			}
			do add_belief(water_predicate);	
			do remove_belief(no_water_predicate);	
			do remove_intention(water_predicate,true);
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

experiment RESCUE type: gui {

	output {		//Display the textured grid in 3D with the cell altitude corresponding to its grid_value.				
		display view1 type:opengl{ 
			species fireArea aspect:base;
			species waterArea aspect:base;
			species helicopter aspect: bdi;
		}
	}

}