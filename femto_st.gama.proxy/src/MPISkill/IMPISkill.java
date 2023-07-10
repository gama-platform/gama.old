package MPISkill;

public interface IMPISkill {
	String MPI_NETWORK = "MPI_Network";

	String MPI_INIT = "MPI_INIT";
    String MPI_INIT_DONE = "MPI_INIT_DONE";
	String MPI_RANK = "MPI_RANK";
	String MPI_SIZE = "MPI_SIZE";
    String MPI_FINALIZE = "MPI_FINALIZE";
    String BARRIER = "MPI_BARRIER";
	
	String MPI_SEND = "MPI_SEND";
	String MESG = "mesg";
	String SNDSIZE = "sndsize";
	String DEST = "dest";
	String STAG = "stag";
	
	String MPI_RECV = "MPI_RECV";
	String RCVSIZE = "rcvsize";
	String SOURCE = "source";
	String RTAG = "rtag";
}
