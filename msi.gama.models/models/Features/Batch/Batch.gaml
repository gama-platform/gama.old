model si
// A simple infection spreading model

global {
    int number_people <- 300;  // The init number of people
    int number_I <- 1 ;	// The init number of infected
    float infection_rate <- 0.1 ; // The infection rate
	float infection_distance <- 5.0 ; // infection distance (in meters)
	int immune_step <- 30 ; // number of steps before becoming immune after infection
	int end_immunity_step <-50; // number of steps before not being immune anymore
	float speed_people <- 5.0 ; // speed of the Host

	int nb_infected <- 0;
	init {
		create people number: number_people ;
        ask (number_I among people) {
        	is_infected <- true;
        	color <- rgb("red");
        }
   }
  }

species people skills:[moving] {
	bool is_infected <- false;
	bool is_immune <- false;
	rgb color <- rgb("green");
	int cpt <- 0;
	reflex basic_move {
		do wander speed: speed_people;
	}

	reflex end_of_immunity when: is_immune {
		if (cpt > end_immunity_step) {
			cpt <- 0;
			is_immune <- false;
			color <-  rgb("green");
		} else {
			cpt <- cpt + 1;
		}
	}
	reflex become_immune when: is_infected {
		if (cpt > immune_step) {
			cpt <- 0;
			is_immune <- true;
			is_infected <- false;
			color <-  rgb("blue");
		} else {
			cpt <- cpt + 1;
		}
	}
	reflex become_infected when: not is_infected and not is_immune{
		if (flip(infection_rate) and not empty(people at_distance infection_distance where each.is_infected)) {
			is_infected <- true;
			color <-  rgb("red");
			nb_infected <- nb_infected + 1;
		}
	}

	aspect default {
		draw circle(1) color: color;
    }
}

experiment Simple type:gui {
	parameter 'Infection Rate:' var: infection_rate;
	parameter 'Infection Distance:' var: infection_distance;
	output {
		monitor "nb of infected people" value: nb_infected;
		display map {
			species people aspect: default;
		}
	}
}


// This experiment runs the simulation 5 times.
// At the end of each simulation, the people agents are saved in a shapefile
experiment 'Run 5 simulations' type: batch repeat: 5 keep_seed: true until: ( time > 1000 ) {
	int cpt <- 0;
	action _step_ {
		save people type:"shp" to:"people_shape" + cpt + ".shp" with: [is_infected::"INFECTED",is_immune::"IMMUNE"];
		cpt <- cpt + 1;
	}
}

// This experiment explores two parameters with an exhaustive strategy,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment 'Exhaustive optimization' type: batch repeat: 5 keep_seed: true until: ( time > 1000 ) {
	parameter 'Infection rate' var: infection_rate among: [ 0.1 , 0.5 , 1.0 ];
	parameter 'Speed of people:' var: speed_people min: 1.0 max: 3.0 step:1.0;
	method exhaustive minimize: nb_infected;
}

// This experiment explores two parameters with a GA strategy,
// repeating each simulation three times (the aggregated fitness correspond to the min fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Genetic type: batch keep_seed: true repeat: 3 until: ( time > 1000 ) {
	parameter 'Infection rate' var: infection_rate among: [ 0.1 ,0.2, 0.5 , 0.6,0.8, 1.0 ];
	parameter 'Speed of people:' var: speed_people min: 1.0 max: 10.0 step:1.0;
	method genetic pop_dim: 3 crossover_prob: 0.7 mutation_prob: 0.1
	nb_prelim_gen: 1 max_gen: 5  minimize: nb_infected  aggregation: "min";
}

// This experiment explores two parameters with a Tabu Search strategy,
// repeating each simulation three times (the aggregated fitness correspond to the max fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Tabu_Search type: batch keep_seed: true repeat: 3 until: ( time > 1000 ) {
	parameter 'Infection rate' var: infection_rate among: [ 0.1 ,0.2, 0.5 , 0.6,0.8, 1.0 ];
	parameter 'Speed of people:' var: speed_people min: 1.0 max: 10.0 step:1.0;
	method tabu iter_max: 10 tabu_list_size: 5 minimize: nb_infected aggregation: "max";
}