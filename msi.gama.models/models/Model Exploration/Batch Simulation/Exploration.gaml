/**
* Name: Exploration
* Based on the internal empty template. 
* Author: kevinchapuis
* Tags: batch
*/


model Exploration

import "../../Tutorials/Predator Prey/models/Model 13.gaml"

/*
 * Change a little bit the behavior of the world agent to fit exploration requirements
 */
global {
	int end_cycle <- 500;
	reflex save_result when: (nb_preys > 0) and (nb_predators > 0){ } // Overload method so we do not have any saved output
	bool stop_sim { return (nb_preys = 0) or (nb_predators = 0); } 
}

/* 
 * Model used to expose simulation exploration capabilities of Gama batch experiments
 * ----
 * See Predator Prey in tutorial in the Model Library
 * 
 */
 
experiment batch_abstract type:batch virtual:true until:(time > end_cycle) {
	init {is_batch <- true;}
	parameter "Prey max transfer:" var: prey_max_transfer min: 0.05 max: 0.5 step: 0.05;
	parameter "Prey energy reproduce:" var: prey_energy_reproduce min: 0.05 max: 0.75 step: 0.05;
	parameter "Predator energy transfer:" var: predator_energy_transfer min: 0.1 max: 1.0 step: 0.1;
	parameter "Predator energy reproduce:" var: predator_energy_reproduce min: 0.1 max: 1.0 step: 0.1;
}

// This experiment runs the full factorial experiment (each combination of parameter) 5 times, that is 14k simulations :) see exhaustive exploration
// At the end of each simulation, the people agents are saved in a shapefile
experiment 'Run 5 simulations' parent: batch_abstract type: batch repeat: 5 keep_seed: true until: world.stop_sim() or (time > end_cycle){
	
	// the reflex will be activated at the end of each run; in this experiment a run consists of the execution of 5 simulations (repeat: 5)
	reflex end_of_runs
	{
		int cpt <- 0;
		// each simulation of the run is an agent; it is possible to access to the list of these agents by using the variable "simulations" of the experiment. 
		// Another way of accessing to the simulations consists in using the name of model + _model: here "batch_example_model"
		//in this example, we ask all the simulation agents of the run to save (at the end of the simulation) the people population in a shapefile with their is_infected and is_immune attributes 
		ask simulations
		{
			save [nb_preys,nb_predators] to: "Results/preypredator.csv" format:"csv" rewrite:false;
		}
	}
}

// This experiment extract the number of replicates that should be made according to two methods:
// 1. How increasing the number of replicates dimish the standard error
// 2. coefficient of variation 
experiment replication_analysis parent: batch_abstract type: batch until: world.stop_sim() or ( time > end_cycle ) 
	repeat:40 keep_simulations:false {
	method stochanalyse outputs:["nb_preys", "nb_predators"] report:"Results/stochanalysis.txt" results:"Results/stochanalysis_raw.csv" sample:3;
} 

// This experiment explores the four parameters with an exhaustive strategy (default sampling method for exploration),
// repeating each simulation three times. The overall combination of parameter values is 14000 (times 3 replications).
// This is what we call 'brut force' exploration - because obviously it is not smart, nor gentle in the exploration approach. 
// In order to diminish number of explored points, one can use 'factorial' facet (how many value per parameter) or diminish the step
// facet of parameters each at a time. Another way is to rely on smarter strategy to sample from the parameter space using sampling methods.  
experiment exhaustive_exploration parent: batch_abstract type: batch repeat: 3 keep_seed: true until: world.stop_sim() or ( time > end_cycle ) {
	method exploration;
	
	//the permanent section allows to define a output section that will be kept during all the batch experiment
	permanent {
		display Comparison  type: 2d {
			chart "Number of people infected" type: series {
				//we can access to all the simulations of a run (here composed of 5 simulation -> repeat: 5) by the variable "simulations" of the experiment.
				//here we display for the 5 simulations, the mean, min and max values of the nb_infected variable.
				data "Mean" value: mean(simulations collect each.nb_preys ) style: spline color: #blue ;
				data "Min" value:  min(simulations collect each.nb_preys ) style: spline color: #darkgreen ;
				data "Max" value:  max(simulations collect each.nb_preys ) style: spline color: #red ;
			}
		}	
	}
}

// This experiment tests two explicit parameters sets,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
experiment explicit_exploration parent: batch_abstract type: batch repeat: 3 keep_seed: true until: world.stop_sim() or ( time > end_cycle ) {
	method exploration with: [
		["prey_max_transfer"::0.1, "predator_energy_transfer":: 0.01],
		["prey_max_transfer"::0.5, "predator_energy_transfer":: 0.2],
		["prey_max_transfer"::1.0, "predator_energy_transfer":: 0.05],
		["prey_max_transfer"::0.5, "predator_energy_transfer":: 0.1]
	];
}

// This experiment iterate over point of the parameter space choosen following
// Latin Hypercube Sampling
experiment exploration_with_sampling  parent: batch_abstract repeat:3 type: batch until:world.stop_sim() or time>end_cycle {
	method exploration sampling:"latinhypercube" sample:100;
	//the permanent section allows to define a output section that will be kept during all the batch experiment
	permanent {
		display Comparison  type: 2d {
			chart "Number of people infected" type: series {
				//we can access to all the simulations of a run (here composed of 5 simulation -> repeat: 5) by the variable "simulations" of the experiment.
				//here we display for the 5 simulations, the mean, min and max values of the nb_infected variable.
				data "Mean" value: mean(simulations collect each.nb_predators ) style: spline color: #blue ;
				data "Min" value:  min(simulations collect each.nb_predators ) style: spline color: #darkgreen ;
				data "Max" value:  max(simulations collect each.nb_predators ) style: spline color: #red ;
			}
		}	
	}
}

// This experiment samples from the parameter space (Saltelli methods) to establish
// Sobol index, i.e. a contribution value of each parameters to 
// observed variance for outcomes of interest, 
// more on this see https://www.jasss.org/19/1/5.html and http://moeaframework.org for the API
experiment Sobol parent: batch_abstract type: batch until:( time > end_cycle ) {
	method sobol outputs:["nb_preys","nb_predators"] sample:10 report:"Results/sobol.csv" results:"Results/sobol_raw.csv";
}

// This experiment perform a Morris analysis (see Morris 1991, doi:10.1080/00401706.1991.10484804)
// to screen and rank parameters based on elementary effect (changes on outputs due to a small modification of 
// one paameter value)
experiment Morris parent: batch_abstract type: batch until:( time > end_cycle ) {
	method morris outputs:["nb_preys","nb_predators"] sample:100 levels:4 report:"Results/morris.txt" results:"Results/morris_raw.csv";
}

// This experiment computed beta d kuiper statistics to estimate the impact of parameters
// on the distribution of outputs. It has been retro-engineered based on the description in
// Borgonovo et al. 2022 doi:10.1007/s10588-021-09358-5
experiment Beta_distribution parent: batch_abstract type: batch until:( time > end_cycle ) {
	method betad outputs:["nb_preys","nb_predators"] sample:100 sampling:"factorial" report:"Results/betad.txt" results:"Results/betad_raw.csv";
}