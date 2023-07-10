/**
* Name: firstmpi
* Author: nicolas
* Description: 
* Tags: Tag1, Tag2, TagN
*/

model firstmpi


global skills:[MPI_Network]
{
	int mpi_rank <- 0;
	int mpi_size <- 0;
	string file_name;
	
	init
	{
		
		mpi_rank <- MPI_RANK();
		mpi_size <- MPI_SIZE();

		file_name <- "log"+mpi_rank+".txt";
		do clearLogFile();
		
	    do writeLog("mon rank est : " + mpi_rank);
		do writeLog("la size est : " + mpi_size);

	}
    
    action writeLog(string log)
	{
		save log type: text to: file_name rewrite:false;
	}
	
	action clearLogFile
	{
		save "" type: text to: file_name rewrite:true;
	}
}

experiment test_mpi
{
	output
	{
		monitor "rank" value:mpi_rank;		
	}

}
