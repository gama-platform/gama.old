/**
* Name: Exploration
* Based on the internal empty template. 
* Author: kevinchapuis
* Tags: 
*/


model Exploration

import "../../Tutorials/Predator Prey/models/Model 13.gaml"

/*
 * Change a little bit the behavior of the world agent to fit exploration requirements
 */
global {
	bool is_batch <- true;
	reflex save_result when: (nb_preys > 0) and (nb_predators > 0){ } // Overload method so we do not have any saved output
	bool stop_sim { return (nb_preys = 0) or (nb_predators = 0); } 
}

/* 
 * Model used to expose simulation exploration capabilities of Gama batch experiments
 * ----
 * See Predator Prey in tutorial in the Model Library
 * 
 */
 
experiment batch_abstract type:batch virtual:true until:(time > 200) {
	parameter "Prey max transfer:" var: prey_max_transfer min: 0.05 max: 0.5 step: 0.05;
	parameter "Prey energy reproduce:" var: prey_energy_reproduce min: 0.05 max: 0.75 step: 0.05;
	parameter "Predator energy transfer:" var: predator_energy_transfer min: 0.1 max: 1.0 step: 0.1;
	parameter "Predator energy reproduce:" var: predator_energy_reproduce min: 0.1 max: 1.0 step: 0.1;
}

// This experiment runs the simulation 5 times.
// At the end of each simulation, the people agents are saved in a shapefile
experiment 'Run 5 simulations' parent: batch_abstract type: batch repeat: 5 keep_seed: true until: world.stop_sim() or (time > 200){
	
	// the reflex will be activated at the end of each run; in this experiment a run consists of the execution of 5 simulations (repeat: 5)
	reflex end_of_runs
	{
		int cpt <- 0;
		// each simulation of the run is an agent; it is possible to access to the list of these agents by using the variable "simulations" of the experiment. 
		// Another way of accessing to the simulations consists in using the name of model + _model: here "batch_example_model"
		//in this example, we ask all the simulation agents of the run to save (at the end of the simulation) the people population in a shapefile with their is_infected and is_immune attributes 
		ask simulations
		{
			save [nb_preys,nb_predators] type: csv to: "Results/preypredator.csv";
		}
	}
}

// This experiment extract the number of replicates that should be made according to three methods:
// 1. Size effect of one added replicate with student t statistics
// 2. How increasing the number of replicates dimish the standard error
// 3. coefficient of variation 
experiment replication_analysis parent: batch_abstract type: batch until: world.stop_sim() or ( time > 200 ) repeat:10 {
	method stochanalyse outputs:["nb_preys","nb_predators"] results:"Results/stochanalysis.txt";
} 

// This experiment explores two parameters with an exhaustive strategy,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Exhaustive parent: batch_abstract type: batch repeat: 3 keep_seed: true until: world.stop_sim() or ( time > 500 ) {
	method exhaustive;
	
	//the permanent section allows to define a output section that will be kept during all the batch experiment
	permanent {
		display Comparison {
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

// This experiment iterate over point of the parameter space choosen following
// Latin Hypercube Sampling
experiment Exhaustive_with_LHS  parent:Exhaustive repeat:3 type: batch until:world.stop_sim() or time>1000 {
	method exhaustive sampling:"latinhypercube" sample:100;
	//the permanent section allows to define a output section that will be kept during all the batch experiment
	permanent {
		display Comparison {
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

// This experiment tests two explicit parameters sets,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
experiment Explicit parent: batch_abstract type: batch repeat: 3 keep_seed: true until: world.stop_sim() or ( time > 1000 ) {
	method explicit parameter_sets: [
		["prey_max_transfer"::0.1, "predator_energy_transfer":: 0.01],
		["prey_max_transfer"::0.5, "predator_energy_transfer":: 0.2],
		["prey_max_transfer"::1.0, "predator_energy_transfer":: 0.05],
		["prey_max_transfer"::0.5, "predator_energy_transfer":: 0.1]
	];
}

// This experiment samples from the parameter space (Saltelli methods) to establish
// Sobol index, i.e. a contribution value of each parameters to 
// observed variance for outcomes of interest, 
// more on this see https://www.jasss.org/19/1/5.html and http://moeaframework.org for the API
experiment Sobol parent: batch_abstract type: batch keep_seed:true until:( time > 5000 ) {
	method sobol outputs:["nb_preys","nb_predators"] sample:100 path: "Results/saltelli.csv" report:"Results/sobol.txt" results:"Results/sobol_raw.csv";
}