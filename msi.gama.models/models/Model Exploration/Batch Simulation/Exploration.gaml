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
	int max_step <- 0;
	
	reflex save_result when: (nb_preys > 0) and (nb_predators > 0){ } // Overload method so we do not have any saved output
	bool stop_sim { return (time > 1000); }
	
	
	// Inter var
	bool prey_extinct <- false;
	bool predators_extinct <- false;
	bool species_extinct <- false;
	
	/* Saving into csv files for headless */
	float t0;
	
	// Selected Outputs
	int max_preys <- nb_preys_init;
	int max_predators <- nb_predators_init;
	int cycle_extinction <- 0;
	int preys_extinction <- 0;
	int predators_extinction <- 0;	
	int species_survive <- 1;
	
	reflex start when: cycle=0{
		t0 <- machine_time;
		write "["+ int(seed) + "] Start.";
	}
	
	reflex prey_extinction when : (nb_preys=0) and (!prey_extinct) {
		prey_extinct <- true;
		species_survive <- 0;
		preys_exctinction <- cycle;
	}
	
	reflex predators_extinction when : (nb_predators=0) and (!predators_extinct) {
		predators_extinct <- true;
		species_survive <- 0;
		predators_extinction <- cycle;
	}
	
	reflex species_extinction when : ( (nb_predators=0) and (nb_preys=0) and (!species_extinct){
		species_extinct <- true;
		species_survive <- 0;
		cycle_extinction <- cycle;
	}
	
	reflex max_entity when: every(1#cycle) {
		max_preys <- (max_prey<nb_preys) ? nb_preys : max_preys;
		max_predatirs <- (max_predatirs<nb_predators) ? nb predators : max_predators;
	}
	
	// Save input and final state of the simulation into a csv
	reflex stop_and_save when: (time > 999) {
		string global_name <- "./Results/GLOBAL/results.csv";

		// Save final Results
		save [
			// Inputs
			nb_preys_init,
			prey_max_transfer,
			prey_max_energy,
			prey_energy_consum,
			nb_predators_init,
			predator_max_energy,
			predator_energy_transfer,
			predator_energy_consum,
			prey_proba_reproduce,
			prey_nb_max_offsprings,
			prey_energy_reproduce,
			predator_proba_reproduce,
			predator_nb_max_offsprings,
			predator_energy_reproduce,

			// Outputs
			nb_preys,
			nb_predators,
			max_preys,
			max_predators,
			cycle_extinction,
			preys_extinction,
			predators_extinction,
			species_survive,

			// To identify the experiment
			seed
		] to:global_name type:"csv" rewrite: false;


		// Write execution time
		write "["+ int(seed) + "] End. Execution time : " + string ((machine_time - t0) / 1000) + "s. Result saved";
	}
	
	// Save state of simulation each steps
	reflex save_outputs {
		max_step <- cycle;
		string sample_name <- "./Results/SAMPLE/sample_" 
						+ nb_preys_init + "_"
						+ prey_max_energy + "_"
						+ prey_max_transfer + "_"
						+ prey_energy_consum + "_"
						+ nb_predators_init + "_"
						+ predator_max_energy + "_"
						+ predator_energy_transfer + "_"
						+ predator_energy_consum + "_"
						+ prey_proba_reproduce + "_"
						+ prey_nb_max_offsprings + "_"
						+ prey_energy_reproduce + "_"
						+ predator_proba_reproduce + "_"
						+ predator_nb_max_offsprings + "_"
						+ predator_energy_reproduce + "/"
						+ "replicat_" + int(self) + ".csv";
		save[
			nb_preys,
			nb_predators,
			max_preys,
			max_predators,
			cycle_extinction,
			preys_extinction,
			predators_extinction,
			species_survive,
			cycle
		] to: (sample_name) type: "csv" rewrite:false;
	}
}

/* 
 * Model used to expose simulation exploration capabilities of Gama batch experiments
 * ----
 * See Predator Prey in tutorial in the Model Library
 * 
 */

