/**
* Name: Simple syntax demonstration of Comodeling 
* Author: HUYNH Quang Nghi
* Description: This is a simple comodel serve to demonstrate the importation and instatiation of micro-model without using the couplings  
* Tags: comodel
*/
model simple_comodeling_example

import "Flies.gaml" as MyFliesCouplingAliasName
import "Mosquitos.gaml" as MyMosquitosCouplingAliasName


global
{
	init
	{
	//micro_model must be instantiated by create statement. We create an experiment inside the micro-model and the simulation will be created implicitly (1 experiment have only 1 simulation).
		create MyFliesCouplingAliasName.FliesExperiment  number: 5;
		create MyMosquitosCouplingAliasName.MosquitosExperiment;
	}

	reflex simulate_micro_models
	{

	//tell the first experiment of micro_model_1 do 1 step;
		ask first(MyFliesCouplingAliasName.FliesExperiment).simulation
		{
			do _step_;
		}

		//tell the  experiment at 3 of micro_model_1 do 1 step;
		ask (MyFliesCouplingAliasName.FliesExperiment at 3).simulation
		{
			do _step_;
		}

		//tell all experiments of micro_model_1 do 1 step;
		ask (MyFliesCouplingAliasName.FliesExperiment collect each.simulation)
		{
			do _step_;
		}

		//tell all experiments of micro_model_2 do 1 step;
		ask (MyMosquitosCouplingAliasName.MosquitosExperiment collect each.simulation)
		{
			do _step_;
		}
		
		//kill simulation  of micro_model and recreate then
		ask  (MyMosquitosCouplingAliasName.MosquitosExperiment collect each.simulation){
			do die;
		}
		ask (MyMosquitosCouplingAliasName.MosquitosExperiment){
			create simulation{do _init_;}
		}
	}
}

experiment SimpleComodelingExampleExp type: gui
{
}