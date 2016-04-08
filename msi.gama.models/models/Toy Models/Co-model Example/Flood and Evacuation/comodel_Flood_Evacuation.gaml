/**
* Name: Comodel of Flood and Evacuation model
* Author: HUYNH Quang Nghi
* Description: Co-model example : couple the evacuation model with the flood model. Water win or human win?
* Tags: comodel
 */
model comodel_Flood_Evacuation

import "Flood_coupling.gaml" as myFlood
import "Evacuation_coupling.gaml" as myEvacuation


global
{
	geometry shape <- envelope(file("../../../Toy Models/Flood Simulation/includes/mnt50.asc"));
	int casualty <- 0;
	init
	{
		create myFlood.Flood_coupling_exp with:
		[buildings_shapefile::file("../../../Toy Models/Flood Simulation/includes/Building.shp"), river_shapefile::file("../../../Toy Models/Flood Simulation/includes/RedRiver.shp"), dykes_shapefile::file("../../../Toy Models/Flood Simulation/includes/Dykes.shp"), dem_file::file("../../../Toy Models/Flood Simulation/includes/mnt50.asc")];
		create myEvacuation.Evacuation_coupling_exp with: [nb_people::200, target_point::{ 0, 1580 }, building_shapefile::file("../../../Toy Models/Evacuation/includes/building.shp")]
		{
			do transform_environement;
		}

	}

	reflex doing_cosimulation
	{
		ask myFlood.Flood_coupling_exp collect each.simulation
		{
			do _step_;
		}

		ask myEvacuation.Evacuation_coupling_exp collect each.simulation
		{
			if (cycle mod 1 = 0)
			{
				do _step_;
			}

		}

		loop thePeople over: first(myEvacuation.Evacuation_coupling_exp).getPeople()
		{
			cell theWater <- cell(first(myFlood.Flood_coupling_exp).getCellAt(thePeople));
			if (theWater.grid_value > 8.0 and theWater overlaps thePeople)
			{
				ask thePeople
				{
					do die;
				}

				casualty <- casualty + 1;
			}

		}

	}

}

experiment comodel_Flood_Evacuation_exp type: gui
{
	output
	{
		display "comodel_disp"
		{
			agents "building" value: first(myEvacuation.Evacuation_coupling_exp).getBuilding();
			agents "people" value: first(myEvacuation.Evacuation_coupling_exp).getPeople();
			agents "cell" value: first(myFlood.Flood_coupling_exp).getCell();
			agents "dyke" value: first(myFlood.Flood_coupling_exp).getDyke();
			graphics 'CasualtyView'
			{
				draw ('Casualty: ' + casualty) at: { 0, 4000 } font: font("Arial", 18, # bold) color: # red;
			}

		}

	}

}
