/**
* Name:  Model using Batch mode
* Author:  Patrick Taillandier
* Description: A model showing how to use batch experiments to find the best combination of parameters to minimize the numbers of infected people 
*      in a SIR infection model where agents infect others and become immune for a certain time. The batch mode uses three different methods : Exhaustive, 
*      GA and Tabu Search. The model proposes five experiments : one simple with a User Interface, one running 5 experiments and saving the data, and one 
*      for each strategy. 
* Tags: batch, algorithm, save_file
*/


model batch_example

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
        	color <- #red;
        }
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

	reflex end_of_immunity when: is_immune {
		if (cpt > end_immunity_step) {
			cpt <- 0;
			is_immune <- false;
			color <-  #green;
		} else {
			cpt <- cpt + 1;
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
	
	// the reflex will be activated at the end of each run; in this experiment a run consists of the execution of 5 simulations (repeat: 5)
	reflex end_of_runs
	{
		int cpt <- 0;
		// each simulation of the run is an agent; it is possible to access to the list of these agents by using the variable "simulations" of the experiment. 
		// Another way of accessing to the simulations consists in using the name of model + _model: here "batch_example_model"
		//in this example, we ask all the simulation agents of the run to save (at the end of the simulation) the people population in a shapefile with their is_infected and is_immune attributes 
		ask simulations
		{
			save people type: "shp" to: "result/people_shape" + cpt + ".shp" attributes: ["INFECTED"::is_infected, "IMMUNE"::is_immune];
			cpt <- cpt + 1;
		}
	}
}

// This experiment explores two parameters with an exhaustive strategy,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment 'Exhaustive optimization' type: batch repeat: 5 keep_seed: true until: ( time > 1000 ) {
	parameter 'Infection rate' var: infection_rate among: [ 0.1 , 0.5 , 1.0 ];
	parameter 'Speed of people:' var: speed_people min: 1.0 max: 3.0 step:1.0;
	method exhaustive minimize: nb_infected;
	
	//the permanent section allows to define a output section that will be kept during all the batch experiment
	permanent {
		display Comparison {
			chart "Number of people infected" type: series {
				//we can access to all the simulations of a run (here composed of 5 simulation -> repeat: 5) by the variable "simulations" of the experiment.
				//here we display for the 5 simulations, the mean, min and max values of the nb_infected variable.
				data "Mean" value: mean(simulations collect each.nb_infected ) style: spline color: #blue ;
				data "Min" value:  min(simulations collect each.nb_infected ) style: spline color: #darkgreen ;
				data "Max" value:  max(simulations collect each.nb_infected ) style: spline color: #red ;
			}
		}	
	}
}

// This experiment explores two parameters with a GA strategy,
// repeating each simulation three times (the aggregated fitness correspond to the min fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Genetic type: batch keep_seed: true repeat: 3 until: ( time > 1000 ) {
	parameter 'Infection rate' var: infection_rate among: [ 0.1 ,0.2, 0.5 , 0.6,0.8, 1.0 ];
	parameter 'Speed of people:' var: speed_people min: 1.0 max: 10.0 step:1.0;
	method genetic pop_dim: 3 crossover_prob: 0.7 mutation_prob: 0.1 improve_sol: true stochastic_sel: false
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