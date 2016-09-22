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
	point centroid <- { 200, 580 };
	list<building> get_building
	{
		return list(building);
	}

	action transform_environment
	{
		
		people_size<-people_size*10;
		free_space<-(free_space*10);
		
//		free_space <- copy(world.shape);
		
		loop t over: list(building)
		{
//			t.shape <- t.shape + {10,50};
			t.shape <- t.shape * 10;
				t.location <- (t.location * 10) + centroid;
//							free_space <- free_space - (t.shape + people_size);
				
		}
//				free_space <- free_space simplification(1.0);
		
		loop t over: list(people)
		{
				
				t.speed <- t.speed*10;
				t.size <- t.size * 10;
//				t.shape <- t.shape translated_by centroid;
			t.shape <- t.shape * 10;

				t.location <- (t.location * 10) + centroid;
				t.target_loc <-  target_point;
			
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