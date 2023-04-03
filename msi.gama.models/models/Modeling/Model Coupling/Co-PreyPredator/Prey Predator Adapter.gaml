/**
* Name: prey_predator_coupling
* Author: HUYNH Quang Nghi
* Description: This is the coupling of Prey Predator  model. It is supposed to use in the Comodeling Example as an interface. 
* Tags: comodel
*/
model prey_predator_coupling

import "Prey Predator.gaml"


global
{
}

experiment Simple type: gui
{
	geometry shape <- square(100);
	list<prey> get_prey
	{
		return list(prey);
	}

	list<predator> get_predator
	{
		return list(predator);
	}

	//if we redefine the output, i.e, a blank output, the displays in parent experiment don't show.
	output
	{
	}

}


