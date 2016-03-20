/**
 *  Comodel
 *  Author: Quang and Nghi
 *  Description: 
 */
model SIR_IF_EBM

import "SIR_EBM.gaml"

experiment SIR_EBM_interface type: gui parent: SIR_EBM_exp
{
	int get_num_S
	{
		return first(agent_with_SIR_dynamic).S;
	}

	int get_num_I
	{
		return first(agent_with_SIR_dynamic).I;
	}

	int get_num_R
	{
		return first(agent_with_SIR_dynamic).R;
	}

	action set_num_S (int num)
	{
		first(agent_with_SIR_dynamic).S <- num;
	}

	action set_num_I (int num)
	{
		first(agent_with_SIR_dynamic).I <- num;
	}

	action set_num_R (int num)
	{
		first(agent_with_SIR_dynamic).R <- num;
	}
//	output{}
}