model SIR_EBM_coupling

import "SIR_EBM.gaml"
experiment "Adapter" type: gui
{
	int get_num_S
	{
		return int(first(agent_with_SIR_dynamic).S);
	}

	int get_num_I
	{
		return int(first(agent_with_SIR_dynamic).I);
	}

	int get_num_R
	{
		return int(first(agent_with_SIR_dynamic).R);
	}

	action set_num_S_I_R (int numS, int numI, int numR)
	{
		unknown call;
		call <- set_num_S(numS);
		call <- set_num_I(numI);
		call <- set_num_R(numR);
	}

	action set_num_S (int num)
	{
		first(agent_with_SIR_dynamic).S <- float(num);
	}

	action set_num_I (int num)
	{
		first(agent_with_SIR_dynamic).I <- float(num);
	}

	action set_num_R (int num)
	{
		first(agent_with_SIR_dynamic).R <- float(num);
	}
}