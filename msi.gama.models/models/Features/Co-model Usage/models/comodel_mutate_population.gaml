/**
* Name: comodel_with_the_coupling
* Author: Lô
* Description: This is a simple comodel serve to demonstrate the importation and instatiation of micro-model  using the couplings  
* Tags: Tag1, Tag2, TagN
*/ model comodel_with_the_coupling

import "PreyPredator_coupling.gaml" as myP


global
{
	geometry shape <- rectangle(200, 100);
	list<agent> s;
	list<agent> p;
	int n <- 0;
	init
	{
		create myP.PreyPredator_coupling_exp number: 3;
		
		list<agent> lstpredator0<-myP.PreyPredator_coupling_exp[0].getPredator();
		list<agent> lstprey0<-myP.PreyPredator_coupling_exp[0].getPrey();
		list<agent> lstpredator1<-myP.PreyPredator_coupling_exp[1].getPredator();
		list<agent> lstprey1<-myP.PreyPredator_coupling_exp[1].getPrey();
		list<agent> lstpredator2<-myP.PreyPredator_coupling_exp[2].getPredator();
		list<agent> lstprey2<-myP.PreyPredator_coupling_exp[2].getPrey();
		
		(myP.PreyPredator_coupling_exp [0].simulation).lstPredator <-lstpredator2;
		(myP.PreyPredator_coupling_exp [1].simulation).lstPredator <- lstprey2;
		(myP.PreyPredator_coupling_exp [2].simulation).lstPredator <-lstprey1;
		
		
		
		(myP.PreyPredator_coupling_exp [0].simulation).lstPrey <- lstprey0+lstprey1;
		(myP.PreyPredator_coupling_exp [1].simulation).lstPrey <-  lstpredator1;
		(myP.PreyPredator_coupling_exp [2].simulation).lstPrey <-  lstpredator0+lstprey2;
		
	}

	reflex simulate_micro_models
	{


	//tell the first experiment of PreyPredator_coupling_exp  do 1 step;
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
		{

			agents "agentprey" value: (myP.PreyPredator_coupling_exp accumulate each.getPrey());
			agents "agentpredator" value: (myP.PreyPredator_coupling_exp accumulate each.getPredator());
		}

	}

}