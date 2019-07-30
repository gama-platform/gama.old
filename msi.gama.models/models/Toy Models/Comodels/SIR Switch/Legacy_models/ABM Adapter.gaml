model SIR_ABM_coupling

import "SIR_ABM.gaml"
experiment "Adapter" type: gui
{
	int get_num_S
	{
		return length(Host where (each.state = 0));
	}

	int get_num_I
	{
		return length(Host where (each.state = 1));
	}

	int get_num_R
	{
		return length(Host where (each.state = 2));
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
		ask (Host where (each.state = 0))
		{
			do die;
		}

		create Host number: num
		{
			state <- 0;
		}

	}

	action set_num_I (int num)
	{		
		ask (Host where (each.state = 1))
		{
			do die;
		}

		create Host number: num
		{
			state <- 1;
		}

	}

	action set_num_R (int num)
	{		
		ask (Host where (each.state = 2))
		{
			do die;
		}

		create Host number: num
		{
			state <- 2;
		}
	}

}