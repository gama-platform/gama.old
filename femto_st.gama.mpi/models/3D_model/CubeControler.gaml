/**
* Name: mpi controler 3D
* Author: LG
* Description: Controller of Cube
*/
model controler

import "Cube.gaml" as pp

global skills: [MPI_Network] 
{
		
    int mpiRank <- 0;
    int netSize <- 0;
    
    int nb_agent_total <- 50;
    int nb_agent_per_proc;
	
    int current_step <- 0;
    int nbLoop <- 20;
	
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
    float OLZSize <- 10.0; // mettre à la valeur de la perception la plus eleve du modele 
    
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
	
    init 
    {
    	do runModel;
        do MPI_INIT;
		
	    mpiRank <- MPI_RANK();
	     
	    netSize <- MPI_SIZE();
	    
    	do init_sub_simulation;
    	
    
    	nb_agent_per_proc <- nb_agent_total / netSize;
    	write("nb_agent_per_proc : "+nb_agent_per_proc+"\n");
    	p <- pp.movingExp[0].create_cells(nb_agent_per_proc);
    	
	    modelSize <- pp.movingExp[0].get_size_model();
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
		
		/* Defines overlap bounds */
		
    	// inner (OLZs on your side)
    	// outer (OLZs on their side)
    	
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
    		current_step <- current_step + 1;
		}
		
		list<cell> generic_in_band <- pp.movingExp[0].get_cell_list_in_area(stripStart,stripEnd);
		write(""+mpiRank+" generic_in_band: "+ generic_in_band +"\n");
		write(""+mpiRank+" b agent : "+ length(generic_in_band) +"\n");
		
    	do finalize;
    }
    
    action init_sub_simulation
    {
    	create pp.movingExp;
    }
    
    action runModel
    {
		ask (pp.movingExp collect each.simulation)
	    {
			do _step_;
	    }
    }
    
    action runMpi_even
    { 	
    	write(""+mpiRank+" cleanOuterOLZ ****\n");
    	do cleanOuterOLZ;
    	
    	write(""+mpiRank+" updateOuterOLZ_even ****\n");
    	do updateOuterOLZ_even;
    	
    	write(""+mpiRank+" runModel****\n");
    	do runModel;
    	
    	write(""+mpiRank+" updateInnerOLZ_even ****\n");
    	do updateInnerOLZ_even;
    }
    
    action runMpi_odd
    { 	
    	write(""+mpiRank+" cleanOuterOLZ ****\n");
    	do cleanOuterOLZ;
    	
    	write(""+mpiRank+" updateOuterOLZ ****\n");
    	do updateOuterOLZ_odd;
    	
    	write(""+mpiRank+" runModel****\n");
    	do runModel;
    	
    	write(""+mpiRank+" updateInnerOLZ ****\n");
    	do updateInnerOLZ_odd;
    }
    
    action cleanOuterOLZ
   	{
		write(""+mpiRank+" cleanOuterOLZ\n");
   		int deleted_agents <- pp.movingExp[0].delete_agent_not_in_band(stripStart, stripEnd);
   		write(""+mpiRank+" nb deleted "+deleted_agents+"\n");
    }
    
    action updateOuterOLZ_even
    { 
		if (leftNeighbor != mpiRank) // Only nodes with a left part of the env
		{ 
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(stripStart, leftInnerOLZBound); 
	   		do MPI_SEND mesg: generic_species_list dest: leftNeighbor stag: 50;      	
		}
	
		// Right inner overlap zone
		if (rightNeighbor != mpiRank) // Only nodes with a right part of the env
		{ 	
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(rightInnerOLZBound, stripEnd); 
	   		do MPI_SEND mesg: generic_species_list dest: rightNeighbor stag: 50;      
    	}
		   
		
		// Receive overlap zones from neighbors		 
		// Left outer overlap zone 
		if (leftNeighbor != mpiRank)
		{
		   	unknown generic_species_list <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];		   	
		   	list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);   
        }
	
		// Right outer overlap zone
        if ( rightNeighbor != mpiRank)
        {
		   	unknown generic_species_list <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
	        list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);
        }
    }
    
	action updateInnerOLZ_even
    {
    
	 	// Gather agents that have moved in the OLZ and send then to the neighbors	
		// Left outer overlap zone
		if (leftNeighbor != mpiRank) 
		{
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(leftOuterOLZBound, stripStart); 
		   	do MPI_SEND mesg: generic_species_list dest: leftNeighbor stag: 50;      
    	}
		
    	// Right outer overlap zone
    	if (rightNeighbor != mpiRank)
		{
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(stripEnd, rightOuterOLZBound); 
			do MPI_SEND mesg: generic_species_list dest: rightNeighbor stag: 50;      
    	}
			
    	
		// Receive incominig agents and create them in my part
		// Left outer overlap zone
    	if (leftNeighbor != mpiRank) 
    	{
		   	unknown generic_species_list <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];
	        list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);
		}
	
		// Right outer overlap zone
    	if (rightNeighbor != mpiRank) 
    	{
		   	unknown generic_species_list <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
	        list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);
		}
    }
    
    action updateOuterOLZ_odd
    {    
		
		// Receive overlap zones from neighbors		 
		// Left outer overlap zone 
		if (leftNeighbor != mpiRank)
		{
		   	unknown generic_species_list <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];		   	
		   	list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);
        }
	
		// Right outer overlap zone
        if ( rightNeighbor != mpiRank)
        {
		   	unknown generic_species_list <- self MPI_RECV [source:: rightNeighbor, rtag:: 50];
	        list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);
        }
        
        if (leftNeighbor != mpiRank) // Only nodes with a left part of the env
		{ 
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(stripStart, leftInnerOLZBound); 
	   		do MPI_SEND mesg: generic_species_list dest: leftNeighbor stag: 50;      	
		}
	
		// Right inner overlap zone
		if (rightNeighbor != mpiRank) // Only nodes with a right part of the env
		{ 	
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(rightInnerOLZBound, stripEnd); 
	   		do MPI_SEND mesg: generic_species_list dest: rightNeighbor stag: 50;      
    	}
    }
    
	action updateInnerOLZ_odd
    {
			
		// Receive incominig agents and create them in my part
		// Left outer overlap zone
    	if (leftNeighbor != mpiRank) 
    	{
		   	unknown generic_species_list <- self MPI_RECV [source:: leftNeighbor, rtag:: 50];   	
	        list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);
		}
	
		// Right outer overlap zone
    	if (rightNeighbor != mpiRank) 
    	{
		   	unknown generic_species_list <- self MPI_RECV [source:: rightNeighbor, rtag:: 50]; 	
	        list<cell> generic_species_list_created <- pp.movingExp[0].create_cell_from_list(generic_species_list);
		}
		
		// Gather agents that have moved in the OLZ and send then to the neighbors	
		// Left outer overlap zone
		if (leftNeighbor != mpiRank) 
		{
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(leftOuterOLZBound, stripStart); 
		   	do MPI_SEND mesg: generic_species_list dest: leftNeighbor stag: 50;      	
    	}
		
    	// Right outer overlap zone
    	if (rightNeighbor != mpiRank)
		{
		   	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(stripEnd, rightOuterOLZBound); 
			do MPI_SEND mesg: generic_species_list dest: rightNeighbor stag: 50;      
    	}
    }
    
    action finalize // trouver un moyen d'exec le finalize ou bien de détecter la fin de sous-modèle 
    {
    	do MPI_FINALIZE;
    	
    	list<cell> generic_species_list <- pp.movingExp[0].get_cell_list_in_area(stripStart, stripEnd);
    	write(""+mpiRank+"=================================================================FINAL LIST "+generic_species_list+"\n");
    }
}


/* Attention ici le nom de l'expe doit etre le meme que celui donne dans le xml */
experiment CubeControler type: gui { }
