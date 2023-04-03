/**
* Name: comodel_with_the_coupling
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel to demonstrate the importation and instatiation of micro-model using coupling  
* Tags: comodel
*/
model complex_comodeling_example

import "Adapters/Flies Adapter.gaml" as Flies
import "Adapters/Mosquitos Adapter.gaml" as Mosquitos

global
{
	geometry shape<-envelope(square(100));
	init{
		//micro_model must be instantiated with the create statement. We create an experiment inside the micro-model and the simulation will be created implicitly (1 experiment have only 1 simulation).
		create Flies.Simple;
		create Mosquitos.Generic number:5;
	}
	reflex simulate_micro_models{
		
		//tell all experiments of micro_model_1 to do 1 step;
		ask (Flies.Simple collect each.simulation){
			do _step_;
		}
		
		//tell the first experiment of micro_model_2 to do 1 step;
		ask (Mosquitos.Generic collect each.simulation){
			do _step_;
		}
	}
}

experiment Complex type: gui{
	output{
		display "Comodel Display" {
			//to display the agents of micro-models, we use the agent layer with the values from the coupling.
			agents "agentB" value:(Mosquitos.Generic accumulate each.get_mosquitos());
			agents "agentA" value:(Flies.Simple accumulate each.get_flies());
		}
	}
}