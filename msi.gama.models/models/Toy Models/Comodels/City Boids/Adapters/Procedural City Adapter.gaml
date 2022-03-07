/**
* Name: city_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Procedural City model. It is used in the "City Boids" as an interface. 
* Tags: comodel
*/
model city_adapter

import "../../../../Visualization and User Interaction/Visualization/3D Visualization/models/Procedural City.gaml"

experiment "Adapter" type:gui  {
	

	
	Building get_building_at (geometry p)
	{
		ask simulation
		{
			return Building closest_to p;
		}

	}
	
	list<Building> get_building{
		return list(Building);
	}	
	
	output{
	}
}  