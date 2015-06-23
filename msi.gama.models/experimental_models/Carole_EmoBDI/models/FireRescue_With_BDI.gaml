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
	
	reflex end_simulation when: length(fireArea) = 0 or length(waterArea) = 0 {
		do pause;
	}
}


species helicopter skills: [moving] control: simple_bdi{
	rgb color;
	float waterValue;
	bool needToTakeWAter;
	float speed <- 10.0;
	predicate patrol_desire <- new_predicate("patrol") with_priority 1;
	predicate fire_predicate <- new_predicate("fire") ;
	predicate water_predicate <- new_predicate("has water", true) with_priority 3;
	predicate no_water_predicate <- new_predicate("has water", false) ;
	bool target_fire_extinguish <- false;
	list<BDIPlan> myplans<-get_plans();
	BDIPlan oneplan<-one_of(myplans);
	init {
		waterValue <-1.0;
		needToTakeWAter <-true;
		do add_belief(water_predicate);
		do add_desire(patrol_desire );
		intention_persistence <- 0.0;
		plan_persistence<-0.5;
		loop p over:myplans
		{
			write("plan name "+p.name+" todo:"+p.todo+" finished: "+p.finished_when);
			// todo = self.is_current_intention(predicate:patrol_desire) 
			// finished: self.has_belief(predicate:fire_predicate)
			//p.todo.match("self.is_current_intention(predicate:??");
			
			string tosplit <- 'newpred(something(a),something(b))';
			list split <- tosplit split_with ':';
			string gauche <- first(split);
			string droit <- last(split);
			write 'gauche = '+gauche;
			write 'droite = '+droit;
			list split2 <- droit split_with ')';
			string avp <- first(split2);
			string app <- last(split2);
			write 'avant parenthese '+avp+'-';
			write 'apres parenthese '+app+'-';
			
			write 'test condition '+eval_gaml(p.todo);
			write 'test effect '+eval_gaml(p.finished_when);
		}
		
		
		
	}
	reflex perception {
		loop fire over: fireArea at_distance 10 {
			if (not has_belief(new_predicate("fire", fire.location))) {
				do add_belief(new_predicate("fire", ["value"::fire.location,"extinguish"::false], 2));		
				do add_desire(new_predicate("fire", ["value"::fire.location,"extinguish"::true], 2));
				do remove_intention(patrol_desire,false);
				
			}
		}
		
	}
	
	plan test when: is_current_intention(new_predicate('bonjour',false)) finished_when:true {
		do wander;
	}
	
	plan patrolling when: is_current_intention(patrol_desire) finished_when: has_belief(fire_predicate){
		do wander;
	}
	
	plan stopFire when: (is_current_intention(fire_predicate) and has_belief(no_water_predicate)) finished_when: true piority:10
	{
		do add_subintention(get_current_intention(), water_predicate);
		do add_desire(water_predicate);
		do current_intention_on_hold();	
	}


	plan stopFire when: is_current_intention(fire_predicate) finished_when: target_fire_extinguish or has_belief(no_water_predicate){
		predicate current_fire <- get_current_intention();
		point target_fire <- point(current_fire at "value");
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
					do remove_belief(new_predicate("fire", ["value"::target_fire,"extinguish"::false], 2));
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

    plan gotoTakeWater instantaneous: true when: is_current_intention(water_predicate) finished_when: has_belief(water_predicate) {
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
		write(thinking);	
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
		display current_intention {
			chart "helicopter plan" type: series {
				data "patrolling" value: helicopter count (each.get_current_intention() != nil and each.get_current_intention().name = "patrol") color: #gray;
				data "patrolling" value: helicopter count (each.get_current_intention() != nil and each.get_current_intention().name = "fire") color: #red;
				data "patrolling" value: helicopter count (each.get_current_intention() != nil and each.get_current_intention().name = "has water") color: #blue;
			}
		}
	}

}