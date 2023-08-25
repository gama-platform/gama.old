/**
* Name: Comodel SIR Switch
* Author: HUYNH Quang Nghi
* Description: This is a comodel that implement the dynamic of SIR_switch: it will use the EBM when the density of population is big and ABM when 
* the density of population is low. It demonstrate the capability of using dynamically the legacy models.
* 
* SIR_ABM_coupling is the coupling that manipulates the elements inside SIR_ABM model and proposes the function would be used from outside. SIR_ABM is a simple example of SIR that use the agents to represent the spreading of disease..
* 
* SIR_EBM_coupling is the coupling that manipulates the elements inside SIR_EBM model and proposes the function would be used from outside. SIR_EBM is a simple example of ODE use into agents with the example of the SIR equation system.
* Tags: comodel, math, equation
*/
model Comodel_SIR_Switch

import "Legacy_models/EBM Adapter.gaml" as SIR_1
import "Legacy_models/ABM Adapter.gaml" as SIR_2


global
{
	geometry shape <- envelope(square(100));
	int switch_threshold <- 120; // threshold for switching models
	int threshold_to_IBM <- 220; // threshold under which the model swith to IBM
	int threshold_to_Maths <- 20;
	init
	{
		create SIR_1."Adapter";
		create SIR_2."Adapter";
		create Switch;
	} 

}

species Switch
{
	int S <- 495;
	int I <- 50;
	int R <- 0;
	reflex request_from_micro_model
	{
		//if the size of S population and I population are bigger than a threshold, use the EBM
		if (S > threshold_to_Maths and I > threshold_to_Maths)
		{
				if(first(SIR_1."Adapter")!=nil){					
					unknown call;
					call <- first(SIR_1."Adapter").set_num_S_I_R(S, I, R);
					ask first(SIR_1."Adapter").simulation
					{
						loop times: 1
						{
							do _step_;
						}
	
					}
	
					S <- first(SIR_1."Adapter").get_num_S();
					I <- first(SIR_1."Adapter").get_num_I();
					R <- first(SIR_1."Adapter").get_num_R();
				}
		}
		else
		//if the size of S population or  I population are smaller  than a threshold, use the ABM
		if (I < threshold_to_IBM or S < threshold_to_IBM)
		{
				unknown call;
				call <- first(SIR_2."Adapter").set_num_S_I_R(S, I, R);
				ask first(SIR_2."Adapter").simulation
				{
					loop times: 10
					{
						do _step_;
					}

				}

				S <- first(SIR_2."Adapter").get_num_S();
				I <- first(SIR_2."Adapter").get_num_I();
				R <- first(SIR_2."Adapter").get_num_R();
		}

	}

	aspect base
	{
		draw square(100);
	}

}

experiment Simple_exp type: gui
{
	output
	{
	 	layout horizontal([0::5000,vertical([1::5000,2::5000])::5000]) tabs:true editors: false;
		display "Switch_SIR chart" type: 2d 
		{
			chart "SIR_agent" type: series background: # white
			{
				data 'S' value: first(Switch).S color: # green;
				data 'I' value: first(Switch).I color: # red;
				data 'R' value: first(Switch).R color: # blue;
			}

		}
		display "EBM Disp"  type: 2d {			
			chart "SIR_agent" type: series background: #white {
				data 'S' value: first(first(SIR_1."Adapter").simulation.agent_with_SIR_dynamic).S color: #green ;				
				data 'I' value: first(first(SIR_1."Adapter").simulation.agent_with_SIR_dynamic).I color: #red ;
				data 'R' value: first(first(SIR_1."Adapter").simulation.agent_with_SIR_dynamic).R color: #blue ;
			}
		}
		display "ABM Disp" type:2d{			
			agents "Host" value:first(SIR_2."Adapter").simulation.Host aspect:base;
		}

	}

}
