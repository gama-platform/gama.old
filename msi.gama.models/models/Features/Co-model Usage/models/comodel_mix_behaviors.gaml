/**
* Name: comodel with mixed behaviors 
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the mixing behaviors of preyPredator with the Ants. Ants are the prey, fleeing from Predators, when they are not chasing, they try to do job of the ants.
* Tags: comodel
*/
model comodel_mix_behaviors

import "PreyPredator_coupling.gaml" as myPreyPredator
import "Ants_coupling.gaml" as myAnt


global
{
	//set the shape of environment: square 100 
	geometry shape <- square(100);
	// the variable that refer to the ants population in micro-model 
	list<agent> theAnts;
	// the variable that refer to the prey population in micro-model
	list<prey> thePreys;
	
	init
	{
		//create the Ants micro-model with the size of grid is 100 and the population have 500 ants.
		create myAnt.Ants_coupling_exp with: [gridsize::100,ants_number::500];
		//create the PreyPredator micro-model with the parameters and the number of the prey is equal with the size of ants population
		create myPreyPredator.PreyPredator_coupling_exp with: [shape::square(100), preyinit::myAnt.Ants_coupling_exp[0].simulation.ants_number, predatorinit::3]  
		{
			// set the size of micro-model PreyPredator equal with the size of the grid of myAnt
			shape <- square(100);
		}

		// save the original population of the Ants and the Preys
		theAnts <- myAnt.Ants_coupling_exp accumulate each.getAnts();
		thePreys <- list<prey>(myPreyPredator.PreyPredator_coupling_exp accumulate each.getPrey());


	}

	reflex simulate_micro_models
	{
		// ask myAnt do a step
		ask (myAnt.Ants_coupling_exp collect each.simulation)
		{
			do _step_;
		}
		// ask myPreyPredator do a step, too
		ask (myPreyPredator.PreyPredator_coupling_exp collect each.simulation)
		{
			do _step_;
		}

		//check if a Prey is chased, set the position of that agent to the location of prey 
		//if not, set ant's location to agent location.
		// if the agent (prey) died, then tell the ant do die
		loop i from: 0 to: length(theAnts) - 1
		{
			if (!dead(thePreys at i) and !dead(theAnts at i))
			{
				if (!(thePreys at i).is_chased)
				{
					(thePreys at i).location <- (theAnts at i).location;
				} else
				{
					(theAnts at i).location <- (thePreys at i).location;
				}

			} else
			{
				ask (theAnts at i)
				{
					do die;
				}

			}

		}

	}

}

experiment comodel_mix_behaviors_exp type: gui
{
	output
	{
		display "comodel"
		{
			agents "ant_grid" value: myAnt.Ants_coupling_exp accumulate each.getAnt_grid() transparency: 0.7;
			agents "agentprey" value: (myPreyPredator.PreyPredator_coupling_exp accumulate each.getPrey());
			agents "agentpredator" value: (myPreyPredator.PreyPredator_coupling_exp accumulate each.getPredator());
		}

	}

}