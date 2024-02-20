/**
* Name: vote
* Author: MAPS TEAM (Frederic Amblard, Thomas Louail, Romain Reulier, Paul Salze et Patrick Taillandier) 
* Description: Modeling of an election
* Tags: gui
*/
 
model vote

global {
	//Shape of the environment
	geometry shape <- rectangle({200, 200});
	
	//Number of electors
	int nb_electors <- 1500;
	//Number of candidates
	int nb_candidates <- 7;
	//Weight of each candidates
	int weight_candidates <- 50;
	//Threshold for the attraction candidates
	int threshold_attraction_candidates <- 80;
	//Threshold for the repulsion candidates
	int threshold_repulsion_candidates <- 200;
	//Threshold for the attraction electors
	int threshold_attraction_electors <- 20;
	
	//Distance traveled
	float distance_traveled <- 7.0;
	//Distribution of the electors
	string distribution_electors <- "Uniform" among: ["Uniform", "Normal"];
	//Distribution of candidates
	string distribution_candidates <- "Polygon" among: ["Random", "Polygon", "Line", "Diagonal"];
	//Strategy of the candidates
	string strategy_candidates <- "No strategy" among: ["No strategy", "Search electors", "Distinction", "Group", "Go closer to the best","Random" ];
	//Count of max group
	int cpt_Group_max <- 5;
	//Count  of group
	int cpt_Group <- cpt_Group_max;
	
	float entropy;
	
	//List of all the active candidates
	list<candidate> active_candidates ;
	
	init {
		//Creation of the elector
		create elector number: nb_electors;
		do creation_candidates;
	}
	//Action to create the candidates according to the distribution of candidates
	action creation_candidates {
		switch distribution_candidates { 
			match "Polygon" {
				list<point> liste_points <- list(nb_candidates points_at 50.0);
				int cpt <- 0;
				create candidate number: nb_candidates{
					 color <- rgb (rnd(255), rnd(255), rnd(255)); 
					 location <- liste_points at cpt;
					 cpt <- cpt + 1; 
				}
			}
			match "Line" {
				int cpt  <- 0;
				create candidate number: nb_candidates{
					color <- rgb ([rnd(255), rnd(255), rnd(255)]); 
					float x_cord  <- 200 * cpt / nb_candidates;
					float y_cord <- 100.0;
					location <- {x_cord, y_cord};
					cpt <- cpt + 1;
				}
			}
			match "Diagonal" {
				int cpt <- 0;
				create candidate number: nb_candidates{
					color <- rgb ([rnd(255), rnd(255), rnd(255)]); 
					float x_cord <- 200 * cpt / nb_candidates;
					float y_cord <- x_cord;
					location <- {x_cord, y_cord};
					cpt <- cpt + 1;
				}
			}
			match "Random" {
				int cpt <- 0;
				create candidate number: nb_candidates{
					color <- rgb ([rnd(255), rnd(255), rnd(255)]); 
					location <- any_location_in(world);
				}
			}
		}
		//Initialization of all the active candidates as the list of candidates
		active_candidates <- list(copy(candidate));	
	}
	//Reflex representing the dynamics of the models
	reflex dynamique {
		//For each elector, ask to move
		ask elector {
			do moving;
		}
		//For each candidate, ask to move
		ask active_candidates{
			do moving;
			my_electors <- list<elector>([]);
		}
		//For each elector, do its definition
		ask elector {
			do definition_candidate;
		}
		int nb_electors_max <- 0;
		candidate candidat_elected <- nil; 
		
		//Ask to all the active candidates to compute their percentage of vote and set the number of maximum electors to know which candidate is elected
		ask active_candidates{ 
			int nb_el <- length(my_electors) ;
			percentage_vote <- (nb_el/nb_electors * 100) with_precision 2;
			if (nb_el > nb_electors_max) {
				nb_electors_max <- nb_el;
			 	candidat_elected <- self;
			}
		}
		//update of the state of the candidate
		ask candidate {
			is_elected <- false; 
		}
		ask candidat_elected {
			is_elected <- true; 
		}
	}
	//Reflex to show the final results
	reflex resultats_finaux when: time = 72 {
		candidate elected <- active_candidates with_max_of (each.percentage_vote);
		//Display a window telling who is the winner and halt the model
		do tell msg: "The winner is " + elected.name; 
		do pause;
	}
	
	//Reflex to compute the creation of group when one candidate chooses this strategy
	reflex creation_Group when: (strategy_candidates in ["Group", "Random"]) {
		 if (cpt_Group = cpt_Group_max) {
		 	//Kill all the group of electors
		 	ask Group_electors as list {
		 		do die;
		 	}
		 	//Compute the list of elector according to their distance
			list<list<elector>> Groups;
			geometry geoms <- union(elector collect (each.shape + (threshold_attraction_electors, 4, #round)));
					loop geom over: geoms.geometries { 
				if (geom != nil and !empty(geom.points)) {
					list<elector> els  <- (elector inside geom); 
					add els to: Groups;
				}
			}
			//Create new groups of electors according to the list of electors
			loop gp over: Groups {
			 	create Group_electors {
					 effectif <- length(gp);
			 		 electors_dans_Group <- gp;
			 		 location <- mean(electors_dans_Group collect (each.location)) ;
			 	}
			 }	 
		}
		cpt_Group <- cpt_Group - 1;
		if (cpt_Group = 0) { cpt_Group <- cpt_Group_max;}	
	}
	//Reflex to compute the entropy
	reflex calcule_entropy {
		entropy <- 0.0;
		//Compute the abstinence rate
		float abst <- (nb_electors - sum (active_candidates  collect (length(each.my_electors)))) / nb_electors;
		if (abst > 0) {
			entropy <- entropy - (abst * ln(abst));
		}
		//Ask to all the active candidates their number of electors to compute the entropy
		ask active_candidates {
			float p <- length(my_electors) / nb_electors;
			if (p > 0) {
				entropy <- entropy - (p * ln(p));
			}
		}
		entropy <- entropy / ln (length(active_candidates) + 1);
	}
}



//Species representing a group of electors
species Group_electors {
	int effectif <- 0;
	//List of all the elector agents in the group
	list<elector> electors_dans_Group ;
	aspect default {
		draw square(2) color: #orange;
	} 
	
}
//Species representing the elector moving 
species elector skills: [moving]{
	
	init {
		//At initialization, place the elector in a certain place according to the distribution of electors
		if (distribution_electors = "Normal") {
			float x_cord <- max([0.0, min([200.0, gauss ({100, 35})])]);
			float y_cord <- max([0.0, min([200.0, gauss ({100, 35})])]);
			location <- {x_cord, y_cord};
		}
	}
	rgb color <- #white;
	//Candidate chosen by the elector
	candidate my_candidate;  
	
	aspect base {
		draw pyramid(2) color: color ;
	} 
	//Action to define the candidate
	action definition_candidate {
		//The candidate chosen is the one closest to the elector in the attraction range
		my_candidate <- active_candidates with_min_of (self distance_to each);
		my_candidate <- (self distance_to my_candidate < threshold_attraction_candidates) ? my_candidate : nil;
		if (my_candidate != nil) {
			add self to: my_candidate.my_electors; 
			color <- my_candidate.color;
		}
	}
	//Action to move the elector
	action moving {
		//Make the agent move closer to another elector, representing the influence of this one
		if ( rnd(100) > (weight_candidates)) {
			elector my_elector <- shuffle(elector) first_with ((self distance_to each) < threshold_attraction_electors);
			if (my_elector != nil) {
				do goto target:my_elector speed: distance_traveled;
			} 
		} else {
			//Move the elector closer to one of the candidate to represent its repulsion or attraction
			candidate the_candidate <- one_of(candidate) ;
			if (the_candidate != nil) {
				float dist <- self distance_to the_candidate;
				if dist < threshold_attraction_candidates {
					do goto target: the_candidate speed: distance_traveled;
				} else if dist > threshold_repulsion_candidates {
					do goto target: location + location - the_candidate.location speed: distance_traveled;
				}
			}
		}
		
	} 
	
}
//Species candidate using the skill moving
species candidate skills:[moving]{
	rgb color <- rgb([100 + rnd(155),100 + rnd(155),100 + rnd(155)]);
	//Boolean to know if the candidate is active
	bool active <- true;
	//Float representing the percentage of vote for the candidate
	float percentage_vote; 
	//List of all the electors of the candidate
	list my_electors of: elector;
	//Boolean to know if the candidate is elected
	bool is_elected <- false;
	aspect default {
		draw sphere(3) color: color;
	} 
	aspect dynamic {
		if (active) {
			float radius  <- 1 + (percentage_vote / 4.0);
			if (is_elected) {
				draw cube( radius * 2) color: color.brighter.brighter; 
				
			} 
				draw sphere(radius) color: color;
			
			draw string(percentage_vote) size: 5 color: #white anchor: #center;
		}
	}
	//Action to move the candidate according to its strategy
	action moving {
		switch strategy_candidates {
			match "No strategy" {}
			match "Search electors" {do strategy_1;}
			match "Distinction" {do strategy_2;}
			match "Group" {do strategy_3;}
			match "Go closer to the best" {do strategy_4;}
			match "Random" { 
				switch (rnd(4)) {
					match 0 {}
					match 1 {do strategy_1;}	
					match 2 {do strategy_2;}	
					match 3 {do strategy_3;}	
					match 4 {do strategy_4;}		
				}
			}
		}
	}
	
	action strategy_1 {
		//go closer to electors
		elector my_elector <- shuffle(elector) first_with ((self distance_to each) < threshold_attraction_electors);
		if (my_elector != nil) {
			do goto target:my_elector speed: distance_traveled;
		} 
	}
	
	action strategy_2 {
		//go in opposite directions to other candidates
		list<candidate> cands <- list(copy(candidate));
		remove self from: cands;
		candidate the_candidate <- one_of(cands) ;
		if (the_candidate != nil) {
			do goto target: (location + location - the_candidate.location) speed: distance_traveled;	
		}
	}
	
	action strategy_3 {
		//go closer to a group of electors
		Group_electors mon_Group  <- (Group_electors where ((self distance_to each) < threshold_attraction_electors)) with_max_of (each.effectif);
		if (mon_Group != nil) {
			do goto target:mon_Group speed: distance_traveled;
		} 
	}
	
	action strategy_4 {
		//go toward the candidate with max of votes
		candidate the_candidate <- candidate with_max_of (percentage_vote) ;
		if (the_candidate != nil) {
			do goto target:the_candidate speed: distance_traveled;	
		}
	}
	
}


experiment vote type: gui {
	/** Insert here the definition of the input and output of the model */
	parameter "Number of electors : " var: nb_electors category: "elector";
	parameter "Moving speed of electors toward another electors : " var: distance_traveled category: "elector";
	parameter "Attraction distance between electors : " var: threshold_attraction_electors category: "elector";
	parameter "Number of candidates : " var: nb_candidates category: "Candidate";
	parameter "Attraction distance between candidates and electors : " var: threshold_attraction_candidates category: "elector";
	
	parameter "Repulsion distance between candidates : " var: threshold_repulsion_candidates category: "elector";
	parameter "weight of candidates : " var: weight_candidates category: "Candidate";
	
	parameter "Distribution type of electors : " var: distribution_electors category: "elector";
	parameter "Distribution type of  candidates : " var: distribution_candidates category: "Candidate";
	parameter "Strategy of candidates : " var: strategy_candidates category: "Candidate";
	
	output {
		
		layout #split;
		display "Main" background: #black { 
			species elector aspect: base;
			species candidate aspect: dynamic;
			species Group_electors;
			
		}
		display "Voters"  type: 2d {
			chart "Distribution of electors" type: pie background: #white  {
			 	loop cand over: candidate {
			 		data  cand.name value:cand.percentage_vote color: cand.color ;
			 	}	
			}
			
			}
		display "Indicators"  type: 2d {
			chart "Shannon Entropy" type: series background: #white size: {1,0.5} position: {0, 0} {
				data "entropy" value: entropy color: #blue ;
			}
			chart "Opinion distribution" type: series background: #white size: {1,0.5} position: {0, 0.5} {
				data "Space area covered" value: (union(candidate collect (each.shape buffer threshold_attraction_candidates))).area / 40000 color: #blue ;
			}
		}
	}
}
