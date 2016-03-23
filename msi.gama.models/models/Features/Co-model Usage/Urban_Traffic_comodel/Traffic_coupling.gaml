/**
* Name: AntsIF
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Traffic model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/
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