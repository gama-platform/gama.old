/**
* Name: Evacuation_coupling
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Evacuation model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/
model Evacuation_coupling

import "../../../Toy Models/Evacuation/models/continuous_move.gaml"
experiment Evacuation_coupling_exp type: gui parent: main
{
	point centroid <- { 0, 180 };
	list<building> getBuilding
	{
		return list(building);
	}

	action transform_environement
	{
		loop t over: list(building)
		{
			t.shape <- t.shape translated_by centroid;
			t.shape <- t.shape * 10;
			t.location <- t.location * 8;
		}

		loop t over: list(people)
		{
			t.speed <- 10;
			t.size <- 20;
			t.shape <- t.shape translated_by centroid;
			t.location <- t.location * 8;
		}

		target_point <- target_point translated_by centroid;
	}

	list<people> getPeople
	{
		return list(people);
	}

	output
	{
	}

}