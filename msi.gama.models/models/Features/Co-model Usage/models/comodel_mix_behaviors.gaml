/**
* Name: comodel_with_the_coupling
* Author: Lô
* Description: This is a simple comodel serve to demonstrate the mixing behaviors of preyPredator with the Ants. Ants are the prey, fleeing from Predators, when they are not chasing, they try to do job of the ants.
* Tags: Tag1, Tag2, TagN 
*/
model comodel_mix_behaviors

import "PreyPredator_coupling.gaml" as myP
import "Ants_coupling.gaml" as myAnt


global
{
	geometry shape <- square(100);
	list<agent> aa;
	list<prey> ll;
	int n <- 0;
	init
	{
		create myAnt.Ants_coupling_exp with: [gridsize::100,ants_number::200];
		create myP.PreyPredator_coupling_exp with: [shape::square(100), preyinit::myAnt.Ants_coupling_exp[0].simulation.ants_number, predatorinit::3] number: 1
		{
			shape <- square(100);
		}

		list<agent> lstpredator0 <- myP.PreyPredator_coupling_exp[0].getPredator();
		list<agent> lstprey0 <- myP.PreyPredator_coupling_exp[0].getPrey() + myAnt.Ants_coupling_exp accumulate each.getAnts();
		aa <- myAnt.Ants_coupling_exp accumulate each.getAnts();
		ll <- myP.PreyPredator_coupling_exp accumulate each.getPrey();


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

		loop i from: 0 to: length(aa) - 1
		{
			if (!dead(ll at i) and !dead(aa at i))
			{
				if (!(ll at i).is_chased)
				{
					(ll at i).location <- (aa at i).location;
				} else
				{
					(aa at i).location <- (ll at i).location;
				}

			} else
			{
				ask (aa at i)
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