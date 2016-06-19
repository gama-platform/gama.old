/**
* Name: Comodel of Flood and Evacuation model
* Author: HUYNH Quang Nghi
* Description: Co-model example : couple the evacuation model with the flood model. Water win or human win?
* Tags: comodel
 */
model flood_evacuation_comodeling
 
import "The Couplings/Flood Coupling.gaml" as Flooding 
import "The Couplings/Evacuation Coupling.gaml" as Evacuation


global
{
	//set the bound of environment
	geometry shape <- envelope(file("../../../Toy Models/Flood Simulation/includes/mnt50.asc"));
	//counting variable of casualty
	int casualty <- 0;
	init
	{
		//create experiment from micro-model myFlood with corresponding parameters
		create Flooding."Coupling Experiment";
	
		//create the Evacuation micro-model's experiment
		create Evacuation."Coupling Experiment" 
		{
			//transform the environment and the agents to new location (near the river)
			do transform_environment;
		}

	}

	reflex doing_cosimulation
	{
		//do a step of Flooding
		ask Flooding."Coupling Experiment" collect each.simulation
		{
			do _step_;
		}

		//people evacate 
		ask Evacuation."Coupling Experiment" collect each.simulation
		{
			//depending on the real plan of evacuation, we can test the speed of the evacuation with the speed of flooding by doing more or less simulation step 
				do _step_;
		}

		//loop over the population
		loop thePeople over: first(Evacuation."Coupling Experiment").get_people()
		{
			//get the cell at people's location
			cell theWater <- cell(first(Flooding."Coupling Experiment").get_cell_at(thePeople));
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
			agents "building" value: first(Evacuation."Coupling Experiment").get_building();
			agents "people" value: first(Evacuation."Coupling Experiment").get_people();
			graphics "exit" {
				draw "EXIT" at: first(Evacuation."Coupling Experiment").simulation.target_point-110;
				draw sphere(100) at: first(Evacuation."Coupling Experiment").simulation.target_point color: #green;	
			}
			agents "cell" value: first(Flooding."Coupling Experiment").get_cell();
			agents "dyke" value: first(Flooding."Coupling Experiment").get_dyke();
			graphics 'CasualtyView'
			{
				draw ('Casualty: ' + casualty) at: { 0, 4000 } font: font("Arial", 18, # bold) color: # red;
			}

		}

	}

}
