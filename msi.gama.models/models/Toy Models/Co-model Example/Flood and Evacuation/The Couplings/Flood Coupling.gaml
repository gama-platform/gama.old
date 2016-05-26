model flood_coupling

import "../../../../Toy Models/Flood Simulation/models/Hydrological Model.gaml"
experiment main_gui type: gui
{
	point newSize <- { 0.07, 0.07 };
	cell get_cell_at (geometry p)
	{
		ask simulation
		{
			return cell closest_to p;
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