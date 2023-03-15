/**
* Name:  Model using Batch mode
* Author:  Patrick Taillandier
* Description: A model showing how to use batch experiments to find the best combination of parameters to minimize the numbers of infected people 
*      in a SIR infection model where agents infect others and become immune after a certain time and has a probability to die. The batch mode uses seven different methods: Hill climbing
*      GA, PSO, Tabu Search, Reactive Tabu Search, Simulated Annealing, and Explicit exploration. The model proposes five experiments : one simple with a User Interface, one running 10 experiments and saving the data, and one 
*      for each strategy. 
* Tags: batch, algorithm, save_file
*/


model batch_example

global {
    int number_people <- 100;  // The init number of people
    int number_I <- 5 ;	// The init number of infected
    float infection_rate <- 0.1 ; // The infection rate
	float infection_distance <- 5.0 ; // infection distance (in meters)
	float dying_proba <- 0.01; //probability to die at each step if infected
	int immune_step <- 100 ; // number of steps before becoming immune after infection
	float speed_people <- 5.0 ; // speed of the Host

	int num_dead <- 0;
	init {
		float t <- machine_time;
		create people number: number_people ;
        ask (number_I among people) {
        	is_infected <- true;
        	color <- #red;
        }
	} 
	
	reflex write_info when: time = 5000  {
		write sample(infection_rate) + " " + sample(dying_proba) + " " +sample(seed) + " -> " + sample(num_dead);
	}
}

species people skills:[moving] {
	bool is_infected <- false;
	bool is_immune <- false;
	rgb color <- #green;
	int cpt <- 0;
	reflex basic_move {
		do wander speed: speed_people;
	}

	reflex die when: is_infected {
		if flip(dying_proba) {
			num_dead <- num_dead + 1;
			do die;
		}
	}
	reflex become_immune when: is_infected {
		if (cpt > immune_step) {
			cpt <- 0;
			is_immune <- true;
			is_infected <- false;
			color <-  #blue;
		} else {
			cpt <- cpt + 1;
		}
	}
	reflex become_infected when: not is_infected and not is_immune{
		if (flip(infection_rate) and not empty(people at_distance infection_distance where each.is_infected)) {
			is_infected <- true;
			color <-  #red;
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
		monitor "nb of dead" value: num_dead;
		display map {
			species people aspect: default;
		}
	}
}

// This experiment explores two parameters with a PSO strategy,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment PSO type: batch keep_seed: true repeat: 3 until: ( time > 5000 ) {
	parameter 'Infection rate' var: infection_rate min: 0.1 max:0.5 step:0.01;
	parameter 'Probability of dying:' var: dying_proba min: 0.01 max: 0.2 step:0.01;
	method pso num_particles: 3 weight_inertia:0.7 weight_cognitive: 1.5 weight_social: 1.5  iter_max: 5  minimize: num_dead  ; 
}

// This experiment explores two parameters with a GA strategy,
// repeating each simulation three times (the aggregated fitness correspond to the min fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Genetic type: batch keep_seed: true repeat: 3 until: ( time > 5000 ) {
	parameter 'Infection rate' var: infection_rate min: 0.1 max:0.5 step:0.01;
	parameter 'Probability of dying:' var: dying_proba min: 0.01 max: 0.2 step:0.01;
	method genetic pop_dim: 3 crossover_prob: 0.7 mutation_prob: 0.1 improve_sol: true stochastic_sel: false
	nb_prelim_gen: 1 max_gen: 5  minimize: num_dead  aggregation: "min";
}


// This experiment explores two parameters with a Hill Climbing strategy from an explicit init solution (if no solution is given, start from a random sol),
// repeating each simulation three times (the aggregated fitness correspond to the max fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Hill_Climbing type: batch keep_seed: true repeat: 3 until: ( time > 5000 ) {
	parameter 'Infection rate' var: infection_rate min: 0.1 max:0.5 step:0.1;
	parameter 'Probability of dying:' var: dying_proba min: 0.01 max: 0.2 step:0.01;
	method hill_climbing init_solution:map(["infection_rate"::0.2, "dying_proba":: 0.05])  minimize: num_dead aggregation: "max";
}


// This experiment explores two parameters with a Tabu Search strategy,
// repeating each simulation three times (the aggregated fitness correspond to the max fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Tabu_Search type: batch keep_seed: true repeat: 3 until: ( time > 5000 ) {
	parameter 'Infection rate' var: infection_rate min: 0.1 max:0.5 step:0.01;
	parameter 'Probability of dying:' var: dying_proba min: 0.01 max: 0.2 step:0.01;
	method tabu iter_max: 5 tabu_list_size: 5 minimize: num_dead aggregation: "max";
}

// This experiment explores two parameters with a Reactive Tabu Search strategy,
// repeating each simulation three times (the aggregated fitness correspond to the max fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Reactive_Tabu_Search type: batch keep_seed: true repeat: 3 until: ( time > 5000 ) {
	parameter 'Infection rate' var: infection_rate min: 0.1 max:0.5 step:0.01;
	parameter 'Probability of dying:' var: dying_proba min: 0.01 max: 0.2 step:0.01;
	method reactive_tabu iter_max: 10 cycle_size_max: 10 cycle_size_min: 3 tabu_list_size_init: 5 minimize: num_dead aggregation: "max";
}

// This experiment explores two parameters with a Simulated annealing strategy,
// repeating each simulation three times (the aggregated fitness correspond to the max fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Simulated_annealing type: batch keep_seed: true repeat: 3 until: ( time > 5000 ) {
	parameter 'Infection rate' var: infection_rate min: 0.1 max:0.5 step:0.01;
	parameter 'Probability of dying:' var: dying_proba min: 0.01 max: 0.2 step:0.01;
	method annealing  minimize: num_dead temp_decrease: 0.5 temp_end: 10.0 temp_init: 50;
}