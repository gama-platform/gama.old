/**
* Name: serialtest
* Based on the internal empty template. 
* Author: lucas
* Tags: 
*/


model serialtest
import "OLZ.gaml" as pp

global
{
	geometry shape <- rectangle(50,50);
	
	init
	{
		do init_sub_simulation;
		create test;
	}
	
	action init_sub_simulation
    {
    	create pp.movingExp;
    }
    
    reflex runModel
    {
		ask (pp.movingExp collect each.simulation)
	    {
			do _step_;
	    }
    }


	species test parent: TestMPI
	{
		agent target;
		list<agent> targets;
		
		init 
		{
			
		}
		
		reflex 
		{
			/*ask pp.movingExp[0]
			{			
				myself.target <- one_of(standingAgent);
			}
			do seri_deseri(target);*/
			ask pp.movingExp[0]
			{
				loop tmp over: agents
				{				
					write("JNIQ  = "+tmp.UNIQUEID);
				}
			}
			/*ask pp.movingExp[0]
			{
				myself.targets <- 5 among(standingAgent);
			}
			loop tmp over: self.targets
			{				
				write("JNIQ  = "+tmp.UNIQUEID);
			}
			do seri_deseri_list(targets);*/
		}
		
		/*reflex when: cycle = 5
		{
			ask pp.movingExp[0]
			{
				ask movingAgent
				{
					do die;
				}
			}
		}*/
	}
}


experiment exp
{
	
}

