/**
 *  GoldBdi
 *  Author: Philippe Caillou, Mathieu Bourgais, Patrick Taillandier
 *  Description: A simple model that uses the simple_bdi architecture. In this model, the Miner agent has a general desire to  find gold. 
 * As it is the only thing it wants at the beginning, it is its initial intention (what it is currently doing). To  find gold, it wanders around (its plan is to wander). 
 * When it perceives some gold nuggets, it stores this information (it has a new belief about the existence and location of this gold nugget), 
 * and it adopts a new desire (it wants to extract the gold). When it perceives a gold nugget, the intention to  find gold is put on hold and a new intention is selected (to extract gold).
 * To achieve this intention, the plan has two steps, i.e. two new (sub)intentions: to choose a gold nugget to extract (among its known gold nuggets) and to go and take it. And so on.
 *  Tags: simple_bdi, perception, rule, plan, predicate
 */

model GoldBdi

global {
	int nbgold<-20;
	int nbminer<-3;
	base the_base;
	geometry shape <- square(200);
	init
	{
		create base {
			the_base <- self;
		}
		create gold number:nbgold;
		create miner number:nbminer;
	}
	
	reflex stop when:length(gold)=0{
		do pause;
	}
}

//give the simple_bdi architecture to the miner agents
species miner skills: [moving] control:simple_bdi {
	//definition of the agent attributes
	float viewdist<-20.0;
	float speed <- 3.0;
	rgb mycolor<-rnd_color(255);
	
	//to simplify the writting of the agent behavior, we define as variables 4 desires for the agents
	predicate define_gold_target <- new_predicate("define_gold_target");
	predicate get_gold <- new_predicate("get_gold");
	predicate wander <- new_predicate("wander");
	predicate return_base <- new_predicate("return_base");
	
	predicate sac <- new_predicate("sac",["contenance"::5]);
	
	//we define in the same way a belief that I have already gold that I have to return to the base
	predicate has_gold <- new_predicate("has_gold");
	
	point target;
	
	//at the vreation of the agent, we add the desire to patrol (wander)
	init
	{
		agent friend <- nil;
		
		do add_social_link(new_social_link(friend));
		
		emotion joie <- new_emotion("joie",wander);
		do add_emotion(joie);
		
		do add_desire(wander);
	}
	
	//if the agent perceive a gold nugget in its neighborhood, it adds a belief a belief concening its location and remove its wandering intention
	perceive target:gold in:viewdist {
		focus id:"location_gold" var:location;
		ask myself {do remove_intention(wander, false);}
	}
	
	
		perceive target:miner in: /*2**/viewdist{
		enforcement law:"working" sanction:"sanctionToLaw";
		enforcement obligation:has_gold sanction: "sanctionToObligation" reward:"rewardToObligation";
		enforcement norm:"share_information" reward:"rewardToNorm";
	}
	
	//if the agent has the belief that their is gold at given location, it adds the desire to get gold 
	rule belief: new_predicate("location_gold") new_desire: get_gold strength:10.0;
//	rule belief: new_predicate("location_gold") new_desire: new_predicate("toto") all: true strength:0.1;
	
	//if the agent has the belief that it has gold, it adds the desire to return to the base
	rule belief: has_gold new_desire: return_base strength:100;
	
	
	// plan that has for goal to fulfill the wander desire	
	plan letsWander intention:wander 
	{
		do wander amplitude: 60.0;
	}
	
	//plan that has for goal to fulfill the get gold desire
	plan getGold intention:get_gold 
	{
		//if the agent does not have chosen a target location, it adds the sub-intention to define a target and puts its current intention on hold
		if (target = nil) {
			do add_subintention(get_current_intention(),define_gold_target, true);
			do current_intention_on_hold();
		} else {
			do goto target: target ;
			
			//if the agent reach its location, it updates it takes the gold, updates its belief base, and remove its intention to get gold
			if (target = location)  {
				gold current_gold <- gold first_with (target = each.location);
				if current_gold != nil {
				 	do add_belief(has_gold);
					ask current_gold {do die;}	
				}
				do remove_belief(new_predicate("location_gold", ["location_value"::target]));
				target <- nil;
				do remove_intention(get_gold, true);
			}
		}	
	}
	
	//plan that has for goal to fulfill the define gold target desire. This plan is instantaneous (does not take a complete simulation step to apply).
	plan choose_gold_target intention: define_gold_target instantaneous: true{
		list<point> possible_golds <- get_beliefs(new_predicate("location_gold")) collect (point(get_predicate(mental_state (each)).values["location_value"]));
		if (empty(possible_golds)) {
			do remove_intention(get_gold, true);
		} else {
			target <- (possible_golds with_min_of (each distance_to self)).location;
		}
		do remove_intention(define_gold_target, true);
	}
	
	////plan that has for goal to fulfill the return to base desire
	plan return_to_base intention: return_base {
		do goto target: the_base ;
		if (the_base.location = location)  {
			do remove_belief(has_gold);
			do remove_intention(return_base, true);
			the_base.golds <- the_base.golds + 1;
		}
	}

	aspect default {
	  draw circle(2) color: mycolor border:mycolor-50;
	  draw circle(viewdist) color:rgb(mycolor,0.5);		
	}
}


species gold {
	aspect default
	{
	  draw triangle(5) color:rgb(#yellow,0.8) border:#yellow;	
	}
}

species base {
	int golds;
	aspect default
	{
	  draw square(20) color: #black;
	}
}


experiment GoldBdi type: gui {
	float minimum_cycle_duration <- 0.05;
	output {
		display map background:#gamablue type:3d
		{
			species base ;
			species miner aspect:default;
			species gold ;
		}
	}
}
