/**
* Name: Comodel_SIR_Switch
* Author: Lô
* Description: The coupling of  model m1.  
* Tags: Tag1, Tag2, TagN
*/
model PreyPredator_coupling
import "PreyPredator.gaml"

global
{	
}

experiment PreyPredator_coupling_exp parent:prey_predator_exp type: gui
{
geometry shape<- square(100);
	list<prey> getPrey{
		return list(prey);
	}
		list<predator> getPredator{
		return list(predator);
	}
	
	//if we redefine the output, i.e, a blank output, the displays in parent experiement dont show.
	output
	{
	}

}


