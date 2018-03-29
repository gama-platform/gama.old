model SIR_ABM_coupling

import "../../../Epidemiology/models/Susceptible Infected Recovered (SIR).gaml"
experiment "Adapter" type: gui parent: Simulation
{
	int get_num_S
	{
		return length(Host where (each.is_susceptible));
	}

	int get_num_I
	{
		return length(Host where (each.is_infected));
	}

	int get_num_R
	{
		return length(Host where (each.is_immune));
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
		ask (Host where (each.is_susceptible))
		{
			do die;
		}

		create Host number: num
		{
        	is_susceptible <- true;
        	is_infected <-  false;
            is_immune <-  false; 
            color <-  #green;
		}

	}

	action set_num_I (int num)
	{		
		ask (Host where (each.is_infected))
		{
			do die;
		}

		create Host number: num
		{
            is_susceptible <-  false;
            is_infected <-  true;
            is_immune <-  false; 
            color <-  #red; 
		}

	}

	action set_num_R (int num)
	{		
		ask (Host where (each.is_immune))
		{
			do die;
		}

		create Host number: num
		{
            is_susceptible <-  false;
            is_infected <-  false;
            is_immune <-  true; 
            color <-  #blue; 
		}
	}

	output
	{
	}

}