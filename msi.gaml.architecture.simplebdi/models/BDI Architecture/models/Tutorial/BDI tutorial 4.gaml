/***
* Name: BDItutorial4
* Author: Mathieu Bourgais
* Description: Addition of emotions and personality to miner agents.
* Tags: emotion, personality
***/

model BDItutorial4

global {
	int nb_mines <- 10; 
	int nb_miners <- 5;
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
		create gold_mine number: nb_mines;
		create miner number: nb_miners;
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

species miner skills: [moving] control:simple_bdi {
	
	float view_dist<-1000.0;
	float speed <- 2#km/#h;
	rgb my_color<-rnd_color(255);
	point target;
	int gold_sold;
	
    bool use_social_architecture <- true;
	bool use_emotions_architecture <- true;
	bool use_personality <- true;
	
	init {
		do add_desire(find_gold);
	}
	
	perceive target:miner in:view_dist {
		socialize liking: 1 -  point(my_color.red, my_color.green, my_color.blue) distance_to point(myself.my_color.red, myself.my_color.green, myself.my_color.blue) / ( 255);
	}
		
	perceive target: gold_mine where (each.quantity > 0) in: view_dist {
		focus id: mine_at_location var: location;
		ask myself {
			if (has_emotion(joy)) {
				write self.name + " is joyous";
				do add_desire(predicate:share_information, strength: 5.0);
			}
			do remove_intention(find_gold, false);
		}
	}
	
	rule belief: mine_location new_desire: has_gold strength: 2.0;
	rule belief: has_gold new_desire: sell_gold strength: 3.0;
	
	plan lets_wander intention:find_gold finished_when: has_desire(has_gold){
		do wander;
	}
	
	plan get_gold intention:has_gold  {
		if (target = nil) {
			do add_subintention(get_current_intention(),choose_gold_mine, true);
			do current_intention_on_hold();
		} else {
			do goto target: target ;
			if (target = location)  {
				gold_mine current_mine<- gold_mine first_with (target = each.location);
				if current_mine.quantity > 0 {
				 	do add_belief(has_gold);
					ask current_mine {quantity <- quantity - 1;}	
				} else {
					do add_belief(new_predicate(empty_mine_location, ["location_value"::target]));
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
	
	plan return_to_base intention: sell_gold {
		do goto target: the_market ;
		if (the_market.location = location)  {
			do remove_belief(has_gold);
			do remove_intention(sell_gold, true);
			gold_sold <- gold_sold + 1;
		}
	}
	plan share_information_to_friends intention: share_information instantaneous: true{
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
		display map type: 3d
		{
			species market ;
			species gold_mine ;
			species miner;
		}
		
		display socialLinks type: 3d{
        species socialLinkRepresentation aspect: base;
    }

		display chart type: 2d {
			chart "Money" type: series {
				datalist legend: miner accumulate each.name value: miner accumulate each.gold_sold color: miner accumulate each.my_color;
			}
		}
		
	}
}

