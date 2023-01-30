package femto_st.gama.mpi;

import java.util.ArrayList;
import java.util.List;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.serializer.factory.StreamConverter;
import msi.gama.runtime.IScope;

public class DistributionHelper 
{
	static String convertAgentsToString(IScope scope, List<IAgent> agents)
	{
		String agentsString = StreamConverter.convertMPIObjectToStream(scope, agents);
		System.out.println("message string convert " + agentsString);	
		return agentsString;
	}
	
	static void sendAgentToNeighbor(String agentsToSend, int source) throws MPIException
	{
		byte[] msg = agentsToSend.getBytes();
		int size_msg = agentsToSend.getBytes().length;

		System.out.println("before sending msg "+new String(msg));
		MPI.COMM_WORLD.send(msg, size_msg, MPI.BYTE, source, 0);  // @3
		System.out.println("msg sent");
	}
	
	static String recvString(int source) throws MPIException
	{
		Status st = MPI.COMM_WORLD.probe(source, 0);
        int sizeOfMessage = st.getCount(MPI.CHAR);
        System.out.println("sizeOfMessage " + sizeOfMessage);
        
        char[] arrayRecv = new char[sizeOfMessage];
        MPI.COMM_WORLD.recv(arrayRecv, sizeOfMessage, MPI.CHAR, MPI.ANY_SOURCE, 0);
        
        String strRecv = String.valueOf(arrayRecv);
        System.out.println("String received = " + strRecv);
        
		return strRecv;
	}
}
