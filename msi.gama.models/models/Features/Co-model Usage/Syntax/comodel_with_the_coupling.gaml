/**
* Name: comodel_with_the_coupling
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the importation and instatiation of micro-model using the couplings  
* Tags: comodel
*/
model comodel_with_the_coupling

import "m1_coupling.gaml" as micro_model_1
import "m2_coupling.gaml" as micro_model_2

global
{
	geometry shape<-envelope(square(100));
	init{
		//micro_model must be instantiated by create statement. We create an experiment inside the micro-model and the simulation will be created implicitly (1 experiment have only 1 simulation).
		create micro_model_1.M1_coupling_exp;
		create micro_model_2.M2_coupling_exp number:5;
	}
	reflex simulate_micro_models{
		
		//tell all experiments of micro_model_1 do 1 step;
		ask (micro_model_1.M1_coupling_exp collect each.simulation){
			do _step_;
		}
		
		//tell the first experiment of micro_model_2 do 1 step;
		ask (micro_model_2.M2_coupling_exp collect each.simulation){
			do _step_;
		}
	}
}

experiment comodel_with_the_coupling type: gui{
	output{
		display "comodel" {
			//to display the agents of micro-models, we use the agent layer with the values come from the coupling.
			agents "agentA" value:first(micro_model_1.M1_coupling_exp).getA();
			agents "agentB" value:first(micro_model_2.M2_coupling_exp).getB();
		}
	}
}