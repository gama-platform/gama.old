package MPICommunication;

import java.util.ArrayList;
import java.util.Map;

import MPICommunication.MPIRequest.RequestType;
import MPISkill.IMPISkill;
import mpi.MPI;
import mpi.MPIException;
import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.dev.utils.DEBUG;

public class MPIRequestSender 
{
	static
	{
		DEBUG.OFF();
	}
	public static void sendUpdatedAgentsFromOLZ(Map<Integer, ArrayList<IAgent>> updatedAgentsToSendToNeighbors) 
	{
		for(var updatedAgentsToSendToNeighbor : updatedAgentsToSendToNeighbors.entrySet())
		{
			try {
				int[] requestOrdinal = new int[1];
				requestOrdinal[0] = RequestType.OLZ_AGENT.ordinal();
				
				DEBUG.OUT("sending request RequestType : OLZ_AGENT to " + updatedAgentsToSendToNeighbor.getKey());
				MPI.COMM_WORLD.send(requestOrdinal, 1, MPI.INT, updatedAgentsToSendToNeighbor.getKey(), IMPISkill.REQUEST_TAG); // send request type
				
				DEBUG.OUT("sending :  " + updatedAgentsToSendToNeighbor.getValue());
				DEBUG.OUT("size of msg :  " + updatedAgentsToSendToNeighbor.getValue().size());
				
				byte[] serializedAgentList = serialize(updatedAgentsToSendToNeighbor.getValue());
				MPI.COMM_WORLD.send(serializedAgentList, serializedAgentList.length, MPI.BYTE, updatedAgentsToSendToNeighbor.getKey(), 10); // send request type			
					
			} catch (MPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] serialize(ArrayList<IAgent> agents)
	{
		return new byte[1];
	}
}

