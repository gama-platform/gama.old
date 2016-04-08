model Flood_coupling

import "../../../Toy Models/Flood Simulation/models/Hydrological Model.gaml"
experiment Flood_coupling_exp type: gui parent: main_gui
{
	point newSize <- { 0.07, 0.07 };
	cell getCellAt (geometry p)
	{
		ask simulation
		{
			return cell closest_to p;
		}

	}

	list<cell> getCell
	{
		return list(cell) where (each.grid_value > 8.0);
	}

	list<buildings> getBuildings
	{
		return list(buildings);
	}

	list<dyke> getDyke
	{
		return list(dyke);
	}

	output
	{
	}

}