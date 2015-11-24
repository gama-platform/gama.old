/**
 *  SavetoText
 *  Author: Patrick Taillandier
 *  Description: Show how to save data into a text file
 */

model SavetoText

global {
	init {
		create bug number: 50;
	}
	
	reflex save_data when: every(10){
		//save the following text into the given text file. Note that each time the save statement is used, a new line is added at the end of the file.
		save ("cycle:" + cycle + ", mean size: " + mean(bug collect each.size)) to: "../results/data.txt";
	}
	reflex end_simulation when: cycle = 100 {
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
		draw circle(size) color: #red;
	}
}

experiment main type: gui {
	output {
		display map {
			species bug;
		}
	}
}
