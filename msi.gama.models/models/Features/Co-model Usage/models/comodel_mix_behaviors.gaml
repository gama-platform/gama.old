/**
* Name: comodel with mixed behaviors 
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the mixing behaviors of preyPredator with the Ants. Ants are the prey, fleeing from Predators, when they are not chasing, they try to do job of the ants.
* Tags: comodel
*/
model comodel_mix_behaviors

import "PreyPredator_coupling.gaml" as myP
import "Ants_coupling.gaml" as myAnt


global
{
	geometry shape <- square(100);
	list<agent> theAnts;
	list<prey> thePreys;
	int n <- 0;
	init
	{
		create myAnt.Ants_coupling_exp with: [gridsize::100,ants_number::500];
		create myP.PreyPredator_coupling_exp with: [shape::square(100), preyinit::myAnt.Ants_coupling_exp[0].simulation.ants_number, predatorinit::3]  
		{
			shape <- square(100);
		}

		list<agent> lstpredator0 <- myP.PreyPredator_coupling_exp[0].getPredator();
		list<agent> lstprey0 <- myP.PreyPredator_coupling_exp[0].getPrey() + myAnt.Ants_coupling_exp accumulate each.getAnts();
		theAnts <- myAnt.Ants_coupling_exp accumulate each.getAnts();
		thePreys <- list<prey>(myP.PreyPredator_coupling_exp accumulate each.getPrey());


	}

	reflex simulate_micro_models
	{
		ask (myAnt.Ants_coupling_exp collect each.simulation)
		{
			do _step_;
		}

		ask (myP.PreyPredator_coupling_exp collect each.simulation)
		{
			do _step_;
		}

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
			agents "agentprey" value: (myP.PreyPredator_coupling_exp accumulate each.getPrey());
			agents "agentpredator" value: (myP.PreyPredator_coupling_exp accumulate each.getPredator());
		}

	}

}