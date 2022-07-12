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
    
    init
    {
        do MPI_INIT;
		mpi_rank <- MPI_RANK();
		write "mon rank est " + mpi_rank;
	
		mpi_size <- MPI_SIZE();
		write "la size est " + mpi_size;
	
		if (mpi_rank = 0){
	
		    int dst <- 1;
		    list<int> msg <- [10];
		    do MPI_SEND mesg: msg dest: dst stag: 50;
		    write "MPI_SEND done";
		    
		    msg <- [10,20];
		    do MPI_SEND mesg: msg dest: dst stag: 50;
		    write "MPI_SEND done";
		    
		} else {
		    int emet <- 0;
		    list l <- self MPI_RECV [source:: emet, rtag:: 50];
		    write ("------------- recv " + l);
		    
		    list l <- self MPI_RECV [source:: emet, rtag:: 50];
		    write ("------------- recv " + l);
		}
    }
}

experiment com_mpi
{
	output
	{
		monitor "rank" value:mpi_rank;		
	}

}
