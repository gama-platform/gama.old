/**
* Name: mpi controler for shapefile model
* Author: Lucas Grosjean
* Description: Controler of SHP.gaml
*/
model controler

import "SHP.gaml" as pp

global skills: [MPI_Network] 
{
    int nb_agent_total <- 10;
    int nbLoop <- 20;
	int rank;
	
    init 
    {
		do MPI_INIT;
    	do init_sub_simulation;
    	rank <- MPI_RANK();
    	
    	write("rank = "+rank+" ------------------------------------------------------");
    	
    	if(rank = 0)
    	{
    		create master_MPI with: [mpiRank:: rank];
    	}else
    	{
    		create slave_MPI  with: [mpiRank:: rank];
    	}
    }
    
    reflex write_cycle when: cycle < nbLoop
    {
    	write("current cycle  "+cycle+"\n");
    }
    
    reflex end_simulation when: cycle = nbLoop + 1 
    {
    	write("end of simulation ----------------------- \n");
    	do die; 	
    }
    
    action init_sub_simulation
    {
    	create pp.movingExp;
    }
}

species MPI skills: [MPI_Network] 
{
	int mpiRank;
	int netSize;
	list<int> neighbors;
	int otherNeighbor;
	
	init
	{
	    netSize <- MPI_SIZE();
	    do create_agent(nb_agent_total/netSize);
	    
	    otherNeighbor <- mpiRank - 1;
    	if (otherNeighbor = -1)
    	{ 
    		otherNeighbor <- 1;
    	}
	    
	    write(""+mpiRank+" mon rank est " + ""+mpiRank+"\n") ;
    	write(""+mpiRank+" netSize = "+netSize+"\n");
    	write(""+mpiRank+" neighbors = "+neighbors+"\n");
    	write(""+mpiRank+" otherNeighbor = "+otherNeighbor+"\n");
    	write(""+mpiRank+" cycle = "+cycle+"\n");
    	write(""+mpiRank+" nbLoop = "+nbLoop+"\n");
	}
	
	action create_agent(int nb)
	{
    	let p <- pp.movingExp[0].create_agents(nb);
	}
    
    action add_neighbors(int rank)
    {
    	add rank to:neighbors;
    }
    
    action runModel
    {
		ask (pp.movingExp collect each.simulation)
	    {
			do _step_;
	    }
    }
	
	action cleanOuterOLZ
   	{
   		write(""+mpiRank+" cleanOuterOLZ\n");
   		int deleted_agents <- pp.movingExp[0].delete_agent_not_in_band(mpiRank);
   		write(""+mpiRank+" nb deleted "+deleted_agents+"\n");
    }
    
    action updateOuterOLZ{}
    action updateInnerOLZ{}
	
	reflex runMpi
	{
		//write(""+mpiRank+" cleanOuterOLZ ****\n");
    	do cleanOuterOLZ;
    	
    	//write(""+mpiRank+" updateOuterOLZ ****\n");
    	do updateOuterOLZ;
    	
    	//write(""+mpiRank+" runModel****\n");
    	do runModel;
    	
    	//write(""+mpiRank+" updateInnerOLZ ****\n");
    	do updateInnerOLZ;	
	}
	
	reflex finalize when: cycle = nbLoop
    {
    	do MPI_FINALIZE;
    	list<agent_common> generic_species_list <- pp.movingExp[0].get_agent_list_in_area(mpiRank);
    	write(""+mpiRank+"=================================================================FINAL LIST "+generic_species_list+"\n");
    	do die;
    } 
}


species master_MPI  parent: MPI
{
	action updateOuterOLZ
    { 
	   	list<agent_common> generic_species_list <- pp.movingExp[0].get_agent_list_in_OLZ_outer(); 
		do MPI_SEND mesg: generic_species_list dest: otherNeighbor stag: 50;   
		write(""+mpiRank+" sending : "+generic_species_list+"\n");   	
		
		unknown generic_species_list <- self MPI_RECV [source:: otherNeighbor, rtag:: 50];		   	
	   	list<agent_common> generic_species_list_created <- pp.movingExp[0].create_agents_from_list(generic_species_list);
		write(""+mpiRank+" recv : "+generic_species_list_created+"\n");   	
    }
    
	action updateInnerOLZ
    {
	   	list<agent_common> generic_species_list <- pp.movingExp[0].get_agent_list_in_OLZ_inner(); 
		do MPI_SEND mesg: generic_species_list dest: otherNeighbor stag: 50;      	
		write(""+mpiRank+"sending : "+generic_species_list+"\n");   	
		
		unknown generic_species_list <- self MPI_RECV [source:: otherNeighbor, rtag:: 50];		   	
	   	list<agent_common> generic_species_list_created <- pp.movingExp[0].create_agents_from_list(generic_species_list);
		write(""+mpiRank+" recv : "+generic_species_list_created+"\n");   	
    }
}

species slave_MPI  parent: MPI
{
	action updateOuterOLZ
    { 
	   	list<agent_common> generic_species_list <- pp.movingExp[0].get_agent_list_in_OLZ_outer(); 
		do MPI_SEND mesg: generic_species_list dest: otherNeighbor stag: 50;   
		write(""+mpiRank+"sending : "+generic_species_list+"\n");   	
		
		unknown generic_species_list <- self MPI_RECV [source:: otherNeighbor, rtag:: 50];		   	
	   	list<agent_common> generic_species_list_created <- pp.movingExp[0].create_agents_from_list(generic_species_list);
		write(""+mpiRank+" recv : "+generic_species_list_created+"\n");   	
    }
    
	action updateInnerOLZ
    {
	   	list<agent_common> generic_species_list <- pp.movingExp[0].get_agent_list_in_OLZ_inner(); 
		do MPI_SEND mesg: generic_species_list dest: otherNeighbor stag: 50;      	
		write(""+mpiRank+"sending : "+generic_species_list+"\n");   	
		
		unknown generic_species_list <- self MPI_RECV [source:: otherNeighbor, rtag:: 50];		   	
	   	list<agent_common> generic_species_list_created <- pp.movingExp[0].create_agents_from_list(generic_species_list);
		write(""+mpiRank+" recv : "+generic_species_list_created+"\n");   	
    }
}


/* Attention ici le nom de l'expe doit etre le meme que celui donne dans le xml */
experiment SHPControler type: gui { }
