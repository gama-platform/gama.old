/**
* Name: urbanization_and_traffic_comodeling
* Author: HUYNH Quang Nghi
* Description: Co-model example : coupling urban growth model with the traffic model.
* Tags: comodel
 */
model urbanization_and_traffic_comodeling

import "The Couplings/Traffic Coupling.gaml" as myTraffic
import "The Couplings/Urban Coupling.gaml" as myUrban


global
{
	//set the bound of the world
	geometry shape <- envelope(shape_file("../../../Toy Models/Traffic/includes/roads.shp"));
	init
	{
		//create Traffic micro-model's experiment
		create myTraffic.traffic with:
		[building_shapefile::file("../../../Toy Models/Traffic/includes/buildings.shp"), road_shapefile::file("../../../Toy Models/Traffic/includes/roads.shp")];
		//create Urban micro-model;s experiment
		create myUrban.raster with:
		[asc_grid::grid_file("../../../Toy Models/Urban Growth/includes/cantho_1999_v6.asc"), road_shapefile::shape_file("../../../Toy Models/Urban Growth/includes/roads15_3.shp"), city_center_shapefile::shape_file("../../../Toy Models/Urban Growth/includes/city center.shp")];
	}

	reflex simulate_micro_models
	{
		//ask simulation of micro-model step one
		ask myTraffic.traffic collect each.simulation
		{
			do _step_;
		}

		// tell the urban to grow up every 200 step
		ask myUrban.raster collect each.simulation
		{
			if (cycle mod 200 = 0)
			{
				do _step_;
			}

		}

	}

}
experiment main type: gui
{
	output
	{
		display "Comodel Display"
		{
			agents "Plotgrid" value: first(myUrban.raster).get_plot() size: { 0.037, 0.052 };
			agents "building" value: first(myTraffic.traffic).get_building();
			agents "people" value: first(myTraffic.traffic).get_people();
			agents "road" value: first(myTraffic.traffic).get_road();
		}

	}

}
