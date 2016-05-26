model evacuation_coupling

import "../../../../Toy Models/Evacuation/models/Continuous Move.gaml"
experiment EvacuationCouplingExperiment type: gui parent: main
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

		target_point <- point(target_point translated_by centroid);
	}

	list<people> get_people
	{
		return list(people);
	}

	output
	{
	}

}