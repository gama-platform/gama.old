/**
* Name: urbanization_and_traffic_comodeling
* Author: HUYNH Quang Nghi
* Description: Co-model example : coupling urban growth model with the traffic model. 
* The speed of people on the road will be effected by the urbanization, more people in the region, the lower speed they move.
* And the greater urbanization the faster developping of roads.
* Tags: comodel
 */
model urbanization_and_traffic_comodeling

import "../The Couplings/Traffic Coupling.gaml" as myTraffic
import "../The Couplings/Urban Coupling.gaml" as myUrban


global
{
//set the bound of the world
	geometry shape <- envelope(shape_file("../../../Toy Models/Traffic/includes/roads.shp"));
	float road_develop_speed <- 1.1;
	//	geometry shape <- envelope(grid_file("../../../Toy Models/Urban Growth/includes/cantho_1999_v6.asc"));
	init
	{
	//create Traffic micro-model's experiment
		create myTraffic.traffic;
		//create Urban micro-model;s experiment
		create myUrban.raster
		{
			do transform;
		}

	}

	reflex simulate_micro_models
	{
		loop r over: myTraffic.traffic[0].simulation.road
		{
		// compute the cell overlaps the road, which means the size of population
			list l <- myUrban.raster[0].simulation.plot where (each.grid_value = 1.0 and each overlaps r);
			if (length(l) > 0)
			{
			// adding the population to the variable of the road. It will be recompute the speed in the next step
				r.nb_people <- r.nb_people + length(l);
				//						write r.nb_people;
				if (r.nb_people > 300)
				{
					r.shape <- r.shape * road_develop_speed;
				}

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
			do _step_;
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

			agents "building" value: first(myTraffic.traffic).get_building();
			agents "people" value: first(myTraffic.traffic).get_people();
			agents "road" value: first(myTraffic.traffic).get_road();
		}

	}

}
