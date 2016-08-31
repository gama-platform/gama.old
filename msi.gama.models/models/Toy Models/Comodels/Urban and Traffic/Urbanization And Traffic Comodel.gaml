/**
* Name: urbanization_and_traffic_comodeling
* Author: HUYNH Quang Nghi
* Description: Co-model example : coupling urban growth model with the traffic model. 
* The speed of people on the road will be effected by the urbanization, more people in the region, the lower speed they move.
* And the greater urbanization the faster developping of roads.
* Tags: comodel
 */
model urbanization_and_traffic_comodeling

import "Adapters/Traffic Adapter.gaml" as Traffic
import "Adapters/Urban Adapter.gaml" as Urbanization


global
{
//set the bound of the world
	geometry shape <- envelope(shape_file("../../../Toy Models/Traffic/includes/roads.shp"));
	float step<-#day;
	float road_develop_speed <- 1.1;

	init
	{
		//create Traffic micro-model's experiment
		create Traffic."Adapter";
		//create Urban micro-model;s experiment
		create Urbanization."Adapter"
		{
			do transform;
		}

	}

	reflex simulate_micro_models
	{
		//ask simulation of micro-model step one
		ask Traffic."Adapter" collect each.simulation
		{
			do _step_;
		}

		// tell the urban to evolve and interract with the traffic every 365 step = 1 year
		if(cycle mod 365 = 0 ){			
			loop r over: Traffic."Adapter"[0].simulation.road
			{
				// compute the cell overlaps the road, which means the size of population
				list l <- Urbanization."Adapter"[0].simulation.plot where (each.grid_value = 1.0 and each overlaps r);
				if (length(l) > 0)
				{
					// adding the population to the variable of the road. It will be recompute the speed in the next step
					r.nb_people <- r.nb_people + length(l);
					if (r.nb_people > 300)
					{
						r.shape <- r.shape * road_develop_speed;
					}
	
				}
	
			}
			
			// tell the urban to grow up 
			ask Urbanization."Adapter" collect each.simulation
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
				loop p over: Urbanization."Adapter"[0].simulation.plot
				{
					draw square(20) color: p.color at: p.location;
				}

			}

			agents "building" 	value: Traffic."Adapter"[0].get_building();
			agents "people" 	value: Traffic."Adapter"[0].get_people();
			agents "road" 		value: Traffic."Adapter"[0].get_road();
		}

	}

}
