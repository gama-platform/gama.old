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
//	geometry shape <- envelope(shape_file("../../../Toy Models/Traffic/includes/roads.shp"));
	geometry shape<-envelope(grid_file("../../../../Toy Models/Urban Growth/includes/cantho_1999_v6.asc"));
	float step<-#day;
	float road_develop_speed <- 1.1;
	int threshold_number_people<-50;

	init
	{
		//create Traffic micro-model's experiment
		create Traffic."Adapter of Traffice"{
			do transform;
		}
		//create Urban micro-model;s experiment
		create Urbanization."Adapter" ;

	}

	reflex simulate_micro_models
	{
		//ask simulation of micro-model step one
		ask Traffic."Adapter of Traffice" collect each.simulation
		{
			do _step_;
		}

		// tell the urban to evolve and interract with the traffic every 30 step = 1 month
		if(cycle mod 30 = 0 ){			
//			// tell the urban to grow up 
			ask Urbanization."Adapter" collect each.simulation
			{
				do _step_;
			}
			loop r over: Traffic."Adapter of Traffice"[0].simulation.road
			{
				// compute the cell overlaps the road, which means the size of population
				list l <- Urbanization."Adapter"[0].simulation.plot where (each.grid_value = 1.0 and each overlaps r);
				if (length(l) > 0)
				{
					// adding the population to the variable of the road. It will be recompute the speed in the next step
					r.nb_people <- r.nb_people + length(l);
					if (r.nb_people > threshold_number_people)
					{
						//we build a random road with hoping to solve the traffic jam
						do build_a_new_road;
					}
	
				}
	
			}
			
			
		}
		

	}
	
	action build_a_new_road
	{
		road r1 <- any(Traffic."Adapter of Traffice"[0].simulation.road);
		road r2 <- any(Traffic."Adapter of Traffice"[0].simulation.road);
		point p1 <- any_point_in(r1.shape);
		point p2 <- any_point_in(r2.shape);
		geometry newroad <- line([p1, p2]);
		list<geometry> nr <- [];
		list<point> i1 <- [p1, p2];
		list rrr <- (Traffic."Adapter of Traffice"[0].simulation.road) sort_by (each distance_to p1);
		loop i from: 0 to: length(rrr) - 1
		{
			if (newroad intersects rrr[i])
			{
				point t <- (newroad intersection rrr[i]).location;
				if (t != nil)
				{
					i1 <+ t;
					list s <- rrr[i].shape split_at t;
					if (length(s) > 1)
					{
						rrr[i].shape <- s[0];
						ask Traffic."Adapter of Traffice"[0].simulation
						{
							create road from: list(s[1]);
						}

					}

				}

			}

		}

		i1 <- i1 sort_by (each distance_to p1);
		loop i from: 0 to: length(i1) - 2
		{
			nr <+ line([i1[i], i1[i + 1]]);
			ask Traffic."Adapter of Traffice"[0].simulation
			{
				road_network << edge(i1[i], i1[i + 1]);
			}

		}

		loop ee over: nr
		{
			if(ee!=nil){
				
			ask Traffic."Adapter of Traffice"[0].simulation
			{
				create road from: list(ee)
				{
					buffer<-100;
					shape <- ee; //scaled_by 0.7;
				}
			}
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
			agents "cell" value: (Urbanization."Adapter"[0]).get_plot() transparency:0.75;

			agents "road" 		value: Traffic."Adapter of Traffice"[0].get_road();
			agents "building" 	value: Traffic."Adapter of Traffice"[0].get_building();
			agents "people" 	value: Traffic."Adapter of Traffice"[0].get_people() aspect:default;
		}

	}

}
