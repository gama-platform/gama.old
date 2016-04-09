/**
* Name: comodel_with_the_coupling
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the importation and instatiation of micro-model  using the couplings  with the mutation the population of micro-model. A population can be a collection from itself and from other mircro-model
* Tags: comodel
*/ 
model comodel_with_the_coupling

import "PreyPredator_coupling.gaml" as myP


global
{
	// set the shape of world as a rectangle 200 x 100
	geometry shape <- rectangle(200, 100);
	init
	{
		//instantiate three instant of micro-model PreyPredator
		create myP.PreyPredator_coupling_exp with: [shape::square(100), preyinit::rnd(20), predatorinit::1] number: 3;
		
		//explicitly save the orginal population of predator and original population of prey of each micro-model
		list<agent> lstpredator0 <- myP.PreyPredator_coupling_exp[0].getPredator();
		list<agent> lstprey0 <- myP.PreyPredator_coupling_exp[0].getPrey();
		
		list<agent> lstpredator1 <- myP.PreyPredator_coupling_exp[1].getPredator();
		list<agent> lstprey1 <- myP.PreyPredator_coupling_exp[1].getPrey();
		
		list<agent> lstpredator2 <- myP.PreyPredator_coupling_exp[2].getPredator();
		list<agent> lstprey2 <- myP.PreyPredator_coupling_exp[2].getPrey();
		
		//mutate the popuplation of micro-model by assigning the list above to  the population of micro-models
		(myP.PreyPredator_coupling_exp[0].simulation).lstPredator <- lstpredator2;
		(myP.PreyPredator_coupling_exp[1].simulation).lstPredator <- lstprey2;
		(myP.PreyPredator_coupling_exp[2].simulation).lstPredator <- lstprey1;
		
		
		(myP.PreyPredator_coupling_exp[0].simulation).lstPrey <- lstprey0 + lstprey1;
		(myP.PreyPredator_coupling_exp[1].simulation).lstPrey <- lstpredator1;
		(myP.PreyPredator_coupling_exp[2].simulation).lstPrey <- lstpredator0 + lstprey2;
		
		//change the shape correspond with the new role of agent in the new populations
		ask (myP.PreyPredator_coupling_exp accumulate each.simulation.lstPredator)
		{
			shape <- triangle(2);
		}

		ask (myP.PreyPredator_coupling_exp accumulate each.simulation.lstPrey)
		{
			shape <- circle(0.5);
		}

	}

	reflex simulate_micro_models
	{
		// ask all simulation do their job
		ask (myP.PreyPredator_coupling_exp collect each.simulation)
		{
			do _step_;
		}

	}

}

experiment comodel_with_the_coupling_exp type: gui
{
	output
	{
		//a mixing display of all agents from all populations
		display "comodel"
		{
			agents "agentprey" value: (myP.PreyPredator_coupling_exp accumulate each.getPrey());
			agents "agentpredator" value: (myP.PreyPredator_coupling_exp accumulate each.getPredator());
		}

	}

}