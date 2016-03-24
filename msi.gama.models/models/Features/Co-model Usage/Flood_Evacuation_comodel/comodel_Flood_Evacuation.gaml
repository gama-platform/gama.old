/**
* Name: ComodelAnts
* Author: hqnghi
* Description: Co-model example : couple the evacuation model with the flood model. Water win or human win?
* Tags: comodel
 */
model comodel_Flood_Evacuation

import "Flood_coupling.gaml" as myFlood
import "Evacuation_coupling.gaml" as myEvacuation


global
{  
	geometry shape<-envelope(square(400));
	init
	{
		create myFlood.Flood_coupling_exp with:
		[buildings_shapefile::"../../../Toy Models/Flood Simulation/includes/Building.shp", river_shapefile::file("../../../Toy Models/Flood Simulation/includes/RedRiver.shp"), dykes_shapefile::file("../../../Toy Models/Flood Simulation/includes/Dykes.shp"), dem_file::file("../../../Toy Models/Flood Simulation/includes/mnt50.asc")];
		create myEvacuation.Evacuation_coupling_exp with: [target_point::{0,world.location.y},building_shapefile::file("../../../Toy Models/Evacuation/includes/building.shp")];
		create myEvacuation.Evacuation_coupling_exp with: [target_point::{world.location.x,0},building_shapefile::file("../../../Toy Models/Evacuation/includes/building.shp")];
	}

	reflex dododo
	{
		ask myFlood.Flood_coupling_exp collect each.simulation
		{
			do _step_;
		}

		ask myEvacuation.Evacuation_coupling_exp collect each.simulation
		{
			if (cycle mod 3 = 0)
			{
				do _step_;
			}

		}

	}

}

experiment comodel_Urban_Traffic_exp type: gui
{
	output
	{
		display "comodel_disp" type: opengl
		{
			agents "building" value: first(myEvacuation.Evacuation_coupling_exp).getBuilding()  position:{0,180};
			agents "people" value: first(myEvacuation.Evacuation_coupling_exp).getPeople()  position:{0,180};
			
			agents "building" value: last(myEvacuation.Evacuation_coupling_exp).getBuilding()  position:{250,0};
			agents "people" value: last(myEvacuation.Evacuation_coupling_exp).getPeople()  position:{250,0};
			
				
			
			agents "cell" value: first(myFlood.Flood_coupling_exp).getCell()  size:{0.07,0.07} ;
			agents "dyke" value: first(myFlood.Flood_coupling_exp).getDyke()  size:{0.07,0.07};


		}

	}

}
