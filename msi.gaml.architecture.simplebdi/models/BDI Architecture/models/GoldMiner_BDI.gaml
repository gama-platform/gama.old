/**
 *  GoldBdi
 *  Author: HPhi
 *  Description: 
 */

model GoldBdi

global {
	int nbgold<-10;
	int nbminer<-10;
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
}

species miner skills: [moving] control:simple_bdi {
	float viewdist<-20.0;
	float speed <- 3.0;
	rgb mycolor<-rnd_color(255);
	predicate define_gold_target <- new_predicate("define_gold_target") with_priority 20;
	predicate get_gold <- new_predicate("get_gold") with_priority 10;
	predicate has_gold <- new_predicate("has_gold");
	predicate wander <- new_predicate("wander");
	predicate return_base <- new_predicate("return_base") with_priority 100;
	point target;
	
	init
	{
		do add_desire(wander);
	}
		
	perceive target:gold in:viewdist {
		focus var:location;
		ask myself {do remove_intention(wander, false);}
	}
	
	rule belief: new_predicate("location_gold") new_desire: get_gold ;
	rule belief: has_gold new_desire: return_base ;
	
		
	plan letsWander intention:wander 
	{
		do wander;
	}
	
	plan getGold intention:get_gold 
	{
		if (target = nil) {
			do add_subintention(get_gold,define_gold_target, true);
			do current_intention_on_hold();
		} else {
			do goto target: target ;
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
	
	plan choose_gold_target intention: define_gold_target instantaneous: true{
		list<point> possible_golds <- get_beliefs("location_gold") collect (point(predicate(each).values["location_value"]));
		if (empty(possible_golds)) {
			do remove_intention(get_gold, true);
		} else {
			target <- (possible_golds with_min_of (each distance_to self)).location;
		}
		do remove_intention(define_gold_target, true);
	}
	
	plan return_to_base intention: return_base {
		do goto target: the_base ;
		if (the_base.location = location)  {
			do remove_belief(has_gold);
			do remove_intention(return_base, true);
			the_base.golds <- the_base.golds + 1;
		}
	}

	aspect default {
	  draw circle(2) color: mycolor;
	  draw circle(viewdist) empty: true color: mycolor;		
	}
}


species gold {
	aspect default
	{
	  draw triangle(5) color: #yellow;	
	}
}

species base {
	int golds;
	aspect default
	{
	  draw square(5) color: #black;
	}
}


experiment GoldBdi type: gui {

	output {
		display map
		{
			species base ;
			species gold ;
			species miner;
			
		}
	}
}
