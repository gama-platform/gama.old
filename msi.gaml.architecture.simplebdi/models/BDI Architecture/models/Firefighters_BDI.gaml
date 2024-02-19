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
	
	//We define the predicates that will be used later.
	predicate patrol_desire <- new_predicate("patrol");
	predicate water_predicate <- new_predicate("has water",true);
	predicate no_water_predicate <- new_predicate("has water", false) ;
	string fireLocation <- "fireLocation";
	
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
	
	//Here are the variables used by a helicopter. 
	rgb color <- rnd_color(150);
	float waterValue;
	grille maCellule <- one_of(grille);
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
	
	//This perceive is used to update the beliefs concerning the intern variable of the agent (the amount of water it has).
	perceive target:self {
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
		focus id:"fireLocation" var:location strength:10.0; 
		ask myself{
			do remove_intention(patrol_desire, true);
		} 
	}
	
	//The rules are used to create a desire from a belief. We can specify the priority of the desire with a statement priority.
	rule belief: new_predicate(fireLocation) new_desire: get_predicate(get_belief_with_name(fireLocation));
	rule belief: no_water_predicate new_desire: water_predicate strength: 10.0;
	
	//The plan to do when the intention is to patrol.
	plan patrolling intention:patrol_desire{
		do wander amplitude: 30.0 speed: 2.0;
	}
	 
	//The plan that is executed when the agent got the intention of extinguish a fire.
	plan stopFire intention: new_predicate(fireLocation) priority:5{
		point target_fire <- point(get_predicate(get_current_intention()).values["location_value"] );
		if(waterValue>0){
			if (self distance_to target_fire <= 1) {
				fireArea current_fire <- fireArea first_with (each.location = target_fire);
				if (current_fire != nil) {
					 waterValue <- waterValue - 1.0;
					 current_fire.size <-  current_fire.size - 1;
					 if ( current_fire.size <= 0) {
						ask  current_fire {do die;}
						do remove_belief(get_predicate(get_current_intention()));
						do remove_intention(get_predicate(get_current_intention()), true);
						do add_desire(patrol_desire,1.0);
					}
				} else {
					do remove_belief(get_predicate(get_current_intention()));
					do remove_intention(get_predicate(get_current_intention()), true);
					do add_desire(patrol_desire,1.0);
				}
			} else {
				do goto(target: target_fire);
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
			path cheminSuivi <-  goto(wa);
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
		draw circle(15) color: color wireframe: true;
		draw ("B:" + length(belief_base) + ":" + belief_base) color:#black size:displatTextSize perspective:false; 
		draw ("D:" + length(desire_base) + ":" + desire_base) color:#black size:displatTextSize at:{location.x,location.y+displatTextSize} perspective:false; 
		draw ("I:" + length(intention_base) + ":" + intention_base) color:#black size:displatTextSize at:{location.x,location.y+2*displatTextSize} perspective:false;
		draw ("curIntention:" + get_current_intention()) color:#black size:displatTextSize at:{location.x,location.y+3*displatTextSize} perspective:false; 	
	}
}

species fireArea{
	float size <-1.0;
	
	init{
		grille place <- one_of(grille);
		location <- place.location;
	}
	
	aspect base {
	  draw file("../includes/Fire.png") size: 5;
	}
}

species waterArea{
	init {
		grille place <- one_of(grille);
		location <- place.location;
	}
	aspect base {
	  draw square(5) color: #blue border: #black;		
	}
}

grid grille width: 25 height: 25 neighbors:4 {
	rgb color <- #green;
}


experiment fight_fire type: gui {
	float minimum_cycle_duration <- 0.05;
	output {					
		display view1 { 
			grid grille border: #darkgreen;
			species fireArea aspect:base;
			species waterArea aspect:base;
			species firefighter aspect: bdi;
		}
	}

}