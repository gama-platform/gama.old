/**
* Name: Comodel SIR Switch
* Author: HUYNH Quang Nghi
* Description: This is a comodel that implement the dynamic of SIR_switch: it will use the EBM when the density of population is big and ABM when the density of population is low. It demonstrate the capability of using dynamically the legacy models  
* Tags: comodel
*/
model Comodel_SIR_Switch

import "Legacy_models/SIR_EBM_coupling.gaml" as SIR_1
import "Legacy_models/SIR_ABM_coupling.gaml" as SIR_2


global
{
	geometry shape <- envelope(square(100));
	int switch_threshold <- 120; // threshold for switching models
	int threshold_to_IBM <- 220; // threshold under which the model swith to IBM
	int threshold_to_Maths <- 20;
	init
	{
		create SIR_1.SIR_EBM_coupling_exp;
		create SIR_2.SIR_ABM_coupling_exp;
		create Switch;
	}

}

species Switch
{
	int S <- 495;
	int I <- 5;
	int R <- 0;
	reflex request_from_micro_model
	{
		//if the size of S population and I population are bigger than a threshold, use the EBM
		if (S > threshold_to_Maths and I > threshold_to_Maths)
		{
			ask world
			{
				unknown call;
				call <- first(SIR_1.SIR_EBM_coupling_exp).set_num_S_I_R(myself.S, myself.I, myself.R);
				ask first(SIR_1.SIR_EBM_coupling_exp).simulation
				{
					loop times: 5
					{
						do _step_;
					}

				}

				myself.S <- first(SIR_1.SIR_EBM_coupling_exp).get_num_S();
				myself.I <- first(SIR_1.SIR_EBM_coupling_exp).get_num_I();
				myself.R <- first(SIR_1.SIR_EBM_coupling_exp).get_num_R();
			}

		}
		
		//if the size of S population or  I population are smaller  than a threshold, use the ABM
		if (I < threshold_to_IBM or S < threshold_to_IBM)
		{
			ask world
			{
				unknown call;
				call <- first(SIR_2.SIR_ABM_coupling_exp).set_num_S_I_R(myself.S, myself.I, myself.R);
				ask first(SIR_2.SIR_ABM_coupling_exp).simulation
				{
					loop times: 1
					{
						do _step_;
					}

				}

				myself.S <- first(SIR_2.SIR_ABM_coupling_exp).get_num_S();
				myself.I <- first(SIR_2.SIR_ABM_coupling_exp).get_num_I();
				myself.R <- first(SIR_2.SIR_ABM_coupling_exp).get_num_R();
			}

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
		display co_SIR_chart
		{
			chart "SIR_agent" type: series background: # white
			{
				data 'S' value: first(Switch).S color: # green;
				data 'I' value: first(Switch).I color: # red;
				data 'R' value: first(Switch).R color: # yellow;
			}

		}

	}

}
