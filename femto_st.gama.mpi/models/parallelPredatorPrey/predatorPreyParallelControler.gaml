/**
* Name: mpi controler
* Author: NM / LP / LG
* Description: 
* Tags: inheritance
*/
model controler

import "predatorPrey.gaml" as pp

global skills:[MPI_Network] 
{
		
    int mpiRank <- 0;
    int netSize <- 0;
	
    int current_step <- 0;
    int nbLoop <- 5;
	
	// Rank of neighbor
    int leftNeighbor; 
    int rightNeighbor;
    
    
    // Strip values
	float modelSize;
	float modelStripSize;

    // Strip limits
    float stripStart;
    float stripEnd;

    // OverLap Zone (OLZ) values
    float OLZSize; // mettre à la valeur de la perception la plus eleve du modele 
    
    float leftOuterOLZBound;
    float leftInnerOLZBound;	
    float rightInnerOLZBound;
    float rightOuterOLZBound;
    
	int p;
    // ----------Process on Outer :
	// Create if enter outer             // receive from other                OTHER -> OLZ outer
	// Kill if leave outer to others 	 // send to other					  OLZ outer -> OTHER
	
	
	// ----------Process on Inner :
	// Send if enter from own to OLZ   // send to other						  OWN -> OLZ Inner
    
	float prey_max_energy  <- 1.0;
	float prey_max_transfert  <- 0.1 ;
	float prey_energy_consum  <- 0.05;
	float predator_max_energy  <- 1.0;
	float predator_energy_transfert  <- 0.5;
	float predator_energy_consum  <- 0.02;
	
    init 
    {
        do MPI_INIT;
    	do init_sub_simulation;
		
	    mpiRank <- MPI_RANK();
	    netSize <- MPI_SIZE ();
	    	    
	    int width_grid <- pp.prey_predatorExp[0].get_width_grid();
	    int height_grid <- pp.prey_predatorExp[0].get_height_grid();
	    
	    write("width_grid = "+width_grid+"\n") ;
	    write("height_grid = "+height_grid+"\n") ;
	    
    	modelSize <- pp.prey_predatorExp[0].get_size_model();	
		modelStripSize <- modelSize / netSize;
		
		
    	leftNeighbor <- mpiRank - 1;
    	if (leftNeighbor = -1)
    	{ 
    		leftNeighbor <- mpiRank;
    	}
    	
    	rightNeighbor <- mpiRank + 1;
    	if (rightNeighbor = netSize) 
    	{
			rightNeighbor <- mpiRank;
    	}
	
		/* Defines strip position values */
		stripStart <- modelStripSize * mpiRank;
		stripEnd <- (modelStripSize * (mpiRank + 1));
		
		OLZSize <- modelSize / width_grid;
	    write("modelSize = "+modelSize+"\n") ;
	    write("OLZSize = "+OLZSize+"\n") ;
		
		/* Defines overlap bounds */
    	leftOuterOLZBound <- stripStart - OLZSize;
    	leftInnerOLZBound <- stripStart + OLZSize;	
    	rightInnerOLZBound <- stripEnd - OLZSize;
    	rightOuterOLZBound <- stripEnd + OLZSize;
    	
    
	    write(""+mpiRank+" mon rank est " + ""+mpiRank+"\n") ;
	    
    	write(""+mpiRank+" netSize = "+netSize+"\n");
    	write(""+mpiRank+" modelSize = "+modelSize+"\n");
    
    	write(""+mpiRank+" leftNeighbor = "+leftNeighbor+"\n");
    	write(""+mpiRank+" rightNeighbor = "+rightNeighbor+"\n");
    	
    	
    	write(""+mpiRank+" has leftNeighbor= "+(leftNeighbor != mpiRank)+"\n");
    	write(""+mpiRank+" has rightNeighbor = "+(rightNeighbor != mpiRank)+"\n");
    	
    	
    	write(""+mpiRank+" stripStart = "+stripStart+"\n");
    	write(""+mpiRank+" stripEnd = "+stripEnd+"\n");
    	
    	write(""+mpiRank+" leftOuterOLZBound = "+leftOuterOLZBound+"\n");
    	write(""+mpiRank+" leftInnerOLZBound = "+leftInnerOLZBound+"\n");
    	write(""+mpiRank+" rightInnerOLZBound = "+rightInnerOLZBound+"\n");
    	write(""+mpiRank+" rightOuterOLZBound = "+rightOuterOLZBound+"\n");
    	
    	
		loop times: nbLoop {
		
			write("------------------------------------------------------------------------"+mpiRank+" loop : "+current_step+" \n");
    		if(even(mpiRank))
    		{
    			do runMpi_even;
    		}else
    		{
    			do runMpi_odd;
    		}
			//do runModel;
    		current_step <- current_step + 1;
		}
		/*list<prey> prey_test <- pp.prey_predatorExp[0].get_prey_in_area(0, modelSize);
		list<predator> predator_test <- pp.prey_predatorExp[0].get_predator_in_area(0, modelSize);
		
		if(mpiRank = 0)
		{
		    write("prey_test size = "+length(prey_test));
		    write("predator_test size = "+length(predator_test));
			do MPI_SEND mesg: prey_test dest: rightNeighbor stag: 50;  
	   		write("send1 over");
	   		do MPI_SEND mesg: predator_test dest: rightNeighbor stag: 50;  
	   		write("send2 over");
	   		
		}else
		{
			unknown leftOuterOLZPreys_test <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	unknown leftOuterOLZPreds_test <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		    
		    write("leftOuterOLZPreys left = "+leftOuterOLZPreys_test+"\n");
		    write("leftOuterOLZPreds left = "+leftOuterOLZPreds_test+"\n");
		    
   		 	list<prey> prey_list_test <- pp.prey_predatorExp[0].create_prey_from_list(leftOuterOLZPreys_test);
		    list<predator> predator_list_test <- pp.prey_predatorExp[0].create_predator_from_list(leftOuterOLZPreds_test);
		    
		    write("prey_list_test = "+prey_list_test);
		    write("predator_list_test = "+predator_list_test);
		    write("prey_list_test size = "+length(prey_list_test));
		    write("predator_list_test size = "+length(predator_list_test));
		}*/
		
		/*loop times: nbLoop {
			if(mpiRank = 0)
			{
					
				list<prey> prey_test <- pp.prey_predatorExp[0].get_prey_in_area(0, modelSize);
				do MPI_SEND mesg: prey_test dest: rightNeighbor stag: 50;  
				
				unknown leftOuterOLZPreys_test <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
				write(""+mpiRank+"leftOuterOLZPreys_test = "+leftOuterOLZPreys_test);
	   		 	//list<prey> prey_list_test <- pp.prey_predatorExp[0].create_prey_from_list(leftOuterOLZPreys_test);
			}else
			{
				
				unknown leftOuterOLZPreys_test <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
				write(""+mpiRank+"leftOuterOLZPreys_test = "+leftOuterOLZPreys_test);
	   		 	//list<prey> prey_list_test <- pp.prey_predatorExp[0].create_prey_from_list(leftOuterOLZPreys_test);
	   		 	
	   		 	list<prey> prey_test <- pp.prey_predatorExp[0].get_prey_in_area(0, modelSize);
				do MPI_SEND mesg: prey_test dest: leftNeighbor stag: 50;  
				
			}
		}*/
    	
    	/*if(mpiRank = 0)
		{
			list<prey> xd <- pp.prey_predatorExp[0].get_prey_in_area(stripStart, stripEnd);
			
			write("1-----------------------------------------\n");
	   		do MPI_SEND mesg: xd dest: 1 stag: 50;   
			write("2-----------------------------------------\n");
	   		
		}else
		{		
			unknown unk <- self MPI_RECV [rcvsize:: 1, source:: 0, rtag:: 50];
			
			loop tmp over: unk
			{
				map<string,unknown> map_unk <- map(tmp);
				write(map_unk);
				prey prey_tmp <- pp.prey_predatorExp[0].create_prey(map_unk);
				write("prey_tmp energy = "+prey_tmp.energy+"\n");
			}	
		}*/
		
		list<prey> prey_in_band <- pp.prey_predatorExp[0].get_prey_in_area(stripStart, stripEnd);
		list<predator> predator_in_band <- pp.prey_predatorExp[0].get_predator_in_area(stripStart, stripEnd);
		
		write(""+mpiRank+" preys in band: "+ prey_in_band +"\n");
		write(""+mpiRank+" predators in band : "+ predator_in_band +"\n");
		
		write(""+mpiRank+" preys : "+ length(prey_in_band) +"\n");
		write(""+mpiRank+" predators : "+ length(predator_in_band) +"\n");
		
    	do finalize;
    }
    
    action cleanOuterOLZ
   	{
		write(""+mpiRank+" cleanOuterOLZ\n");
   		int deleted_agents <- pp.prey_predatorExp[0].delete_agent_not_in_band(stripStart, stripEnd);
   		write(""+mpiRank+" nb deleted "+deleted_agents+"\n");
    }
    
    action init_sub_simulation
    {
    	create pp.prey_predatorExp;
    }
    
    action runModel
    {
		ask (pp.prey_predatorExp collect each.simulation)
	    {
			do _step_;
	    }
    }
    
    action runMpi_odd
    {
    	write(""+mpiRank+" cleanOuterOLZ ****\n");
    	do cleanOuterOLZ;
    	
    	write(""+mpiRank+" updateOuterOLZ_odd  ****\n");
    	do updateOuterOLZ_odd;
    	
    	write(""+mpiRank+" runModel_odd****\n");
    	do runModel;
    	
    	write(""+mpiRank+" updateInnerOLZ_odd  ****\n");
    	do updateInnerOLZ_odd;
    }
    
    action updateOuterOLZ_odd
    {
    	// Receive overlap zones from neighbors		 
		// Left outer overlap zone 
		if (leftNeighbor != mpiRank)
		{
			write(""+mpiRank+" leftNeighbor update outer\n");
		   	unknown leftOuterOLZPreys <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 left \n");            
		   	unknown leftOuterOLZPreds <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 left \n");            
		    
		    write("leftOuterOLZPreys left = "+leftOuterOLZPreys+"\n");
		    write("leftOuterOLZPreds left = "+leftOuterOLZPreds+"\n");
		    
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(leftOuterOLZPreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(leftOuterOLZPreds);
		    
		    write("prey_list "+prey_list);
		    write("predator_list "+predator_list);
        }
	
		// Right outer overlap zone
        if ( rightNeighbor != mpiRank)
        {
			write(""+mpiRank+" rightNeighbor update outer\n");
		   	unknown rightOuterOLZPreys <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 right \n");            
		   	unknown rightOuterOLZPreds <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 right \n");            
		    
		    write("rightOuterOLZPreys right = "+rightOuterOLZPreys+"\n");
		    write("rightOuterOLZPreds right = "+rightOuterOLZPreds+"\n");
		    
		        
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(rightOuterOLZPreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(rightOuterOLZPreds);
		    
		    write("prey_list "+prey_list);
		    write("predator_list "+predator_list);
        }
        
    	if (leftNeighbor != mpiRank) // Only nodes with a left part of the env
		{ 
			write(""+mpiRank+" leftNeighbor update inner send\n");
			
		   	list<prey> leftPreyList <- pp.prey_predatorExp[0].get_prey_in_area(stripStart, leftInnerOLZBound); 
			write(""+mpiRank+" leftNeighbor update inner send list prey : \n"+leftPreyList+"\n");
			
		   	list<predator> leftPredatorList <- pp.prey_predatorExp[0].get_predator_in_area(stripStart, leftInnerOLZBound);
			write(""+mpiRank+" leftNeighbor update inner send list predator : \n"+leftPredatorList+"\n");
			
			
			write(""+mpiRank+" before send\n");
	   		do MPI_SEND mesg: leftPreyList dest: leftNeighbor stag: 50;  
			write(""+mpiRank+" send1\n");    
	   		do MPI_SEND mesg: leftPredatorList dest: leftNeighbor stag: 50;    
			write(""+mpiRank+" send2\n");  
			
			write(""+mpiRank+" *** updateOuterOLZ : leftNeighbor collected \n");
		}
	
		// Right inner overlap zone
		
		write(""+mpiRank+" *** Right inner overlap zone \n");
		if (rightNeighbor != mpiRank) // Only nodes with a right part of the env
		{ 
			write(""+mpiRank+" rightNeighbor update inner send\n");
			
		   	list<prey> rightPreyList <- pp.prey_predatorExp[0].get_prey_in_area(rightInnerOLZBound, stripEnd); 
			write(""+mpiRank+" rightNeighbor update inner send list prey : \n"+rightPreyList+"\n");
			
		   	list<predator> rightPredatorList <- pp.prey_predatorExp[0].get_predator_in_area(rightInnerOLZBound, stripEnd);		   	
			write(""+mpiRank+" rightNeighbor update inner send list predator : \n"+rightPredatorList+"\n");
			
			write(""+mpiRank+" before send xxxxxx"+rightPreyList+"\n");
	   		do MPI_SEND mesg: rightPreyList dest: rightNeighbor stag: 50;      
			write(""+mpiRank+" send1 xxxxxxxxxxxxxxxxxxxxxxxxxxx\n");    
	   		do MPI_SEND mesg: rightPredatorList dest: rightNeighbor stag: 50;   
			write(""+mpiRank+" send2 xxxxxxxxxxxxxxxxxxxxxxxxxxx\n");     
			
		   
    		write("*** updateOuterOLZ : rightNeighbor collected  \n");
    	}
    }
    
    action updateInnerOLZ_odd
    {
    	// Receive incominig agents and create them in my part
		// Left outer overlap zone
    	if (leftNeighbor != mpiRank) 
    	{
			write(""+mpiRank+" leftNeighbor update outer receive\n");
			
		   	unknown leftIncomePreys <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 left \n");            
		   	unknown leftIncomePredators <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 left \n");            
		   	
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(leftIncomePreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(leftIncomePredators);
		    
		    write("prey_list : "+prey_list);
		    write("predator_list : "+predator_list);
		    
		}
	
		// Right outer overlap zone
    	if (rightNeighbor != mpiRank) 
    	{
			write(""+mpiRank+" rightNeighbor update outer receive\n");
		   	unknown rightIncomePreys <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 right \n");            
		   	unknown rightIncomePredators <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 right \n");            
		   	
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(rightIncomePreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(rightIncomePredators);
		    
		    write("prey_list : "+prey_list);
		    write("predator_list : "+predator_list);
		}
    	// Gather agents that have moved in the OLZ and send then to the neighbors	
		// Left outer overlap zone
		if (leftNeighbor != mpiRank) 
		{
			write(""+mpiRank+" leftNeighbor update inner\n");
		   	list<prey> leftOutcomePreys <- pp.prey_predatorExp[0].get_prey_in_area(leftOuterOLZBound, stripStart); 
		   	list<predator> leftOutcomePredators <- pp.prey_predatorExp[0].get_predator_in_area(leftOuterOLZBound, stripStart);		
		   	write(""+mpiRank+" send left OLZ \n  preys= \n " + leftOutcomePreys + "\n predators" + leftOutcomePredators+" \n");
		   	
		   	write(""+mpiRank+" before send updateInnerOLZ left xxxxxxxx \n");
		   	do MPI_SEND mesg: leftOutcomePreys dest: leftNeighbor stag: 50;  
		   	write(""+mpiRank+" send 1 left\n");    
	   		do MPI_SEND mesg: leftOutcomePredators dest: leftNeighbor stag: 50;    
		   	write(""+mpiRank+" send 2 left\n");      
    	}
		
    	// Right outer overlap zone
    	if (rightNeighbor != mpiRank)
		{
			write(""+mpiRank+" rightNeighbor update inner\n");
			
		   	list<prey> rightOutcomePreys <- pp.prey_predatorExp[0].get_prey_in_area(stripEnd, rightOuterOLZBound); 
		   	list<predator> rightOutcomePredators <- pp.prey_predatorExp[0].get_predator_in_area(stripEnd, rightOuterOLZBound);		
		   	write(""+mpiRank+" send right OLZ \n  preys= \n " + rightOutcomePreys + "\n predators" + rightOutcomePredators+" \n");
		   	
		   	
		   	write(""+mpiRank+" before send right updateInnerOLZ xxxxxxxx \n");
	   		do MPI_SEND mesg: rightOutcomePreys dest: rightNeighbor stag: 50;  
		   	write(""+mpiRank+" send 1 right \n");        
	   		do MPI_SEND mesg: rightOutcomePredators dest: rightNeighbor stag: 50;
		   	write(""+mpiRank+" send 2 right \n");            
			
    	}	
    }
    
    action runMpi_even
    {
    	write(""+mpiRank+" cleanOuterOLZ ****\n");
    	do cleanOuterOLZ;
    	
    	write(""+mpiRank+" updateOuterOLZ_even  ****\n");
    	do updateOuterOLZ_even;
    	
    	write(""+mpiRank+" runModel_even****\n");
    	do runModel;
    	
    	write(""+mpiRank+" updateInnerOLZ_even ****\n");
    	do updateInnerOLZ_even;
    }
    
    action updateOuterOLZ_even
    {
    	if (leftNeighbor != mpiRank) // Only nodes with a left part of the env
		{ 
			write(""+mpiRank+" leftNeighbor update inner send\n");
			
		   	list<prey> leftPreyList <- pp.prey_predatorExp[0].get_prey_in_area(stripStart, leftInnerOLZBound); 
			write(""+mpiRank+" leftNeighbor update inner send list prey : \n"+leftPreyList+"\n");
			
		   	list<predator> leftPredatorList <- pp.prey_predatorExp[0].get_predator_in_area(stripStart, leftInnerOLZBound);
			write(""+mpiRank+" leftNeighbor update inner send list predator : \n"+leftPredatorList+"\n");
			
			
			write(""+mpiRank+" before send\n");
	   		do MPI_SEND mesg: leftPreyList dest: leftNeighbor stag: 50;  
			write(""+mpiRank+" send1\n");    
	   		do MPI_SEND mesg: leftPredatorList dest: leftNeighbor stag: 50;    
			write(""+mpiRank+" send2\n");  
			
			write(""+mpiRank+" *** updateOuterOLZ : leftNeighbor collected \n");
		}
	
		// Right inner overlap zone
		
		write(""+mpiRank+" *** Right inner overlap zone \n");
		if (rightNeighbor != mpiRank) // Only nodes with a right part of the env
		{ 
			write(""+mpiRank+" rightNeighbor update inner send\n");
			
		   	list<prey> rightPreyList <- pp.prey_predatorExp[0].get_prey_in_area(rightInnerOLZBound, stripEnd); 
			write(""+mpiRank+" rightNeighbor update inner send list prey : \n"+rightPreyList+"\n");
			
		   	list<predator> rightPredatorList <- pp.prey_predatorExp[0].get_predator_in_area(rightInnerOLZBound, stripEnd);		   	
			write(""+mpiRank+" rightNeighbor update inner send list predator : \n"+rightPredatorList+"\n");
			
			write(""+mpiRank+" before send xxxxxx"+rightPreyList+"\n");
	   		do MPI_SEND mesg: rightPreyList dest: rightNeighbor stag: 50;      
			write(""+mpiRank+" send1 xxxxxxxxxxxxxxxxxxxxxxxxxxx\n");    
	   		do MPI_SEND mesg: rightPredatorList dest: rightNeighbor stag: 50;   
			write(""+mpiRank+" send2 xxxxxxxxxxxxxxxxxxxxxxxxxxx\n");     
			
		   
    		write("*** updateOuterOLZ : rightNeighbor collected  \n");
    	}
		   
		// Receive overlap zones from neighbors		 
		// Left outer overlap zone 
		if (leftNeighbor != mpiRank)
		{
			write(""+mpiRank+" leftNeighbor update outer\n");
		   	unknown leftOuterOLZPreys <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 left \n");            
		   	unknown leftOuterOLZPreds <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 left \n");            
		    
		    write("leftOuterOLZPreys left = "+leftOuterOLZPreys+"\n");
		    write("leftOuterOLZPreds left = "+leftOuterOLZPreds+"\n");
		    
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(leftOuterOLZPreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(leftOuterOLZPreds);
		    
		    write("prey_list "+prey_list);
		    write("predator_list "+predator_list);
        }
	
		// Right outer overlap zone
        if ( rightNeighbor != mpiRank)
        {
			write(""+mpiRank+" rightNeighbor update outer\n");
		   	unknown rightOuterOLZPreys <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 right \n");            
		   	unknown rightOuterOLZPreds <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 right \n");            
		    
		    write("rightOuterOLZPreys right = "+rightOuterOLZPreys+"\n");
		    write("rightOuterOLZPreds right = "+rightOuterOLZPreds+"\n");
		    
		        
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(rightOuterOLZPreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(rightOuterOLZPreds);
		    
		    write("prey_list "+prey_list);
		    write("predator_list "+predator_list);
        }
    	
    }
    
    action updateInnerOLZ_even
    {
    	// Gather agents that have moved in the OLZ and send then to the neighbors	
		// Left outer overlap zone
		if (leftNeighbor != mpiRank) 
		{
			write(""+mpiRank+" leftNeighbor update inner\n");
		   	list<prey> leftOutcomePreys <- pp.prey_predatorExp[0].get_prey_in_area(leftOuterOLZBound, stripStart); 
		   	list<predator> leftOutcomePredators <- pp.prey_predatorExp[0].get_predator_in_area(leftOuterOLZBound, stripStart);		
		   	write(""+mpiRank+" send left OLZ \n  preys= \n " + leftOutcomePreys + "\n predators" + leftOutcomePredators+" \n");
		   	
		   	write(""+mpiRank+" before send updateInnerOLZ left xxxxxxxx \n");
		   	do MPI_SEND mesg: leftOutcomePreys dest: leftNeighbor stag: 50;  
		   	write(""+mpiRank+" send 1 left\n");    
	   		do MPI_SEND mesg: leftOutcomePredators dest: leftNeighbor stag: 50;    
		   	write(""+mpiRank+" send 2 left\n");      
    	}
		
    	// Right outer overlap zone
    	if (rightNeighbor != mpiRank)
		{
			write(""+mpiRank+" rightNeighbor update inner\n");
			
		   	list<prey> rightOutcomePreys <- pp.prey_predatorExp[0].get_prey_in_area(stripEnd, rightOuterOLZBound); 
		   	list<predator> rightOutcomePredators <- pp.prey_predatorExp[0].get_predator_in_area(stripEnd, rightOuterOLZBound);		
		   	write(""+mpiRank+" send right OLZ \n  preys= \n " + rightOutcomePreys + "\n predators" + rightOutcomePredators+" \n");
		   	
		   	
		   	write(""+mpiRank+" before send right updateInnerOLZ xxxxxxxx \n");
	   		do MPI_SEND mesg: rightOutcomePreys dest: rightNeighbor stag: 50;  
		   	write(""+mpiRank+" send 1 right \n");        
	   		do MPI_SEND mesg: rightOutcomePredators dest: rightNeighbor stag: 50;
		   	write(""+mpiRank+" send 2 right \n");            
			
    	}
			
    	
		// Receive incominig agents and create them in my part
		// Left outer overlap zone
    	if (leftNeighbor != mpiRank) 
    	{
			write(""+mpiRank+" leftNeighbor update outer receive\n");
			
		   	unknown leftIncomePreys <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 left \n");            
		   	unknown leftIncomePredators <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 left \n");            
		   	
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(leftIncomePreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(leftIncomePredators);
		    
		    write("prey_list : "+prey_list);
		    write("predator_list : "+predator_list);
		    
		}
	
		// Right outer overlap zone
    	if (rightNeighbor != mpiRank) 
    	{
			write(""+mpiRank+" rightNeighbor update outer receive\n");
		   	unknown rightIncomePreys <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 1 right \n");            
		   	unknown rightIncomePredators <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
		   	write(""+mpiRank+" rcv 2 right \n");            
		   	
   		 	list<prey> prey_list <- pp.prey_predatorExp[0].create_prey_from_list(rightIncomePreys);
		    list<predator> predator_list <- pp.prey_predatorExp[0].create_predator_from_list(rightIncomePredators);
		    
		    write("prey_list : "+prey_list);
		    write("predator_list : "+predator_list);
		}	
    }
    
    action finalize // trouver un moyen d'exec le finalize ou bien de détecter la fin de sous-modèle 
    {
    	do MPI_FINALIZE;
    }
}



/* Attention ici le nom de l'expe doit etre le meme que celui donne dans le xml */
experiment ParallelControler type: gui { }
