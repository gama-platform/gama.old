model prey_predator
//Model 1 of the predator/prey tutorial

global {
	int nb_preys_init <- 200;
	init {
		create prey number: nb_preys_init ;
	}
}

species prey {
	float size <- 1.0 ;
	rgb color <- #blue;
		
	aspect base {
		draw circle(size) color: color ;
	}
} 

experiment prey_predator type: gui {
	parameter "Initial number of preys: " var: nb_preys_init min: 1 max: 1000 category: "Prey" ;
	output {
		display main_display {
			species prey aspect: base ;
		}
	}
}

 