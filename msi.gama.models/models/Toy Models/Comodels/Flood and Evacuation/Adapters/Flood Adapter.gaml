/**
* Name: flood_adapter
* Author: HUYNH Quang Nghi
* Description: It is NOT supposed to launch. This is the coupling of Flood model. It is used in the "Flood and Evacuation" as an interface. 
* Tags: comodel
*/
model flood_adapter

import "../../../../Toy Models/Flood Simulation/models/Hydrological Model.gaml"
experiment "Adapter" type: gui
{
	point newSize <- { 0.07, 0.07 };
	cell get_cell_at (agent p)
	{	 
		using topology(cell) {
			return first(cell overlapping p);
		} 
	}

	list<cell> get_cell
	{
		return list(cell) where (each.grid_value > 8.0);
	}

	list<buildings> get_buildings
	{
		return list(buildings);
	}

	list<dyke> get_dyke
	{
		return list(dyke);
	}

	output
	{
	}

}