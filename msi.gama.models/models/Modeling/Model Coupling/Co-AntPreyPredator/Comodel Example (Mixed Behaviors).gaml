/**
* Name: comodel_mix_behaviors
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the mixing behaviors of preyPredator with the Ants. Ants are the prey, fleeing from Predators, when they are not chasing, they try to do job of the ants.
* Tags: comodel
*/
model comodel_mix_behaviors

import "../Co-PreyPredator/Prey Predator Adapter.gaml" as Organism
import "Ants Adapter.gaml" as Ant


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
		create Ant.Base with: [gridsize::100,ants_number::500]{
			write self;
		}
		
//		write Ant.Simple collect each.simulations as list;
		//create the PreyPredator micro-model with the parameters and the number of the prey is equal with the size of ants population
		create Organism.Simple with: [shape::square(100), preyinit::Ant.Base[0].simulation.ants_number, predatorinit::2]  
		{
			// set the size of micro-model PreyPredator equal with the size of the grid of myAnt
			shape <- square(100);
		}

		write  Organism.Simple as list;
		// save the original population of the Ants and the Preys
		theAnts <- Ant.Base accumulate each.get_ants();
		thePreys <- list<prey>(Organism.Simple accumulate each.get_prey());


	}

	reflex simulate_micro_models
	{
		// ask myAnt do a step
		ask (Ant.Base collect each.simulation)
		{
			do _step_;
		}
		// ask myPreyPredator do a step, too
		ask (Organism.Simple collect each.simulation)
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

experiment main type: gui
{
	output synchronized:true
	{
		display "Comodel display"
		{
			agents "ant_grid" value: Ant.Base accumulate each.get_ant_grid() transparency: 0.7;			
			agents "agentprey" value: (Organism.Simple accumulate each.get_prey());
			agents "agentpredator" value: (Organism.Simple accumulate each.get_predator());
		}

	}

}