model traffic_coupling

import "../../../../Toy Models/Traffic/models/Simple traffic model.gaml"
experiment traffic type: gui
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