/**
 *  modelTestBDI
 *  Author: mathieu
 *  Description: A simple model to show how the BDI architectures and its tools work (perceive, rule, etc). It's the model of a helicopter that fight fires.
 */

model modelTestBDI

global {
	int displatTextSize <-4;
	init {
		create fireArea number:100;
		create waterArea number:1;
		create helicopter number: 2;
	}
	
	reflex stop when: length(fireArea) = 0 {
		do halt;
	}
}


species helicopter skills: [moving] control: simple_bdi{	
	//Here are the variables used by a helicopter. We define the predicates that will be used later.
	rgb color;
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
	reflex perception {
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
	perceive target:fireArea in: 10{
		focus var:location /*agent:myself*/ priority:11;
		ask myself{
			do remove_intention(patrol_desire, true);
		}
	}
	
	//The rules are used to create a desire from a belief. We can specify the priority of the desire with a statement priority.
	rule belief: new_predicate("location_fireArea") new_desire: get_belief_with_name("location_fireArea");
	rule belief: no_water_predicate new_desire: water_predicate;
	
	//The plan to do when the intention is to patrol.
	plan patrolling intention:patrol_desire finished_when: has_belief(new_predicate("location_fireArea")) or has_belief(no_water_predicate){
		write "patrolling";
		do wander;
	}
	
	//The plan that is executed when the agent got the intention of extinguish a fire.
	plan stopFire intention: new_predicate("location_fireArea") priority:5{
		write "stopFire";
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
    	write "gotoTakeWater";
    	waterArea wa <- first(waterArea);
    	list<grille> voisins <-  (grille(location) neighbors_at (1)) + grille(location);
			path cheminSuivi <- self goto(on: grille,target: wa,return_path:true);
    	if (self distance_to wa <= 1) {
    		waterValue <- waterValue + 2.0;
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
	float size <-10.0;
	
	init {
		grille place <- one_of(grille);
		location <- place.location;
	}
	aspect base {
	  draw circle(size) color: #blue;		
	}
}

grid grille width: 50 height: 50 neighbours:4{

}


experiment RESCUE type: gui {

	output {					
		display view1 { 
			grid grille lines: #black;
			species fireArea aspect:base;
			species waterArea aspect:base;
			species helicopter aspect: bdi;
		}
	}

}