experiment batch_abstract type:batch virtual:true until: world.stop_sim() {
	parameter "Initial number of preys: " var: nb_preys_init min: 1 max: 1000;
	parameter "Prey max energy: " var: prey_max_energy min:0.0 max: 10.0;
	parameter "Prey max transfer:" var: prey_max_transfer min: 0.05 max: 0.5;
	parameter "Prey energy consumption: " var: prey_energy_consum min: 0.0 max:1.0;
	parameter "Initial number of predators: " var: nb_predators_init min: 1 max: 1000;
	parameter "Predator max energy: " var: predator_max_energy min:0.0 max: 10.0;
	parameter "Predator energy transfer:" var: predator_energy_transfer min: 0.05 max: 0.5;
	parameter "Predator energy consumption: " var: predator_energy_consum min: 0.0 max:1.0;
	parameter 'Prey probability reproduce: ' var: prey_proba_reproduce min: 0.0 max: 1.0;
	parameter 'Prey nb max offsprings: ' var: prey_nb_max_offsprings min: 1 max: 10;
	parameter "Prey energy reproduce:" var: prey_energy_reproduce min: 0.05 max: 0.75;
	parameter 'Predator probability reproduce: ' var: predator_proba_reproduce min: 0.0 max: 1.0;
	parameter 'Predator nb max offsprings: ' var: predator_nb_max_offsprings min: 1 max: 10;
	parameter "Predator energy reproduce:" var: predator_energy_reproduce min: 0.05 max: 0.75;
}
 
experiment headless type:gui{
	parameter "Initial number of preys: " var: nb_preys_init min: 1 max: 1000;
	parameter "Prey max energy: " var: prey_max_energy min:0.0 max: 10.0;
	parameter "Prey max transfer:" var: prey_max_transfer min: 0.05 max: 0.5;
	parameter "Prey energy consumption: " var: prey_energy_consum min: 0.0 max:1.0;
	parameter "Initial number of predators: " var: nb_predators_init min: 1 max: 1000;
	parameter "Predator max energy: " var: predator_max_energy min:0.0 max: 10.0;
	parameter "Predator energy transfer:" var: predator_energy_transfer min: 0.05 max: 0.5;
	parameter "Predator energy consumption: " var: predator_energy_consum min: 0.0 max:1.0;
	parameter 'Prey probability reproduce: ' var: prey_proba_reproduce min: 0.0 max: 1.0;
	parameter 'Prey nb max offsprings: ' var: prey_nb_max_offsprings min: 1 max: 10;
	parameter "Prey energy reproduce:" var: prey_energy_reproduce min: 0.05 max: 0.75;
	parameter 'Predator probability reproduce: ' var: predator_proba_reproduce min: 0.0 max: 1.0;
	parameter 'Predator nb max offsprings: ' var: predator_nb_max_offsprings min: 1 max: 10;
	parameter "Predator energy reproduce:" var: predator_energy_reproduce min: 0.05 max: 0.75;
}

// This experiment runs the simulation 5 times.
// At the end of each simulation, the people agents are saved in a shapefile
experiment 'Run 5 simulations' parent: batch_abstract type: batch repeat: 5 keep_seed: true until: world.stop_sim(){
	
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
experiment replication_analysis parent: batch_abstract type: batch until: world.stop_sim() repeat:10 {
	method stochanalyse outputs:["nb_preys","nb_predators"] results:"Results/stochanalysis.txt";
} 

// This experiment explores two parameters with an exhaustive strategy,
// repeating each simulation three times (the aggregated fitness correspond to the mean fitness), 
// in order to find the best combination of parameters to minimize the number of infected people
experiment Exhaustive parent: batch_abstract type: batch repeat: 3 keep_seed: true until: world.stop_sim() {
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
experiment Exhaustive_with_LHS  parent:Exhaustive repeat:3 type: batch until:world.stop_sim(){
	method exhaustive sampling:"latinhypercube" sample:5;
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
experiment Explicit parent: batch_abstract type: batch repeat: 3 keep_seed: true until: world.stop_sim() {
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
// nb of simulations to run = n * (2 * p + 2) = 1024 * (2 * 14 + 2) = 30720
experiment Sobol parent: batch_abstract type: batch keep_seed:true until:world.stop_sim() {
	method sobol outputs:["nb_preys","nb_predators","max_step"] sample:2 path: "Results/saltelli.csv" report:"Results/sobol.txt" results:"Results/GLOBAL/results.csv";
}

// This experiment samples from the parameter space (Morris methods) to establish
// Morris index, i.e. a contribution value of each parameters to 
// observed variance for outcomes of interest.
// nb of simulations to run = n * p = 1024 * 14 = 14336
experiment Morris parent: batch_abstract type: batch keep_seed:true until:world.stop_sim() {
	method morris outputs:["nb_preys","nb_predators"] levels: 4 sample:1 report:"Results/morris.txt" results:"Results/morris_raw.csv";
}









