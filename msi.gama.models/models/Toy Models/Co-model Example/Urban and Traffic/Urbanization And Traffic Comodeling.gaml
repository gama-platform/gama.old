/**
* Name: urbanization_and_traffic_comodeling
* Author: HUYNH Quang Nghi
* Description: Co-model example : coupling urban growth model with the traffic model. The speed of people on the road will be effected by the urbanization, more people in the region, the lower speed they move.
* Tags: comodel
 */
model urbanization_and_traffic_comodeling

import "The Couplings/Traffic Coupling.gaml" as myTraffic
import "The Couplings/Urban Coupling.gaml" as myUrban


global
{
//set the bound of the world
	geometry shape <- envelope(shape_file("../../../Toy Models/Traffic/includes/roads.shp"));
	//	geometry shape <- envelope(grid_file("../../../Toy Models/Urban Growth/includes/cantho_1999_v6.asc"));
	init
	{
	//create Traffic micro-model's experiment
		create myTraffic.traffic with:
		[building_shapefile::file("../../../Toy Models/Traffic/includes/buildings.shp"), road_shapefile::file("../../../Toy Models/Traffic/includes/roads.shp")];
		//create Urban micro-model;s experiment
		create myUrban.raster with:
		[shape::envelope(shape_file("../../../Toy Models/Traffic/includes/roads.shp")), asc_grid::grid_file("../../../Toy Models/Urban Growth/includes/cantho_1999_v6.asc"), road_shapefile::shape_file("../../../Toy Models/Urban Growth/includes/roads15_3.shp"), city_center_shapefile::shape_file("../../../Toy Models/Urban Growth/includes/city center.shp")]
		{
			do transform;
		}

	}

	reflex simulate_micro_models
	{
		
		

		loop r over: myTraffic.traffic[0].simulation.road	
		{
			// compute the cell overlaps the road, which means the size of population
					list l<- myUrban.raster[0].simulation.plot where (each.grid_value = 1.0  and each overlaps r);
					if(length(l)>0){
						// adding the population to the variable of the road. It will be recompute the speed in the next step
						r.nb_people<-r.nb_people + length(l);
					}
		}
	//ask simulation of micro-model step one
		ask myTraffic.traffic collect each.simulation
		{
			do _step_;
		}

		// tell the urban to grow up every 200 step
		ask myUrban.raster collect each.simulation
		{
			if (cycle mod 20 = 0)
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
			graphics "Plotgrid"
			{
				loop p over: myUrban.raster[0].simulation.plot
				{
					draw square(20) color: p.color at: p.location;
				}

			}
			//size: { 0.037, 0.052 };
			agents "building" value: first(myTraffic.traffic).get_building();
			agents "people" value: first(myTraffic.traffic).get_people();
			agents "road" value: first(myTraffic.traffic).get_road();
		}

	}

}
