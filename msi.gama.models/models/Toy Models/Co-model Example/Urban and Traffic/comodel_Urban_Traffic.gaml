/**
* Name: comodel Urban and Traffic model
* Author: HUYNH Quang Nghi
* Description: Co-model example : coupling urban growth model with the traffic model.
* Tags: comodel
 */
model comodel_Urban_Traffic

import "Traffic_coupling.gaml" as myTraffic
import "Urban_coupling.gaml" as myUrban


global
{
	//set the bound of the world
	geometry shape <- envelope(shape_file("../../../Toy Models/Traffic/includes/roads.shp"));
	init
	{
		//create Traffic micro-model's experiment
		create myTraffic.Traffic_coupling_exp with:
		[building_shapefile::file("../../../Toy Models/Traffic/includes/buildings.shp"), road_shapefile::file("../../../Toy Models/Traffic/includes/roads.shp")];
		//create Urban micro-model;s experiment
		create myUrban.Urban_coupling_exp with:
		[asc_grid::grid_file("../../../Toy Models/Urban Growth/includes/cantho_1999_v6.asc"), road_shapefile::shape_file("../../../Toy Models/Urban Growth/includes/roads15_3.shp"), city_center_shapefile::shape_file("../../../Toy Models/Urban Growth/includes/city center.shp")];
	}

	reflex simulate_micro_models
	{
		//ask simulation of micro-model step one
		ask myTraffic.Traffic_coupling_exp collect each.simulation
		{
			do _step_;
		}

		// tell the urban to grow up every 200 step
		ask myUrban.Urban_coupling_exp collect each.simulation
		{
			if (cycle mod 200 = 0)
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
		display "comodel_disp"
		{
			agents "Plotgrid" value: first(myUrban.Urban_coupling_exp).getPlot() size: { 0.037, 0.052 };
			agents "building" value: first(myTraffic.Traffic_coupling_exp).getBuilding();
			agents "people" value: first(myTraffic.Traffic_coupling_exp).getPeople();
			agents "road" value: first(myTraffic.Traffic_coupling_exp).getRoad();
		}

	}

}
