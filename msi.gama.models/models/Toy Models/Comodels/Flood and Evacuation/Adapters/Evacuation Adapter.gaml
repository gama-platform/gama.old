/**
* Name: evacuation_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Evacuation model. It is used in the "Flood and Evacuation" as an interface. 
* Tags: comodel
*/
model evacuation_adapter

import "../../../../Toy Models/Evacuation/models/Continuous Move.gaml"
experiment "Adapter of Evacuation" type: gui
{
	point centroid <- { 200, 580 };
	list<building> get_building
	{
		return list(building);
	}

	action transform_environment
	{
		people_size <- people_size * 10;
		loop t over: list(building)
		{
			t.shape <- t.shape * 20;
			t.location <- (t.location * 10) + centroid;
		}

		loop t over: list(people)
		{
			t.speed <- t.speed * 10;
			t.size <- t.size * 10;
			t.location <- (t.location * 10) + centroid;
			t.shape<-(square(1) at_location t.location); 
			t.target_loc <- target_point;
		}

	}

	list<people> get_people
	{
		return list(people);
	}

	output
	{
	}

}