/**
* Name: Comodel of Predator Prey and the SugarScape
* Author: HUYNH Quang Nghi
* Description: Co-model example : The Predator Prey and SugarScape are mixed into a common environment.
* Tags: comodel
 */
 model prey_sugarscaptor

import "Adapters/Predator Prey Adapter.gaml" as Preydator 
import "Adapters/Sugarscape Adapter.gaml" as Sugar

global
{  
	geometry shape <- square(200);
	int grid_size_Preydator<-2;
	int grid_size_Sugar<-4;
	list<point> offset_Preydator <- [{ 0, 0 }, { 0, 100 }, { 100, 0 }, { 100, 100 }];
	
	list<point> offset_Sugar <- [
										{ 0, 0 }, 			{ 0, 50 }, 		{ 0, 100 }, 			{ 0, 150 }, 
										{ 50, 0 }, 		{ 50, 50 }, 		{ 50, 100 }, 		{ 50, 150 }, 
										{ 100, 0 }, 		{ 100, 50 }, 	{ 100, 100 }, 		{ 100, 150 }, 
										{ 150, 0 }, 		{ 150, 50 }, 	{ 150, 100 }, 		{ 150, 150 }
	];

	
	
	
	list<agent> micro_models_Preydator<-[];
	list<agent> micro_models_Sugar<-[];
	init
	{  
		int i <- -1;
		create Preydator.Adapter2 number: grid_size_Preydator*grid_size_Preydator;
		ask Preydator.Adapter2{
			seed<-float(rnd(1111));
			i<-i+1;
			centroid <- myself.offset_Preydator[i];
			do transform_environment;
		}
		
		 i <- -1;
		create Sugar.Adapter number: grid_size_Sugar*grid_size_Sugar  with:[shape::envelope(100)];
		ask Sugar.Adapter{
			seed<-float(rnd(1111));
			i<-i+1;
			centroid <- myself.offset_Sugar[i]; 
			do transform_environment;
		} 
		
		micro_models_Preydator<- Preydator.Adapter2 collect each.simulation;
		micro_models_Sugar<- Sugar.Adapter collect each.simulation;

	}
	reflex ss{
		
		ask micro_models_Sugar
		{
			do _step_;
		}
		
	}
	
	
	list<agent> veg_cells;
	list<agent> sug_cells;
	list<prey> all_preys;
	list<agent> all_animals;


	reflex update {
		veg_cells <- Preydator.Adapter2 accumulate each.simulation.vegetation_cell;
		sug_cells <- Sugar.Adapter accumulate each.simulation.sugar_cell;
		all_preys <- Preydator.Adapter2 accumulate each.simulation.prey;
		all_animals <- Sugar.Adapter accumulate each.simulation.animal;
	}
}

grid G width: grid_size_Preydator height: grid_size_Preydator
{
	reflex a
	{
		ask micro_models_Preydator[int(self)]
		{
			do _step_;
		}
	}

}

experiment main type: gui
{
	output synchronized: true
	{
		display "Co-display" type:2d antialias:false
		{

				grid G border: # red transparency:0.2;				
				
				agents "vegetation_cell" value: veg_cells ;
				
				agents "sugar_cell" value: sug_cells transparency:0.7;

				agents "prey" value: all_preys aspect:base;
						
				agents "animal" value: all_animals;
				
		}

	}

}