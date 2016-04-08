/**
* Name: ComodelAnts
* Author:  HUYNH Quang Nghi
* Description: Co-model example : coupling Ants and Boids. In an experimental use case, Boids chase and eat Ants when Ants are trying to fill-up their nids.
* Tags: comodel
 */
model comodelAnts

// Import the Ant model with an alias name, this alias name will be used as micro-model identifier in co-model.
import "Ants_coupling.gaml" as myAnt


global
{
	geometry shape <- envelope(square(100));
	init
	{
		create myAnt.Ants_coupling_exp with: [gridsize::100, ants_number::10] number: 2;
	}

	reflex dododo
	{
		ask (myAnt.Ants_coupling_exp collect each.simulation)
		{
			do _step_;
		}

	}

}

experiment comodelAnts_exp type: gui
{
	output
	{
		display "comodel_disp"
		{ 
			image 'background' file: '../images/soil.jpg';
			agents "Ss" value:first(myAnt.Ants_coupling_exp).getAnts() ;
		}

	}

}
