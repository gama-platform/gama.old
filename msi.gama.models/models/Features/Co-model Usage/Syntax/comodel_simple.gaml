/**
* Name: Simple syntax demonstration of Comodeling 
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the importation and instatiation of micro-model without using the couplings  
* Tags: comodel
*/
model Comodel_simple
//import the micro-model with an alias name
import "m1.gaml" as micro_model_1
import "m2.gaml" as micro_model_2


global
{
	init
	{
	//micro_model must be instantiated by create statement. We create an experiment inside the micro-model and the simulation will be created implicitly (1 experiment have only 1 simulation).
		create micro_model_1.M1_exp number: 5;
		create micro_model_2.M2_exp;
	}

	reflex simulate_micro_models
	{

	//tell the first experiment of micro_model_1 do 1 step;
		ask first(micro_model_1.M1_exp).simulation
		{
			do _step_;
		}

		//tell the  experiment at 3 of micro_model_1 do 1 step;
		ask (micro_model_1.M1_exp at 3).simulation
		{
			do _step_;
		}

		//tell all experiments of micro_model_1 do 1 step;
		ask (micro_model_1.M1_exp collect each.simulation)
		{
			do _step_;
		}

		//tell all experiments of micro_model_2 do 1 step;
		ask (micro_model_2.M2_exp collect each.simulation)
		{
			do _step_;
		}

	}

}

experiment Comodel_simple_exp type: gui
{
}