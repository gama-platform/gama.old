/**
* Name: evacuation_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Evacuation model. It is used in the "Flood and Evacuation" as an interface. 
* Tags: comodel
*/
model evacuation_adapter

import "../../../../Toy Models/Evacuation/models/Continuous Move.gaml"
experiment "Adapter" type: gui
{
	point centroid <- { 0, 180 };
	list<building> get_building
	{
		return list(building);
	}

	action transform_environment
	{
		loop t over: list(building)
		{
			t.shape <- t.shape translated_by centroid;
			t.shape <- t.shape * 10;
			t.location <- t.location * 8;
		}

		loop t over: list(people)
		{
			t.speed <- 10.0;
			t.size <- 20.0;
			t.shape <- t.shape translated_by centroid;
			t.location <- t.location * 8;
		}

		target_point <- { 99, 999 };
	}

	list<people> get_people
	{
		return list(people);
	}

	output
	{
	}

}