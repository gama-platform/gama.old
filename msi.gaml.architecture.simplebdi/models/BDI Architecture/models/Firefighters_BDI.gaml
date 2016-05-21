/**
 *  Firefighters
 *  Author: Mathieu Bourgais
 *  Description: A simple model to show how the BDI architectures and its tools work (perceive, rule, etc). It's the model of a helicopter that fights fires.
 * the Chopper agent has a general desire to patrol. As it is the only thing he wants at the beginning, it is its initial intention (what it is doing). 
 * To patrol, it wanders around (its plan to patrol). When it perceives a fire, it stores this information (it has a new belief about the existence of this fire), 
 * and it has a new desire (it wants the fire to be extinct). When it sees a fire, the Patrol intention is put on hold and a new intention is selected (to put out the fire). 
 * To achieve this intention, the plan has two steps, i.e. two new (sub)desires: go to the fire and put water on the fire. And so on.
 *  Tags: simple_bdi, perception, rule, plan, predicate
 */

model Firefighters

global {
	int displatTextSize <-4;
	init {
		create fireArea number:20;
		create waterArea number:1;
		create firefighter number: 2;
	}
	
	reflex stop when: length(fireArea) = 0 {
		do pause;
	}
}


//give the simple_bdi architecture to the firefighter agents
species firefighter skills: [moving] control: simple_bdi{	
	
	//Here are the variables used by a helicopter. We define the predicates that will be used later.
	rgb color <- rnd_color(150);
	float waterValue;
	grille maCellule <- one_of(grille);
	predicate patrol_desire <- new_predicate("patrol") with_priority 1;
	predicate water_predicate <- new_predicate("has water", ["water"::true]) with_priority 10;
	predicate no_water_predicate <- new_predicate("has water", ["water"::false]) ;

	//Definition of the variables featured in the BDI architecture.
	float plan_persistence <- 1.0; 
	float intention_persistence <- 1.0;
	bool probabilistic_choice <- false;
	
	//Initialisation of the agent. At the begining, the agent just has the desire to patrol.
	init {
		waterValue <-2.0;
		location<-maCellule.location;
		do add_desire(patrol_desire );
	}
	
	//This reflex is used to update the beliefs concerning the intern variable of the agent (the amount of water it has).
	reflex update_variables {
		if(waterValue>0){
			do add_belief(water_predicate);
			do remove_belief(no_water_predicate);
		}
		if(waterValue<=0){
			do add_belief(no_water_predicate);
			do remove_belief(water_predicate);
		}
	}
	
	//The helicopter perceive the fires at a certain distance. It just record the location of the fire it obsrves. When it sees a fire, it stops it's intention of patroling.
	perceive target:fireArea in: 15{
		focus var:location priority:11;
		ask myself{
			do remove_intention(patrol_desire, true);
		}
	}
	
	//The rules are used to create a desire from a belief. We can specify the priority of the desire with a statement priority.
	rule belief: new_predicate("location_fireArea") new_desire: get_belief_with_name("location_fireArea");
	rule belief: no_water_predicate new_desire: water_predicate;
	
	//The plan to do when the intention is to patrol.
	plan patrolling intention:patrol_desire finished_when: has_belief(new_predicate("location_fireArea")) or has_belief(no_water_predicate){
		do wander amplitude: 30 speed: 2;
	}
	
	//The plan that is executed when the agent got the intention of extinguish a fire.
	plan stopFire intention: new_predicate("location_fireArea") priority:5{
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
						do add_desire(patrol_desire );
					}
				} else {
					do remove_belief(get_current_intention());
					do remove_intention(get_current_intention(), true);
					do add_desire(patrol_desire );
				}
				
			} else {
				do goto(on: grille,target: target_fire,return_path:true);
			}
		} else {
			do add_subintention(get_current_intention(),water_predicate,true);
			do current_intention_on_hold();
		}
	}  
	
	//The plan to take water when the agent get the desire of water.
    plan gotoTakeWater intention: water_predicate priority:2 {
    	waterArea wa <- first(waterArea);
    	list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self goto(on: grille,target: wa,return_path:true);
    	if (self distance_to wa <= 1) {
    		waterValue <- waterValue + 2.0;
		}
    }

	aspect base {
		draw triangle(2) color:color rotate: 90 + heading;	
		draw circle(15) color: color ;	
	}
	
	aspect bdi {
		draw triangle(2) color:color rotate: 90 + heading;	
		draw circle(15) color: color empty: true;
		draw ("B:" + length(belief_base) + ":" + belief_base) color:#black size:displatTextSize; 
		draw ("D:" + length(desire_base) + ":" + desire_base) color:#black size:displatTextSize at:{location.x,location.y+displatTextSize}; 
		draw ("I:" + length(intention_base) + ":" + intention_base) color:#black size:displatTextSize at:{location.x,location.y+2*displatTextSize}; 
		draw ("curIntention:" + get_current_intention()) color:#black size:displatTextSize at:{location.x,location.y+3*displatTextSize}; 	
	}
}

species fireArea{
	float size <-1.0;
	
	init{
		grille place <- one_of(grille);
		location <- place.location;
	}
	
	aspect base {
	  draw circle(size) color: #red;
	}
}

species waterArea{
	init {
		grille place <- one_of(grille);
		location <- place.location;
	}
	aspect base {
	  draw square(5) color: #blue;		
	}
}

grid grille width: 25 height: 25 neighbors:4 {
	rgb color <- #palegreen;
}


experiment fight_fire type: gui {
	output {					
		display view1 { 
			grid grille lines: #black;
			species fireArea aspect:base;
			species waterArea aspect:base;
			species firefighter aspect: bdi;
		}
	}

}