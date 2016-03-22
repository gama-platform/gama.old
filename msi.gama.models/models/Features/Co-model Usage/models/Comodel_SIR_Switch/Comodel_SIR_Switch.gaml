/**
* Name: Comodel_SIR_Switch
* Author: Lô
* Description: This is a comodel that implement the dynamic of SIR_switch: it will use the EBM when the density of population is big and ABM when the density of population is low. It demonstrate the capability of using dynamically the legacy models  
* Tags: Tag1, Tag2, TagN
*/
model Comodel_SIR_Switch

import "Legacy_model/SIR_IF_EBM.gaml" as SIR_1
import "Legacy_model/SIR_IF_ABM.gaml" as SIR_2


global
{
	geometry shape <- envelope(square(100));
	int switch_threshold <- 120; // threshold for switching models
	int threshold_to_IBM <- 220; // threshold under which the model swith to IBM
	int threshold_to_Maths <- 20;
	init
	{
		create SIR_1.SIR_EBM_interface;
		create SIR_2.SIR_ABM_interface;
		create Switch;
	}

	reflex dostep
	{
		ask first(SIR_1.SIR_EBM_interface).simulation
		{
			loop times: 1
			{
				do _step_;
			}

		}

		ask first(SIR_2.SIR_ABM_interface).simulation
		{
			do _step_;
		}

	}

}

species Switch
{
	int S <- 495;
	int I <- 5;
	int R <- 0;
	reflex request_from_micro_model
	{
		if (S > threshold_to_Maths and I > threshold_to_Maths)
		{
		//			write 'Switch to Maths model at cycle ' + cycle;
			ask world
			{
				unknown call;
				call <- first(SIR_1.SIR_EBM_interface).set_num_S(myself.S);
				call <- first(SIR_1.SIR_EBM_interface).set_num_I(myself.I);
				call <- first(SIR_1.SIR_EBM_interface).set_num_R(myself.R);
				ask first(SIR_1.SIR_EBM_interface).simulation
				{
					loop times: 5
					{
						do _step_;
					}

				}

				myself.S <- first(SIR_1.SIR_EBM_interface).get_num_S();
				myself.I <- first(SIR_1.SIR_EBM_interface).get_num_I();
				myself.R <- first(SIR_1.SIR_EBM_interface).get_num_R();
			}

		}

		if (I < threshold_to_IBM or S < threshold_to_IBM)
		{
		//			write 'Switch to IBM model at cycle ' + string(cycle);
			ask world
			{
				unknown call;
				call <- first(SIR_2.SIR_ABM_interface).set_num_S(myself.S);
				call <- first(SIR_2.SIR_ABM_interface).set_num_I(myself.I);
				call <- first(SIR_2.SIR_ABM_interface).set_num_R(myself.R);
				ask first(SIR_2.SIR_ABM_interface).simulation
				{
					loop times: 1
					{
						do _step_;
					}

				}

				myself.S <- first(SIR_2.SIR_ABM_interface).get_num_S();
				myself.I <- first(SIR_2.SIR_ABM_interface).get_num_I();
				myself.R <- first(SIR_2.SIR_ABM_interface).get_num_R();
			}

		}

	}

	aspect base
	{
		draw square(100);
	}

}

experiment simu type: gui
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
