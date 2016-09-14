/**
* Name: sugarscape_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of SugarScape model. It is used in the "Prey Sugarscaptor" as an interface. 
* Tags: comodel
*/
model sugarscape_adapter

import "../../../../Toy Models/Sugarscape/models/Sugarscape.gaml"

experiment Adapter type: gui
{
	point centroid <- { 0, 100 }; 

	action transform_environment
	{
//		write centroid;
		
		loop t over: sugar_cell
		{
			ask t
			{
				shape <- shape translated_by myself.centroid;
				location <- location + myself.centroid;
			}

		}

		loop t over: animal
		{
			ask t 
			{
				shape <- shape translated_by myself.centroid;
				place <- one_of(sugar_cell);
				location <- place.location;
			}
		}
	}

	output
	{
	}

}