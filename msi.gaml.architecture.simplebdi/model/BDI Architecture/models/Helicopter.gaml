model helicopter

global {
	init {
		create helicopter;
		create fireArea;
		create waterArea;
	}
}

species fireArea {
	int size<-10;
	
	aspect base {
	    draw circle(1) color: #red;   
	}
}

species waterArea {
	int size<-10;
	
	aspect base {
	    draw circle(1) color: #blue;   
	}
}

species helicopter skills:[moving] control: simple_bdi{
	float waterValue <- 1.0;
	float speed <- 10.0;
	
	predicate patrol_desire <- new_predicate("patrol") with_priority 1;
	predicate water_predicate <- new_predicate("has water", true) with_priority 3;
	predicate no_water_predicate <- new_predicate("has water", false) ;
	
	aspect base {
	    draw circle(1) color: #black;   
	}
	
	init {
		waterValue <-1.0;
		do add_desire(patrol_desire);
		intention_persistence <- 1.0;
		plan_persistence <- 1.0;
		probabilistic_choice <- false;  
	}
	
	perceive target:self{
	    if(waterValue>0){
	        do add_belief(water_predicate);
	        do remove_belief(no_water_predicate);
	    }
	    if(waterValue<=0){
	        do add_belief(no_water_predicate);
	        do remove_belief(water_predicate);
	    }
	}
	
	perceive target:fireArea in: 10{
	    focus fireLocation var:location priority:10;
	    ask myself{
	        do remove_intention(patrol_desire, true);
	    }
	}
	
	rule belief: new_predicate("fireLocation") new_desire: get_belief_with_name("fireLocation");
	rule belief: no_water_predicate new_desire: water_predicate;
	
	plan patrolling intention: patrol_desire{
		do wander;
	}
	
	plan stopFire intention: new_predicate("fireLocation") {
	    point target_fire <- point(get_current_intention().values["location_value"] );
	    if(waterValue>0){
	        if (self distance_to target_fire <= 1) {
	            fireArea current_fire <- fireArea first_with (each.location = target_fire);
	            if (current_fire != nil) {
	                 waterValue <- waterValue - 1.0;
	                 current_fire.size <-  current_fire.size - 1;
	                 if ( current_fire.size <= 0) {
	                    ask  current_fire {do die;}
	                    do remove_belief(get_current_intention());
	                    do remove_intention(get_current_intention(), true);
	                    do add_desire(patrol_desire);
	                }
	            } else {
	                do remove_belief(get_current_intention());
	                do remove_intention(get_current_intention(), true);
	                do add_desire(patrol_desire);
	            }
	        } else {
	            do goto target: target_fire;
	        }
	    } else {
	        do add_subintention(get_current_intention(),water_predicate,true);
	        do current_intention_on_hold();
	    }
	}
	
	plan gotoTakeWater intention: water_predicate {
        waterArea wa <- first(waterArea);
        do goto target: wa;
        if (self distance_to wa <= 1) {
            waterValue <- waterValue + 2.0;
    	}
	}
}

experiment expe type:gui {
	output {
		display display1 {
			species helicopter aspect:base;
			species fireArea aspect:base;
			species waterArea aspect:base;
		}
	}
}