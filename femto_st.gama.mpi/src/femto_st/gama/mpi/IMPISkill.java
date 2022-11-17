package femto_st.gama.mpi;

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
	
	String OUTER_OLZ_AREA = "outer_OLZ_area";	
	String INNER_OLZ_AREA = "inner_OLZ_area";	
	String MAP_NEIGHBOR_INNEROLZ = "map_neighbor_innerOLZ";	
	String MAIN_AREA = "shape";
	
	String GET_AGENT_IN_NEIGHBOR_OUTER_OLZ = "getAgentInNeighborOuterOLZ";
	String GET_AGENT_IN_NEIGHBOR_INNER_OLZ = "getAgentInNeighborInnerOLZ";
	String GET_AGENT_IN_NEIGHBOR_MAIN_AREA = "getAgentInNeighborMainArea";
	String GET_ALL_AGENT_IN_NEIGHBOR = "getAllAgentInNeighbor";
	String GATHER_ATTRIBUTE_FROM_MAIN_MODEL = "gatherAttributeFromMainModel";
	String GATHER_ATTRIBUTE_FROM_EACH_PROCESS = "gatherAttributeFromEachProcess";
	String UPDATE_COPY_ATTRIBUTE = "updateIsCopyAttribute";
	
	String NEIGHBORS_RANK = "neighbor_rank";
	String MY_RANK = "myRank";

	String START_LISTENER = "start_listener";
	String STOP_LISTENER = "stop_listener";

	String SPECIE_NAME_IN_MAIN_MODEL = "specieName";
	String ATTRIBUTE_TO_GATHER = "attribute";
	
	Integer REQUEST_TAG = 1; // tag to send request (GettingAgent, Gather, etc....)
}
