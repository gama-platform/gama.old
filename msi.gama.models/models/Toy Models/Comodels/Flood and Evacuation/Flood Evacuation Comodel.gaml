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
	//set the bound of environment
	geometry shape <- envelope(file("../../../Toy Models/Flood Simulation/includes/mnt50.asc"));
	//counting variable of casualty
	int casualty <- 0;
	init
	{
		//create experiment from micro-model myFlood with corresponding parameters
		create Flooding."Adapter";
	
		//create the Evacuation micro-model's experiment
		create Evacuation."Adapter" 
		{
			//transform the environment and the agents to new location (near the river)
			do transform_environment;
		}

	}

	reflex doing_cosimulation
	{
		//do a step of Flooding
		ask Flooding."Adapter" collect each.simulation
		{
			do _step_;
		}

		//people evacate 
		ask Evacuation."Adapter" collect each.simulation
		{
			//depending on the real plan of evacuation, we can test the speed of the evacuation with the speed of flooding by doing more or less simulation step 
				do _step_;
		}

		//loop over the population
		loop thePeople over: first(Evacuation."Adapter").get_people()
		{
			//get the cell at people's location
			cell theWater <- cell(first(Flooding."Adapter").get_cell_at(thePeople));
			//if the water levele is high than 8 meters and people is overlapped, tell him that he must dead
			if (theWater.grid_value > 8.0 and theWater overlaps thePeople)
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
	output
	{
		display "Comodel Display"
		{
			agents "building" value: first(Evacuation."Adapter").get_building();
			agents "people" value: first(Evacuation."Adapter").get_people();
			graphics "exit" {
				draw "EXIT" at: first(Evacuation."Adapter").simulation.target_point-110;
				draw sphere(100) at: first(Evacuation."Adapter").simulation.target_point color: #green;	
			}
			agents "cell" value: first(Flooding."Adapter").get_cell();
			agents "dyke" value: first(Flooding."Adapter").get_dyke();
			graphics 'CasualtyView'
			{
				draw ('Casualty: ' + casualty) at: { 0, 4000 } font: font("Arial", 18, # bold) color: # red;
			}

		}

	}

}
