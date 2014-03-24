/**
 *  SavetoCSV
 *  Author: Patrick Taillandier
 *  Description: 
 */

model SavetoCSV

global {
	init {
		create bug number: 50;
	}
	
	reflex save_bug_attribute when: cycle = 100{
		ask bug {
			// save the values of the variables name, speed and size to the csv file
			save [name,speed, size] to: "../results/bug.csv" type:"csv";
		}
		do pause;
	}
}

species bug skills:[moving]{
	float size <- 1.0 + rnd(4) min: 1.0 max: 5.0;
	float speed <- 1.0 + rnd(4.0);
	
	reflex update_size {
		int nb_neigh <- length(bug at_distance 20.0);
		if (nb_neigh > 5) {
			size <- size + 1;
		} else {
			size <- size - 1;
		}
	} 	
	reflex move {
		do wander;
	}
	
	aspect default {
		draw circle(size) color: °red;
	}
}

experiment SavetoCSV type: gui {
	output {
		display map {
			species bug;
		}
	}
}
