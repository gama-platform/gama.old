/**
* Name: traffic_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Traffic  model. It is used in the "Urban and Traffic" as an interface. 
* Tags: comodel
*/
model traffic_adapter

import "../../../../Toy Models/Traffic/models/Traffic and Pollution.gaml"
experiment "Adapter of Traffice" type: gui
{
	point centroid <- { 15200, 1580 };
	action transform{		
		loop t over:list(road){			
			t.shape <- (t.shape * 20 );
			t.location <- (t.location * 20) + centroid;
			t.buffer<-100;
		}
		loop t over: list(building)
		{
			t.shape <- t.shape * 20;
			t.location <- (t.location * 20) + centroid;
		}

		loop t over: list(people)
		{
			t.speed <- t.speed / 50;
			t.shape <- t.shape * 40;
			t.location <- any_location_in(one_of(building));
			t.target <- any_location_in(one_of(building));
		}
		
	}
	list<building> get_building
	{
		return list(building);
	}

	list<people> get_people
	{
		return list(people);
	}

	list<road> get_road
	{
		return list(road);
	}

}