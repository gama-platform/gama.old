/**
* Name: Flood_coupling
* Author: Lô
* Description: This is the coupling that manipulate the elements inside Flood model and propose the function would be used outside.
* Tags: Tag1, Tag2, TagN
*/
model Flood_coupling

import "../../../Toy Models/Flood Simulation/models/Hydrological Model.gaml"
experiment Flood_coupling_exp type: gui parent: main_gui
{
	list<cell> getCell
	{
		return list(cell);
	}

	list<buildings> getBuildings
	{
		return list(buildings); 
	}

	list<dyke> getDyke
	{
		return list(dyke);
	}
output{}
}