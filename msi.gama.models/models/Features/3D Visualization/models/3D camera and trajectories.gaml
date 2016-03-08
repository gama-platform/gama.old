model multi_simulations

global {
	init {
		create my_species number:50;
	}
}

species my_species skills:[moving] {
	reflex update {
		do wander;
	}
	aspect base {
		draw square(2) color:#blue;
	}
}

experiment my_experiment type:gui  {
	float seed <- 10.0;
	init {
		create simulation with:[rng::"cellular",seed::10.0];
		create simulation with:[rng::"java",seed::10.0];
	}
	output {
		display my_display {
			species my_species aspect:base;
			graphics "my_graphic" {
				draw rectangle(35,10) at:{0,0} color:#lightgrey;
				draw rng at:{3,3} font:font("Helvetica", 20 , #plain) color:#black;
			}
		}
	}
}