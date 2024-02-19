/**
* Name: Comodel of Flood and Evacuation model
* Author: HUYNH Quang Nghi
* Description: Co-model example : couple the evacuation model with the flood model. Water win or human win?
* Tags: comodel
 */
model flood_evacuation_comodeling
 
import "Adapters/Flood Adapter.gaml" as Flooding 
import "Adapters/Evacuation Adapter.gaml" as Evacuation


global
{
	//set the bound of the environment
	geometry shape <- envelope(file("../../../Toy Models/Flood Simulation/includes/mnt50.asc"));
	geometry the_free_shape<-copy(shape);
	//counter for casualties
	int casualty <- 0;
	
	list<point> offset <- [{ 50, 1700 }, { 800, 3400 }, { 2900, 0 }, { 4200, 2900 }, { 5100, 1300 }];
	list<point> exits <- [{250, 1600 }, { 400, 4400 }, { 4100, 1900 }, { 6100, 2900 }, { 5700, 900 }];
	
	init
	{
		//create experiment from micro-model myFlood with corresponding parameters
		create Flooding."Adapter";
	
		//create the Evacuation micro-model's experiment
		create Evacuation."Adapter of Evacuation" number:length(offset);// with:[nb_people::1];
		ask Evacuation."Adapter of Evacuation"
		{
			centroid <- myself.offset[int(self)];
			target_point <- myself.exits[int(self)];
			//transform the environment and the agents to new location (near the river)
			do transform_environment;
			loop t over: list(building)
			{
				the_free_shape <- myself.the_free_shape - (t.shape+ people_size);
			}
			
			free_space<-copy(myself.the_free_shape);			
			free_space <- free_space simplification(1.0);
		}

	}

	reflex doing_cosimulation
	{
		//do a step of Flooding
		ask Flooding."Adapter" collect each.simulation
		{
			do _step_;
		}

		//evacuate people
		ask Evacuation."Adapter of Evacuation" collect each.simulation
		{
			//depending on the real plan of evacuation, we can test the speed of the evacuation with the flooding speed by doing more or less simulation steps 
			do _step_;
		}
		
		//loop over the population
		loop thePeople over: Evacuation."Adapter of Evacuation"  accumulate each.get_people()
		{
			//get the cell at people's location 
			cell theWater <- cell(first(Flooding."Adapter").get_cell_at(thePeople));
			//if the water level is higher than 8 meters and the cell overlaps people, kill the people
			if (theWater.grid_value > 8.0)
			{
				ask thePeople
				{
					do die;
				}
				//increase the counting variable
				casualty <- casualty + 1;
			}

		}

	}

}

experiment simple type: gui
{
	output synchronized: true
	{
		display "Comodel Display"  type:3d
		{
			agents "building" value: Evacuation."Adapter of Evacuation"  accumulate each.get_building();
			agents "people" value:  Evacuation."Adapter of Evacuation"  accumulate each.get_people();
			graphics "exits" refresh:false{
				loop e over: exits
				{
					draw sphere(100) at: e color: # green;
				}

			}
			agents "cell" value: first(Flooding."Adapter").get_cell();
			agents "cell" value: first(Flooding."Adapter").get_buildings()  aspect: geometry;
			agents "dyke" value: first(Flooding."Adapter").get_dyke() aspect: geometry ;
			graphics 'CasualtyView' 
			{
				draw ('Casualty: ' + casualty +"/"+sum(Evacuation."Adapter of Evacuation"  accumulate (each.simulation.nb_people))) at: { 1000, 5200 } font: font("Arial", 24, # bold) color: # red;
			}
		}

	}

}
