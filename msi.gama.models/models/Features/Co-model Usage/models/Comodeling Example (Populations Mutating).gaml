/**
* Name: comodeling_example_populations_mutating
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the importation and instantiation of micro-model  using the couplings  with the mutation the population of micro-model. A population can be a collection from itself and from other micro-model
* Tags: comodel
*/ 
model comodeling_example_populations_mutating

import "Prey Predator Coupling.gaml" as Organism


global
{
	// set the shape of world as a rectangle 200 x 100
	geometry shape <- square(100);
	init
	{
		//instantiate three instant of micro-model PreyPredator
		create Organism.Complex with: [shape::square(100), preyinit::rnd(20), predatorinit::1] number: 3;
		
		//explicitly save the orginal population of predator and original population of prey of each micro-model
		
		//the predator population of experiment 0 saved into the list lstpredator0  
		list<agent> lstpredator0 <- Organism.Complex[0].get_predator();
		//the prey population of experiment 0 saved into the list lstprey0
		list<agent> lstprey0 <- Organism.Complex[0].get_prey();
		
		//the predator population of experiment 1 saved into the list lstpredator1
		list<agent> lstpredator1 <-Organism.Complex[1].get_predator();
		//the prey population of experiment 1 saved into the list lstprey1
		list<agent> lstprey1 <- Organism.Complex[1].get_prey();

		//the predator population of experiment 2 saved into the list lstpredator2
		list<agent> lstpredator2 <- Organism.Complex[2].get_predator();
		//the prey population of experiment 2 saved into the list lstprey2
		list<agent> lstprey2 <- Organism.Complex[2].get_prey();
		
		
		//mutate the popuplation of micro-model by assigning the list above to  the population of micro-models
		
		
		(Organism.Complex[0].simulation).lstPredator <- lstpredator2;
		(Organism.Complex[1].simulation).lstPredator <- lstprey2;
		(Organism.Complex[2].simulation).lstPredator <- lstprey1;
		
		
		(Organism.Complex[0].simulation).lstPrey <- lstprey0 + lstprey1;
		(Organism.Complex[1].simulation).lstPrey <- lstpredator1;
		(Organism.Complex[2].simulation).lstPrey <- lstpredator0 + lstprey2;
		
		//change the shape correspond with the new role of agent in the new populations
		ask (Organism.Complex accumulate each.simulation.lstPredator)
		{
			shape <- triangle(2);
		}

		ask (Organism.Complex accumulate each.simulation.lstPrey)
		{
			shape <- circle(0.5);
		}

	}

	reflex simulate_micro_models
	{
		// ask all simulation do their job
		ask (Organism.Complex collect each.simulation)
		{
			do _step_;
		}

	}

}

experiment main type: gui
{
	output
	{
		//a mixing display of all agents from all populations
		display "Comodel display"
		{
			agents "agentprey" value: (Organism.Complex accumulate each.get_prey());
			agents "agentpredator" value: (Organism.Complex accumulate each.get_predator());
		}

	}

}