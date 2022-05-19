/**
* Name: NewModel
* Test of gama 
* Author: Lucas GROSJEAN
* Tags: 
*/


model controler

import "OLZ.gaml" as pp

global
{
	
	string file_name;
	int final_step <- 10;
	int agent_range <- 5;
	int rank;
	
	init
	{	
		do init_sub_simulation;
		create slave;
		//create testMPI;
	}
    
    action init_sub_simulation
    {
    	create pp.movingExp;
    }
    
    reflex runModel when: cycle < final_step
    {
		ask (pp.movingExp collect each.simulation)
	    {
			do _step_;
	    }
    }
    
    reflex die when: cycle = final_step
    {
    	ask (pp.movingExp collect each.simulation)
	    {
			do die;
	    }
	    ask slave
	    {
	    	do die;
	    }
    	do die;
    }
}

species slave parent: SlaveMPI
{
	cell cellule;
	int neighbors;
	
	list<agent> agent_inside_me;
	list<agent> inside_main;
	list<agent> inside_outer_OLZ;
	list<agent> inside_inner_OLZ;
	list<agent> to_delete;
	
	init
	{
		rank <- myRank;
		file_name <- "log"+myRank+".txt";
		do clearLogFile();
		
		ask pp.movingExp[0]
		{
			myself.cellule <- cell[0,myself.myRank];
		}
		
		shape <- cellule.shape;
		outer_OLZ_area <- agent_range around(self.shape);
		inner_OLZ_area <- agent_range around(self.shape - agent_range);
		
		if(myRank = 0)
		{	
			neighbors <- 1;
		}else
		{
			neighbors <- 0;
		}
		
		do writeLog("My shape " + shape);
		do writeLog("My rank is " + myRank);
		do writeLog("NetSize " + MPI_SIZE());
		do writeLog("My cell is " + cellule);
		do writeLog("neighbors " + neighbors);	
		
		do writeLog("outer_OLZ_area " + outer_OLZ_area);
		do writeLog("inner_OLZ_area " + inner_OLZ_area);	
		do writeLog("main_area " + shape);	
	}
	
	action deleteAgentsNotInMyArea
	{
		ask pp.movingExp[0]
	    {
	    	 myself.agent_inside_me <- agents; 
	    }
	    
		do writeLog("ALL agents in the model = "+agent_inside_me);
		
		inside_main <- agent_inside_me inside(shape); 
		inside_outer_OLZ <- agent_inside_me inside(outer_OLZ_area);
		inside_inner_OLZ <- agent_inside_me inside(inner_OLZ_area);
		
		do writeLog("Agents insideMain = " + string(inside_main));
		do writeLog("Agents inside_outer_OLZ = " + string(inside_outer_OLZ));
		do writeLog("Agents inside_inner_OLZ = " + string(inside_inner_OLZ));
		
		agent_inside_me <- inside_main + inside_outer_OLZ; // inner is in main
		
		list<agent> agent_outside_me;
		string deleted;
		ask pp.movingExp[0] 
	    {
	    	agent_outside_me <- agents - myself.agent_inside_me;
	    	ask movingAgent
	    	{
	    		if(agent_outside_me contains self)
	    		{
	    			deleted<- deleted + ", " + self.name;
	    			do die;
	    		}
	    	}
	    	ask followingAgent
	    	{
	    		if(agent_outside_me contains self)
	    		{
	    			deleted<- deleted + ", " + self.name;
	    			do die;
	    		}
	    	}
	    	ask standingAgent
	    	{
	    		if(agent_outside_me contains self)
	    		{
	    			deleted<- deleted + ", " + self.name;
	    			do die;
	    		}
	    	}
	    }
		do writeLog("agent deleted = "  +deleted);
	    
		do writeLog("Agents in INNER + MAIN + OUTER : " + agent_inside_me);
	}
	
	reflex routineMPI
	{
		do writeLog("--------"+cycle+"----------");
		
		do deleteAgentsNotInMyArea;
		
		do start_listener;
		
		do writeLog("b1");
		do MPI_BARRIER();
		do writeLog("b2");
		
		if(myRank = 0)
		{	
			list<agent> t <- getAgentInNeighborInnerOLZ(neighbors);		
			do writeLog("getAgentInNeighborInnerOLZ = "+t);
		}
		
		do writeLog("b3");
		do MPI_BARRIER();
		do writeLog("b4");
		
		do stop_listener;
	}
	
    action writeLog(string log)
	{
		save log type: text to: file_name rewrite:false;
	}
	
	action clearLogFile
	{
		save "" type: text to: file_name rewrite:true;
	}
	
	aspect olz
	{
		draw self.shape;
		draw self.outer_OLZ_area color:#red;
		draw self.inner_OLZ_area color:#blue;
		
		draw movingAgent;
		
		ask pp.movingExp[0]
		{
			draw movingAgent color:#black;
			draw followingAgent color:#black;
		}
	}
}


experiment main
{	
	output
	{
		display "display1" type: java2D
		{
			species slave aspect:olz;
		}
	}
}