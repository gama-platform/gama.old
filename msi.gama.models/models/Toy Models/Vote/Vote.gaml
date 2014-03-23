/**
 *  vote
 *  Author: MAPS TEAM (Frederic Amblard, Thomas Louail, Romain Reulier, Paul Salze et Patrick Taillandier)
 *  Description: Modeling of an election
 */

model vote

global {
	geometry shape <- rectangle({200, 200});
	
	int nb_electors <- 1500;
	int nb_candidates <- 7;
	int weight_candidates <- 50;
	int threshold_attraction_candidates <- 80;
	int threshold_repulsion_candidates <- 200;
	int threshold_attraction_electors <- 20;
	
	float distance_traveled <- 7.0;
	string distribution_electors <- "Uniform" among: ["Uniform", "Normal"];
	string distribution_candidates <- "Polygon" among: ["Random", "Polygon", "Line", "Diagonal"];
	string strategy_candidates <- "No strategy" among: ["No strategy", "Search electors", "Distinction", "Group", "Go closer to the best","Random" ];
	int cpt_Group_max <- 5;
	int cpt_Group <- cpt_Group_max;
	float entropy;
	list<candidate> active_candidates ;
	
	init {
		create elector number: nb_electors;
		do creation_candidates;
	}
	
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
		}
		active_candidates <- list(copy(candidate));	
	}
	
	reflex dynamique {
		ask elector {
			do moving;
		}
		ask active_candidates{
			do moving;
			my_electors <- [];
		}
		ask elector {
			do definition_candidate;
		}
		int nb_electors_max <- 0;
		candidate candidat_elected <- nil; 
		ask active_candidates{ 
			int nb_el <- length(my_electors) ;
			percentage_vote <- (nb_el/nb_electors * 100) with_precision 2;
			if (nb_el > nb_electors_max) {
				nb_electors_max <- nb_el;
			 	candidat_elected <- self;
			}
		}
		ask candidate {
			is_elected <- false; 
		}
		ask candidat_elected {
			is_elected <- true; 
		}
	}
	
	reflex resultats_finaux when: time = 72 {
		candidate elected <- active_candidates with_max_of (each.percentage_vote);
		do tell message: "The winner is " + elected.name; 
		do halt;
	}
	
	reflex creation_Group when: (strategy_candidates in ["Group", "Random"]) {
		 if (cpt_Group = cpt_Group_max) {
		 	ask Group_electors as list {
		 		do die;
		 	}
			list<list<elector>> Groups<- [];
			geometry geoms <- union(elector collect ((each.shape) buffer (["distance"::float(threshold_attraction_electors) , "quadrantSegments"::4, "endCapStyle"::1])));
			loop geom over: geoms.geometries { 
				if (geom != nil and !empty(geom.points)) {
					list<elector> els  <- (elector inside geom); 
					add els to: Groups;
				}
			}
			
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
	
	reflex calcule_entropy {
		entropy <- 0.0;
		float abst <- (nb_electors - sum (active_candidates  collect (length(each.my_electors)))) / nb_electors;
		if (abst > 0) {
			entropy <- entropy - (abst * ln(abst));
		}
		ask active_candidates {
			float p <- length(my_electors) / nb_electors;
			if (p > 0) {
				entropy <- entropy - (p * ln(p));
			}
		}
		entropy <- entropy / ln (length(active_candidates) + 1);
	}
}


entities {
	
	species Group_electors {
		int effectif <- 0;
		list<elector> electors_dans_Group ;
		aspect default {
			draw square(2) color: rgb("orange");
		} 
		
	}
	
	species elector skills: [moving]{
		
		init {
			if (distribution_electors = "Normal") {
				float x_cord <- max([0.0, min([200.0, gauss ({100, 35})])]);
				float y_cord <- max([0.0, min([200.0, gauss ({100, 35})])]);
				location <- {x_cord, y_cord};
			}
		}
		rgb color <- rgb('white');
		candidate my_candidate;  
		
		aspect base {
			draw triangle(2) color: color ;
		} 
		action definition_candidate {
			my_candidate <- active_candidates with_min_of (self distance_to each);
			my_candidate <- (self distance_to my_candidate < threshold_attraction_candidates) ? my_candidate : nil;
			if (my_candidate != nil) {
				add self to: my_candidate.my_electors; 
				color <- my_candidate.color;
			}
		}
		action moving {
			if ( rnd(100) > (weight_candidates)) {
				elector my_elector <- shuffle(elector) first_with ((self distance_to each) < threshold_attraction_electors);
				if (my_elector != nil) {
					do goto target:my_elector speed: distance_traveled;
				} 
			} else {
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
	
	species candidate skills:[moving]{
		rgb color <- rgb([100 + rnd(155),100 + rnd(155),100 + rnd(155)]);
		bool active <- true;
		float percentage_vote; 
		list my_electors of: elector;
		bool is_elected <- false;
		aspect default {
			draw circle(3) color: color;
		} 
		aspect dynamic {
			if (active) {
				float radius  <- 1 + (percentage_vote / 4.0);
				if (is_elected) {
					draw square( radius *1.5) color: rgb("red"); 
					draw circle(radius) color: color;
				} else {
					draw circle(radius) color: color;
				}
				draw string(percentage_vote) size: 5 color: rgb("white");
			}
		}
		
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
		display main background: rgb("black") { 
			species elector aspect: base;
			species candidate aspect: dynamic;
			species Group_electors;
			
		}
		display votants {
			chart "Distribution of electors" type: pie background: rgb('white')  {
			 	loop cand over: candidate {
			 		data legend: cand.name value:cand.percentage_vote color: cand.color ;
			 	}	
			}
			
			}
		display indicateurs {
			chart "Shannon Entropy" type: series background: rgb('white') size: {1,0.5} position: {0, 0} {
				data "entropy" value: entropy color: rgb('blue') ;
			}
			chart "Opinion distribution" type: series background: rgb('white') size: {1,0.5} position: {0, 0.5} {
				data "Space area covered" value: (union(candidate collect (each.shape buffer threshold_attraction_candidates))).area / 40000 color: rgb('blue') ;
			}
		}
	}
}
