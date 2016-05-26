/**
* Name: Comodel of Flood and Evacuation model
* Author: HUYNH Quang Nghi
* Description: Co-model example : couple the evacuation model with the flood model. Water win or human win?
* Tags: comodel
 */
model flood_evacuation_comodeling
 
import "The Couplings/Flood Coupling.gaml" as myFlood 
import "The Couplings/Evacuation Coupling.gaml" as myEvacuation


global
{
	//set the bound of environment
	geometry shape <- envelope(file("../../../Toy Models/Flood Simulation/includes/mnt50.asc"));
	//counting variable of casualty
	int casualty <- 0;
	init
	{
		//create experiment from micro-model myFlood with corresponding parameters
		create myFlood.FloodCouplingExperiment with:
		[buildings_shapefile::file("../../../Toy Models/Flood Simulation/includes/Building.shp"), river_shapefile::file("../../../Toy Models/Flood Simulation/includes/RedRiver.shp"), dykes_shapefile::file("../../../Toy Models/Flood Simulation/includes/Dykes.shp"), dem_file::file("../../../Toy Models/Flood Simulation/includes/mnt50.asc")];
		//create the Evacuation micro-model's experiment
		create myEvacuation.EvacuationCouplingExperiment with: [nb_people::200, target_point::{ 0, 1580 }, building_shapefile::file("../../../Toy Models/Evacuation/includes/building.shp")]
		{
			//transform the environment and the agents to new location (near the river)
			do transform_environment;
		}

	}

	reflex doing_cosimulation
	{
		//do a step of Flooding
		ask myFlood.FloodCouplingExperiment collect each.simulation
		{
			do _step_;
		}

		//people evacate 
		ask myEvacuation.EvacuationCouplingExperiment collect each.simulation
		{
			//depending on the real plan of evacuation, we can test the speed of the evacuation with the speed of flooding by doing more or less simulation step 
				do _step_;
		}

		//loop over the population
		loop thePeople over: first(myEvacuation.EvacuationCouplingExperiment).get_people()
		{
			//get the cell at people's location
			cell theWater <- cell(first(myFlood.FloodCouplingExperiment).get_cell_at(thePeople));
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

experiment comodel_Flood_Evacuation_exp type: gui
{
	output
	{
		display "comodel_disp"
		{
			agents "building" value: first(myEvacuation.EvacuationCouplingExperiment).get_building();
			agents "people" value: first(myEvacuation.EvacuationCouplingExperiment).get_people();
			agents "cell" value: first(myFlood.FloodCouplingExperiment).get_cell();
			agents "dyke" value: first(myFlood.FloodCouplingExperiment).get_dyke();
			graphics 'CasualtyView'
			{
				draw ('Casualty: ' + casualty) at: { 0, 4000 } font: font("Arial", 18, # bold) color: # red;
			}

		}

	}

}
