/**
* Name: Simple syntax demonstration of Comodeling 
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the importation and instatiation of micro-model without using the couplings  
* Tags: comodel
*/
model simple_comodeling_example

import "Flies.gaml" as Flies
import "Mosquitos.gaml" as Mosquitos


global
{
	init
	{
	//micro_model must be instantiated by create statement. We create an experiment inside the micro-model and the simulation will be created implicitly (1 experiment have only 1 simulation).
		create Flies.Simple  number: 5;
		create Mosquitos.Generic;
	}

	reflex simulate_micro_models
	{

	//tell the first experiment of micro_model_1 do 1 step;
		ask first(Flies.Simple).simulation
		{
			do _step_;
		}

		//tell the  experiment at 3 of micro_model_1 do 1 step;
		ask (Flies.Simple at 3).simulation
		{
			do _step_;
		}

		//tell all experiments of micro_model_1 do 1 step;
		ask (Flies.Simple collect each.simulation)
		{
			do _step_;
		}

		//tell all experiments of micro_model_2 do 1 step;
		ask (Mosquitos.Generic collect each.simulation)
		{
			do _step_;
		}
		
		//ask  simulation of micro_model to kill all agents every 100 cycles and recreate them
		if(cycle mod 100 = 0){			
			ask  (Mosquitos.Generic collect each.simulation){
				ask Mosquito{				
					do die;
				}
			}
			ask  (Mosquitos.Generic collect each.simulation){
				seed<-float(rnd(100));
				do _init_;
			}
		}
	}
}

experiment main type: gui
{
}