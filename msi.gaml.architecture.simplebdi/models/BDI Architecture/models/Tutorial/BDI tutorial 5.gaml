/***
* Name: BDItutorial5
* Author: Mathieu Bourgais
* Description: Addition of norms, obligation, and enforcement
* Tags: norm, obligation, enforcement
***/

model BDItutorial5

global {
	int nb_mines <- 10; 
	int nb_miners <- 5;
	int nb_police <- 1;
	int fine <- 0;
	market the_market;
	
	string mine_at_location <- "mine_at_location";
	string empty_mine_location <- "empty_mine_location";
	
	float step <- 10#mn;
	
	//possible predicates concerning miners
	predicate mine_location <- new_predicate(mine_at_location) ;
	predicate choose_gold_mine <- new_predicate("choose a gold mine");
	predicate has_gold <- new_predicate("extract gold");
	predicate find_gold <- new_predicate("find gold") ;
	predicate sell_gold <- new_predicate("sell gold") ;
	predicate share_information <- new_predicate("share information") ;
	
	
	emotion joy <- new_emotion("joy");
	
	float inequality <- 0.0 update:standard_deviation(miner collect each.gold_sold);
	
	geometry shape <- square(20 #km);
	init
	{
		create market {
			the_market <- self;	
		}
		create gold_mine number:nb_mines;
		create miner number:nb_miners;
		create policeman number:nb_police;
	}
	
	reflex display_social_links{
		loop tempMiner over: miner{
				loop tempDestination over: tempMiner.social_link_base{
					if (tempDestination !=nil){
						bool exists<-false;
						loop tempLink over: socialLinkRepresentation{
							if((tempLink.origin=tempMiner) and (tempLink.destination=tempDestination.agent)){
								exists<-true;
							}
						}
						if(not exists){
							create socialLinkRepresentation number: 1{
								origin <- tempMiner;
								destination <- tempDestination.agent;
								if(get_liking(tempDestination)>0){
									my_color <- #green;
								} else {
									my_color <- #red;
								}
							}
						}
					}
				}
			}
	}
	
	reflex end_simulation when: sum(gold_mine collect each.quantity) = 0 and empty(miner where each.has_belief(has_gold)){
		do pause;
		ask miner {
			write name + " : " +gold_sold;
		}
		write "**********************";
		write "fine : " + fine;
	}
}

species gold_mine {
    int quantity <- rnd(1,20);
    aspect default {
        draw triangle(200 + quantity * 50) color: (quantity > 0) ? #yellow : #gray border: #black;    
    }
}

species market {
	int golds;
	aspect default {
	  draw square(1000) color: #black ;
	}
}

species policeman skills: [moving] control:simple_bdi {
	predicate patroling <- new_predicate("patrolling");
	float view_dist <- 1000.0;
	miner agent_perceived <- nil;
	
	init {
		do add_desire(patroling);
	}
	
	perceive target: miner in: view_dist {
		myself.agent_perceived <-self;
		enforcement law:"working" sanction:"sanction_to_law";
		enforcement obligation:has_gold /*when:has_belief(has_gold)*/ sanction: "sanction_to_obligation" reward:"reward_to_obligation";
	}
	
	sanction sanction_to_law{
		ask agent_perceived{
			threshold_law <- 0.0;
			gold_sold <- gold_sold-5;
		}
		fine <- fine +5;
	}
	
	sanction sanction_to_obligation {
		ask agent_perceived{
			gold_sold <- gold_sold-3;
			do remove_intention(sell_gold,true);
			threshold_obligation <- self.threshold_obligation - 0.1;
		}
		fine <- fine + 3;
	}
	
	sanction reward_to_obligation{
		ask agent_perceived{
			gold_sold <- gold_sold+2;
		}
		fine <- fine -2;
	}
	
	plan patrol intention: patroling {
		do wander;
	}
	
	aspect base {
		draw circle(view_dist) color: #blue depth: 0.0;
	}
}

species miner skills: [moving] control:simple_bdi {
	
	float view_dist<-1000.0;
	float speed <- 2#km/#h;
	rgb my_color<-rnd_color(255);
	point target;
	int gold_sold;
	int gold_transported<-0;
	agent agent_perceived<-nil;
	
    bool use_social_architecture <- true;
	bool use_emotions_architecture <- true;
	bool use_personality <- true;
	
	float openness <- gauss(0.5,0.12);
	float conscientiousness <- gauss(0.5,0.12);
	float extraversion <- gauss(0.5,0.12);
	float agreeableness <- gauss(0.5,0.12);
	float neurotism <- gauss(0.5,0.12);
	
	float plan_persistence <- 1.0;
	float intention_persistence <- 1.0;
	
	float threshold_law <- 1.0;
	float threshold_obligation <- 1.0;
	float threshold_norm <- 0.5;
	
	init {
		do add_desire(find_gold);
	}
	
	perceive target: self {
		if(gold_transported>0) {
			do add_belief(has_gold);
		} else {
			do remove_belief(has_gold);
		}
	}
	
	perceive target: miner in: view_dist {
		myself.agent_perceived <- self;
		socialize liking: point(my_color.red, my_color.green, my_color.blue) distance_to point(myself.my_color.red, myself.my_color.green, myself.my_color.blue) / ( 255) - 1;
		enforcement norm:"share_information" sanction:"sanction_to_norm" reward:"reward_to_norm";
	}
		
	sanction sanction_to_norm {
		do change_liking(agent_perceived,-0.1);
	}	
	
	sanction reward_to_norm {
		do change_liking(agent_perceived,0.1);
	}
		
	perceive target: gold_mine where (each.quantity > 0) in: view_dist {
		focus id:mine_at_location var:location;
		ask myself {
			if (has_emotion(joy)) {
				write self.name + " is joyous";
				do add_desire(predicate:share_information, strength: 5.0);
			}
			do remove_intention(find_gold, false);
		}
	}
	
	rule belief: has_gold new_desire: sell_gold strength: 3.0;
	
	law working belief: mine_location new_obligation: has_gold when:not has_obligation(has_gold) and not has_belief(has_gold) strength: 2.0 threshold:threshold_law;
	
	plan lets_wander intention:find_gold finished_when: has_desire(has_gold)
	{
		do wander;
	}
	
	norm doing_job obligation:has_gold finished_when: has_belief(has_gold) threshold:threshold_obligation{
		if (target = nil) {
			do add_subintention(get_current_intention(),choose_gold_mine, true);
			do current_intention_on_hold();
		} else {
			do goto target: target ;
			if (target = location)  {
				gold_mine current_mine<- gold_mine first_with (target = each.location);
				if current_mine.quantity > 0 {
					gold_transported <- gold_transported+1;
				 	do add_belief(has_gold);
					ask current_mine {quantity <- quantity - 1;}	
				} else {
					do add_belief(new_predicate(empty_mine_location, ["location_value"::target]));
					do remove_belief(new_predicate(mine_at_location, ["location_value"::target]));
				}
				target <- nil;
			}
		}	
	}
	
	plan get_more_gold intention: has_gold {
		if (target = nil) {
			do add_subintention(get_current_intention(),choose_gold_mine, true);
			do current_intention_on_hold();
		} else {
			do goto target: target ;
			if (target = location)  {
				gold_mine current_mine<- gold_mine first_with (target = each.location);
				if current_mine.quantity > 0 {
					gold_transported <- 3;
				 	do add_belief(has_gold);
					ask current_mine {if(quantity>=3) {
						quantity <- quantity - 3;
					}else {
						quantity <- 0;
					} 
					}	
				} else {
					do add_belief(new_predicate(empty_mine_location, ["location_value"::target]));
					do remove_belief(new_predicate(mine_at_location, ["location_value"::target]));
				}
				target <- nil;
			}
		}	
	}
	
	plan choose_closest_gold_mine intention: choose_gold_mine instantaneous: true{
		list<point> possible_mines <- get_beliefs_with_name(mine_at_location) collect (point(get_predicate(mental_state (each)).values["location_value"]));
		list<point> empty_mines <- get_beliefs_with_name(empty_mine_location) collect (point(get_predicate(mental_state (each)).values["location_value"]));
		possible_mines <- possible_mines - empty_mines;
		if (empty(possible_mines)) {
			do remove_intention(has_gold, true); 
		} else {
			target <- (possible_mines with_min_of (each distance_to self)).location;
		}
		do remove_intention(choose_gold_mine, true); 
	}
	
	plan return_to_base intention: sell_gold when: has_belief(has_gold){
		do goto target: the_market ;
		if (the_market.location = location)  {
			do remove_belief(has_gold);
			do remove_intention(sell_gold, true);
			gold_sold <- gold_sold + gold_transported;
			gold_transported <- 0;
		}
	}
	
	norm share_information intention:share_information threshold:threshold_norm instantaneous: true{
		list<miner> my_friends <- list<miner>((social_link_base where (each.liking > 0)) collect each.agent);
		loop known_gold_mine over: get_beliefs_with_name(mine_at_location) {
			ask my_friends {
				do add_directly_belief(known_gold_mine);
			}
		}
		loop known_empty_gold_mine over: get_beliefs_with_name(empty_mine_location) {
			ask my_friends {
				do add_directly_belief(known_empty_gold_mine);
			}
		}
		
		do remove_intention(share_information, true); 
	}
	
	plan share_information_to_friends intention: share_information instantaneous: true{
		list<miner> my_friends <- list<miner>((social_link_base where (each.liking > 0)) collect each.agent);
		loop known_gold_mine over: get_beliefs_with_name(empty_mine_location) {
			ask my_friends {
				do add_directly_belief(known_gold_mine);
			}
		}		
		do remove_intention(share_information, true); 
	}

	aspect default {
	  draw circle(200) color: my_color border: #black depth: gold_sold;
	  draw circle(view_dist) color: my_color border: #black depth: gold_sold wireframe: true;
	}
}

species socialLinkRepresentation{
	miner origin;
	agent destination;
	rgb my_color;
	
	aspect base{
		draw line([origin,destination],50.0) color: my_color;
	}
}

experiment GoldBdi type: gui {
	output {
		display map type: 3d {
			species market ;
			species gold_mine ;
			species miner;
			species policeman aspect:base;
		}	
		
        display socialLinks type: 3d{
        	species socialLinkRepresentation aspect: base;
    	}

		display chart type: 2d {
			chart "Money" type: series  {
				datalist legend: miner accumulate each.name value: miner accumulate each.gold_sold color: miner accumulate each.my_color;
				data "policeman" value: fine color: #red;
			}
		}
		
	}
}

