/**
 *  test
 *  Author: mathieu
 *  Description: simple model to test the intention facet and the and/or operators
 */

model test

global{
	
	init{
		create but number:1;
		create sortie number:1;
//		create agentTest number:100;
		create agentTest2 number:100;		
		
	}
	
//	reflex a{
//		write duration;
//	}
	
}

species agentTest skills:[moving] control: simple_bdi{
	
	bool probabilistic_choice <- true;
	float plan_persistence <- 1.0;
	float intention_persistence <- 1.0;
	
	
	bool fini;
	but monBut <- first(but);
	sortie maSortie <-first(sortie);
	predicate position <- new_predicate("position");
	predicate butAtteint <- new_predicate("but");
	predicate sortieAtteint <- new_predicate("sortie");
	geometry carre; /* <- circle(10)*/ /*update: carre.location <- self.location*//* ;*/
	
	reflex perception{
		carre <- square(10);
//		if(self.location = monBut.location){
//			do add_belief(butAtteint);
//		}
//		if(self.location = maSortie.location){
//			do add_belief(butAtteint);
//		}
	}
	
	perceive name:a target: but in:carre /*when:false*/{
//		write "but en vue";
		ask myself{
//			do add_belief(new_predicate("new",["location"::myself.location]));
			if (myself.location=location){
				do add_belief(butAtteint);
			}
		}
//		write myself.belief_base;
//		write myself.location;
	}
	
	perceive b target: sortie in:carre /*when:false*/{
//		write "sortie en vue";
		ask myself{
//			do add_belief(new_predicate("new",["location"::myself.location]));
			if (myself.location=location){
				do add_belief(butAtteint);
			}
		}
//		write myself.belief_base;
//		write myself.location;
	}
	
	init{
		fini<-false;
		self.location <- one_of(grille).location;
		do add_desire(butAtteint);
	}
	
	plan bouger intention: butAtteint /*finished_when: fini=true*/ priority: 2{
		do goto on: grille target:monBut speed:2;
	}
	
	plan bouger2 intention: butAtteint /*finished_when: fini=true*/ priority: 2{
		do goto on: grille target:maSortie speed:2;
	}
	
	aspect base{
		draw circle(1) color:#blue;
//		draw carre;
	}
}

species agentTest2 skills:[moving] control: simple_bdi{
	
	bool probabilistic_choice <- true;
	float plan_persistence <- 1.0;
	float intention_persistence <- 1.0;
	
	
	bool fini;
	but monBut <- first(but);
	sortie maSortie <-first(sortie);
	predicate position <- new_predicate("position");
	predicate butAtteint <- new_predicate("but");
	predicate sortieAtteint <- new_predicate("sortie");
	predicate butAndSortie <- (butAtteint and sortieAtteint);
	predicate butOrSortie <- (butAtteint or sortieAtteint);

	
//	reflex perception{
////		write get_current_intention();
//		if(self.location = monBut.location){
//			do add_belief(butAtteint);
//			do remove_desire(butAtteint);
//		}
//		if(self.location = maSortie.location){
//			do add_belief(sortieAtteint);
//			do remove_desire(sortieAtteint);
//		}
//	}
	
	perceive name:a target: but /*when:false*/{
//		write "but en vue";
		ask myself{
//			do add_belief(new_predicate("new",["location"::myself.location]));
			if (myself.location=location){
				do add_belief(butAtteint);
			}
		}
//		write myself.belief_base;
//		write myself.location;
	}
	
	perceive b target: sortie /*when:false*/{
//		write "sortie en vue";
		ask myself{
//			do add_belief(new_predicate("new",["location"::myself.location]));
			if (myself.location=location){
				do add_belief(butAtteint);
			}
		}
//		write myself.belief_base;
//		write myself.location;
	}
	
	init{
		fini<-false;
		self.location <- one_of(grille).location;
		do add_desire(butOrSortie);
	}
	
	plan bouger when: is_current_intention(butAtteint) finished_when: fini=true priority: 1{
		do goto on: grille target:monBut speed:2;
	}
	
	plan bouger2 when: is_current_intention(sortieAtteint) finished_when: fini=true priority: 1{
		do goto on: grille target:maSortie speed:2;
	}
	
	aspect base{
		draw circle(1) color:#yellow;
	}
}

species but{
	rgb color <- #red;
	grille maCellule <- one_of(grille);
	list<agentTest> gagnant update: agentTest inside(maCellule);
	
	init {
		location <-maCellule.location;
	}
	
	reflex affichage {
		write length(gagnant);
	}
	
	
	aspect base{
		draw circle(1) color: color;
	}
}

species sortie{
	rgb color <- #green;
	grille maCellule <- one_of(grille);
	list<agentTest> gagnantSortie update: agentTest inside(maCellule);
	
	init {
		location <-maCellule.location;
	}
	
	aspect base{
		draw circle(1) color: color;
	}
}

grid grille width:50 height:50 neighbours:4{
	
}

experiment test type: gui{
	output {
		display main{
			species agentTest aspect:base;
			species agentTest2 aspect:base;
			species but aspect:base;
			species sortie aspect:base;
			grid grille lines:#black;
		}
	}
}