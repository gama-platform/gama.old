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
			t.speed <- 10.0;
			t.size <- 20.0;
			t.shape <- t.shape translated_by centroid;
			t.location <- t.location * 8;
		}

		target_point <- point(target_point translated_by centroid);
	}

	list<people> getPeople
	{
		return list(people);
	}

	output
	{
	}

}