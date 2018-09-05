/**
* Name: predator_prey_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Predator Prey model. It is used in the "Prey Sugarscaptor" as an interface. 
* Tags: comodel
*/
model predator_prey_adapter

import "../../../../Tutorials/Predator Prey/models/Model 06.gaml"

experiment Adapter2 type: gui
{
	point centroid <- { 0, 100 };
 
	action transform_environment
	{
		loop t over: list(prey)
		{
			ask t
			{
				shape <- shape translated_by myself.centroid;
			}
		}
		
		loop t over: vegetation_cell
		{
			ask t
			{
				shape <- shape translated_by myself.centroid;
			}

		}

	}

	output
	{
	}

}