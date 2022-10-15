/***
* Name: BDItutorial1
* Author: Mathieu Bourgais
* Description: The skeleton model of the Goldminer BDI tutorial.
* Tags: species
***/

model BDItutorial1

global {
	int nb_mines <- 10; 
	market the_market;
	geometry shape <- square(20 #km);
	float step <- 10#mn;	
	
	init {
		create market {
			the_market <- self;
		}
		create gold_mine number: nb_mines;
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

experiment GoldBdi type: gui {

	output {
		display map type: 3d {
			species market ;
			species gold_mine ;
		}
	}
}
