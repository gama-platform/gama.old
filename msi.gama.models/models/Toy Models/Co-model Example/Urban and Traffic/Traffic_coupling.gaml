model Traffic_coupling

import "../../../Toy Models/Traffic/models/Simple traffic model.gaml"
experiment Traffic_coupling_exp type: gui parent: trafic
{
	list<building> getBuilding
	{
		return list(building);
	}

	list<people> getPeople
	{
		return list(people);
	}

	list<road> getRoad
	{
		return list(road);
	}

}