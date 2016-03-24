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
	list<building> getBuilding
	{
		return list(building);
	}

	list<people> getPeople
	{
		return list(people);
	}
	output{}

}