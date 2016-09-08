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
		create Preydator.Adapter number: grid_size_Preydator*grid_size_Preydator
		{
			seed<-rnd(1111);
			i<-i+1;
			centroid <- myself.offset_Preydator[i];
			do transform_environment;
		}
		
		int i <- -1;
		create Sugar.Adapter number: grid_size_Sugar*grid_size_Sugar  with:[shape::envelope(100)]
		{
			seed<-rnd(1111);
			i<-i+1;
			centroid <- myself.offset_Sugar[i]; 
			do transform_environment;
		} 
		
		micro_models_Preydator<- Preydator.Adapter collect each.simulation;
		micro_models_Sugar<- Sugar.Adapter collect each.simulation;

	}
	reflex ss{
		
		ask micro_models_Sugar
		{
			do _step_;
		}
		
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
	output
	{
		display "Co-display"
		{

				grid G lines: # red transparency:0.2;				
				
				agents "vegetation_cell" value: Preydator.Adapter accumulate each.simulation.vegetation_cell ;
				
				agents "sugar_cell" value: Sugar.Adapter accumulate each.simulation.sugar_cell transparency:0.7;

				agents "prey" value: Preydator.Adapter accumulate each.simulation.prey aspect:base;
						
				agents "animal" value: Sugar.Adapter accumulate each.simulation.animal aspect:default;
				
		}

	}

}