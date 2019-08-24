/**
* Name: Simple SI Model
* Author: GAMA team
* Description: First part of the tutorial : Incremental Model
* Tags: tutorial, gis
*/
model SI_city

global {
	int nb_people <- 500;
    float agent_speed <- 5.0 #km/#h;	
	float infection_distance <- 2.0 #m;
	float proba_infection <- 0.05;
	int nb_infected_init <- 5;
	float step <- 1 #minutes;
	geometry shape <- envelope(square(500 #m));

	init {
		create people number: nb_people {
			speed <- agent_speed;
		}

		ask nb_infected_init among people {
			is_infected <- true;
		}

	}

}

species people skills: [moving] {
	bool is_infected <- false;

	reflex move {
		do wander;
	}

	reflex infect when: is_infected {
		ask people at_distance infection_distance {
			if (flip(proba_infection)) {
				is_infected <- true;
			}
		}
	}

	aspect default {
		draw circle(5) color: is_infected ? #red : #green;
	}
}

experiment main_experiment type: gui {
	parameter "Infection distance" var: infection_distance;
	parameter "Proba infection" var: proba_infection min: 0.0 max: 1.0;
	parameter "Nb people infected at init" var: nb_infected_init;
	
	output {
		display map {
			species people; // 'default' aspect is used automatically			
		}
	}
}