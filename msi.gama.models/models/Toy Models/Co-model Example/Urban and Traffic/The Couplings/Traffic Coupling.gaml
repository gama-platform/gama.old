/**
* Name: traffic_coupling
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Traffic  model. It is used in the "Urban and Traffic" as an interface. 
* Tags: comodel
*/
model traffic_coupling

import "../../../../Toy Models/Traffic/models/Simple traffic model.gaml"
experiment "Coupling Experiment" type: gui
{
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