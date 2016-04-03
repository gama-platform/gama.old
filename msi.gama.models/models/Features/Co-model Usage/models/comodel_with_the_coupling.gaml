/**
* Name: comodel_with_the_coupling
* Author: Lô
* Description: This is a simple comodel serve to demonstrate the importation and instatiation of micro-model  using the couplings  
* Tags: Tag1, Tag2, TagN
*/ model comodel_with_the_coupling //import the micro-model with an alias name
//import "m1_coupling.gaml" as micro_model_1
import "PreyPredator_coupling.gaml" as myP


global
{
	geometry shape <- rectangle(200, 100);
	list<agent> s;
	list<agent> p;
	int n <- 0;
	init
	{ //micro_model must be instantiated by create statement. We create an experiment inside the micro-model and the simulation will be created implicitly (1 experiment have only 1 simulation).
	//		create micro_model_1.M1_coupling_exp  number:50;
		create myP.PreyPredator_coupling_exp number: 2;
		list<agent> lst1 <- myP.PreyPredator_coupling_exp accumulate each.getPredator();
		list<agent> lst2 <- myP.PreyPredator_coupling_exp accumulate each.getPrey();
		ask (myP.PreyPredator_coupling_exp collect each.simulation)
		{
			write (myself.comodel_with_the_coupling_exp);
			lstPredator <- lst1;
			lstPrey <- lst2;
		}

	}

	reflex simulate_micro_models
	{ //tell the first experiment of micro_model_2 do 1 step;
		ask (myP.PreyPredator_coupling_exp collect each.simulation)
		{
			do _step_;
		}

	}

}

experiment comodel_with_the_coupling_exp type: gui
{
	output
	{
		display "comodel"
		{ //to display the agents of micro-models, we use the agent layer with the values come from the coupling.
		//			agents "agentA" value:(micro_model_1.M1_coupling_exp accumulate each.getA());
			agents "agentprey" value: (myP.PreyPredator_coupling_exp accumulate each.getPrey());
			agents "agentpredator" value: (myP.PreyPredator_coupling_exp accumulate each.getPredator());
		}

	}

}