package femto_st.gama.mpi;


import mpi.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.types.IType;
import ummisco.gama.serializer.factory.StreamConverter;

/*
 * 
 * todo #cp $1 gamlChanged.gaml
		#sed -i "/s/MPI-REGEX-WIDTH/$2/g" $1
		pour taille de grille custom
 */

class RequestListenerThread extends Thread
{
	
	private SlaveMPI slaveMPI;
	private IScope scope;
	private volatile boolean running;
	
	private List<Thread> threadList;
	
	RequestListenerThread(SlaveMPI slaveMPI, IScope scope)
	{
		this.slaveMPI = slaveMPI;
		this.scope = scope;
		this.running = true;
		this.threadList = new ArrayList<Thread>();
	}
	
	public void run()
	{
		System.out.println("RequestListenerThread Run begin ");
		while(running)
		{
			try {
				
				byte[] arr = new byte[4];
				Status st = MPI.COMM_WORLD.recv(arr, 1, MPI.INT, MPI.ANY_SOURCE, IMPISkill.REQUEST_TAG); // @1
				System.out.println("st source= "+st.getSource());
				System.out.println("st tag = "+st.getTag());
				
				int requestType = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
				System.out.println("resquestType = " + requestType);
				
				DistributionRequest request = new DistributionRequest(requestType, st.getSource());
				System.out.println("Request " + request.toString());
				
				Thread th = new Thread(new ProcessRequestRunnable(request, this.slaveMPI, this.scope)); // exec the request in a thread
				threadList.add(th);
				
				System.out.println("threadList " + threadList);
				th.start();
				System.out.println("end try run()");
				
			} catch (MPIException e) 
			{
				System.out.println("MPIException "+e);
			}
		}
		
		for(var auto : threadList)
		{
			System.out.println("interrupting "+auto);
			auto.interrupt();
		}
		System.out.println("End of thread");
	}
	
	public void stopThread()
    {
        this.running = false;
    }
	
	public boolean isRunning()
	{
		return this.running;
	}
}

class ProcessRequestRunnable implements Runnable
{
	DistributionRequest request;
	SlaveMPI slaveMPI;
	IScope scope;
	
	ProcessRequestRunnable(DistributionRequest request, SlaveMPI slaveMPI, IScope scope)
	{
		System.out.println("ProcessRequestRunnable constructor");
		this.request = request;
		this.slaveMPI = slaveMPI;
		this.scope = scope;
		
		System.out.println("request constructor = " + request);
		System.out.println("slaveMPI constructor = " + slaveMPI);
	}
	
	ArrayList<IAgent> getAgentsInOuterOLZ() throws MPIException
	{
		System.out.println("getAgentsInOuterOLZ area  begin ");
		
		IMap<Integer,IShape> neighbors_OLZ_Shape_map = (IMap<Integer,IShape>) this.scope.getAgent().getAttribute(IMPISkill.MAP_NEIGHBOR_INNEROLZ);
		IShape outerOLZ = neighbors_OLZ_Shape_map.get(this.request.source);
	
		ArrayList<IAgent> agents = MPISkill.getAgentInArea(this.scope, (IShape) outerOLZ);
		System.out.println("agents in OuterOLZ "+agents);	
		
		return agents;
	}
	
	String convertAgentsToString(ArrayList<IAgent> agents)
	{
		String agentsString = StreamConverter.convertMPIObjectToStream(this.scope, agents);
		System.out.println("message string convert " + agentsString);	
		return agentsString;
	}
	
	void sendMessageSizeToNeighbor(int messageSize) throws MPIException
	{
		int[] size_msg_send = new int[1];
		System.out.println(" messageSize " + messageSize);	
		size_msg_send[0] = messageSize;
		
		System.out.println("sending size_msg_send " + size_msg_send[0]);		
		
		MPI.COMM_WORLD.send(size_msg_send, 1, MPI.INT, request.source, 0); // @2
		System.out.println("size_msg_send sent");
	}
	
	void sendAgentToNeighbor(String agentsToSend) throws MPIException
	{
		byte[] msg = agentsToSend.getBytes();
		int size_msg = agentsToSend.getBytes().length;

		System.out.println("before sending msg "+new String(msg));
		MPI.COMM_WORLD.send(msg, size_msg, MPI.BYTE, request.source, 0);  // @3
		System.out.println("msg sent");
	}
	
	String recvString() throws MPIException
	{
		Status st = MPI.COMM_WORLD.probe(request.source, 0);
        int sizeOfMessage = st.getCount(MPI.CHAR);
        System.out.println("sizeOfMessage " + sizeOfMessage);
        
        char[] arrayRecv = new char[sizeOfMessage];
        MPI.COMM_WORLD.recv(arrayRecv, sizeOfMessage, MPI.CHAR, MPI.ANY_SOURCE, 0);
        
        String strRecv = String.valueOf(arrayRecv);
        System.out.println("String received = " + strRecv);
        
		return strRecv;
	}
	
	public void run()
	{
		System.out.println("ProcessRequestRunnable run begin");
		try 
		{
			System.out.println("Request RequestType = "+request.requestType.name());
			switch(request.requestType)
			{
				case GET_AGENT_IN_INNER_OLZ : 
					System.out.println("case GET_AGENT_IN_INNER_OLZ ");
					ArrayList<IAgent> agents =  getAgentsInOuterOLZ();	
					String agentsToSend = convertAgentsToString(agents);
					sendAgentToNeighbor(agentsToSend);
					break;
					
				case GATHER_ATTRIBUTE_FROM_EACH_PROCESS :

					System.out.println("case GATHER_ATTRIBUTE_FROM_EACH_PROCESS ");
					String nameSpecie = recvString();
					System.out.println("nameSpecie recv " + nameSpecie);
					String attribute = recvString();
					System.out.println("attribute recv " + attribute);
					System.out.println("before gatherAttributeFromMainModel in recv ");
					List<Object> attributes = MPISkill.gatherAttributeFromMainModel(scope, nameSpecie, attribute);
					
					String message = StreamConverter.convertMPIObjectToStream(scope, attributes);
					System.out.println("message to be send to gather = " + message );
					MPI.COMM_WORLD.gatherv(message.getBytes(), message.getBytes().length, MPI.BYTE, 0);
					
					System.out.println("attributes found in current process " + attributes);
					for (Object value : attributes)
					{
						System.out.println("value "+value);
					}
					
					break;
					
				default: 
					return;
			}
			
		} catch (MPIException e) 
		{
			System.out.println("MPIException "+e);
		}

		System.out.println("END OF run()");		
    }
}

class DistributionRequestSender
{	
	// send request to a specific neighbor
	static void sendRequestTypePtoP(RequestType requestType, int neighborRank) throws MPIException
	{
		System.out.println("sendRequestTypePtoP = " + requestType.toString());
		int[] requestOrdinal = new int[1];
		requestOrdinal[0] = requestType.ordinal();
		System.out.println("requestOrdinal = "+requestOrdinal[0]);
		
		MPI.COMM_WORLD.send(requestOrdinal, 1, MPI.INT, neighborRank, IMPISkill.REQUEST_TAG); // @1 send request
		System.out.println("requestOrdinal sent"); 
	}
	
	// send request to a group
	static void sendRequestTypeGroup(RequestType requestType) throws MPIException
	{
		System.out.println("sendRequestTypeGroup = " + requestType.toString());
		int[] requestOrdinal = new int[1];
		requestOrdinal[0] = requestType.ordinal();

		System.out.println("requestOrdinal = " + requestOrdinal[0]);
		
        int sizeWorldCom = MPI.COMM_WORLD.getSize();
		for (int processRank = 0; processRank < sizeWorldCom; processRank++) // implement tree sender + receiver
		{
			MPI.COMM_WORLD.send(requestOrdinal, 1, MPI.INT, processRank, IMPISkill.REQUEST_TAG);
		}
	}
	
	static ArrayList<IAgent> receiveAgentsFromNeighborOLZ(IScope scope, int neighborRank) throws MPIException
	{
		Status st = MPI.COMM_WORLD.probe(neighborRank,0);
        int sizeOfMessage = st.getCount(MPI.BYTE);
        System.out.println("sizeOfMessage " + sizeOfMessage);
        byte[] message = new byte[sizeOfMessage];
		
		System.out.println("before receiving message");
		MPI.COMM_WORLD.recv(message, sizeOfMessage, MPI.BYTE, neighborRank, 0); // @3 getting the agent's string representation
		System.out.println("message "+new String(message));
		
		ArrayList<IAgent> agentsInNeighborOLZ = (ArrayList<IAgent>) StreamConverter.convertMPIStreamToObject(scope, new String(message)); // convert the string to agent lists
		System.out.println("IAgent : \n\n");
		
		if(agentsInNeighborOLZ != null) // agent already in the current process so we dont unserialized it -> null
		{
			agentsInNeighborOLZ.removeAll(Collections.singleton(null));
		}
		
		return agentsInNeighborOLZ;
	}
	
	
	public static ArrayList<IAgent> getAgentsInNeighobrOLZ(IScope scope, int neighborRank)
	{	
		try 
		{
			sendRequestTypePtoP(RequestType.GET_AGENT_IN_INNER_OLZ, neighborRank);
			ArrayList<IAgent> AgentsInNeighborOLZ = DistributionRequestSender.receiveAgentsFromNeighborOLZ(scope, neighborRank);
			
			for(var auto : AgentsInNeighborOLZ)
			{
				System.out.println("auto class name = "+auto.getClass().getName());
				System.out.println("auto index "+auto.getIndex());
				System.out.println("auto getGamlType "+auto.getGamlType());
				System.out.println("auto getName "+auto.getName());
				System.out.println("auto getUniqueID "+auto.getUniqueID());
				//System.out.println("auto getGamlType "+auto.restoreTo(scope, scope.getSimulation().getPopulation()));
			}
			//System.out.println("rcvMesg Object "+ rcvMesg);
			
			return AgentsInNeighborOLZ;
			
		} catch (MPIException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static List<Object> sendGatherRequest(IScope scope, RequestType requestType, String specieName, String attribute)
	{
		//try 
		//{
			/*System.out.println("GATHER 1");
			sendRequestTypeGroup(RequestType.GATHER_ATTRIBUTE_FROM_EACH_PROCESS);

			System.out.println("GATHER 2");
	        int sizeWorldCom = MPI.COMM_WORLD.getSize();
			for (int processRank = 0; processRank < sizeWorldCom; processRank++) // implement tree sender + receiver
			{
				MPI.COMM_WORLD.send(specieName.toCharArray(), specieName.length(), MPI.CHAR, processRank, 0);
				MPI.COMM_WORLD.send(attribute.toCharArray(), attribute.length(), MPI.CHAR, processRank, 0);
			}
			
			System.out.println("GATHER 3");
	        byte[] message = new byte[2048];

			System.out.println("GATHER 4");
			MPI.COMM_WORLD.gatherv(message, 2048, MPI.BYTE, 0);

			System.out.println("GATHER 5");
			ArrayList<Object> allAttributes = (ArrayList<Object>) StreamConverter.convertMPIStreamToObject(scope, new String(message)); // convert the string to List<Object>

			System.out.println("GATHER 6");
			
			System.out.println("allAttributes " + allAttributes);
			System.out.println("END SEND GATHER REQUEST ");
			
			return allAttributes;*/
			
			//return null;
		
		/*} catch (MPIException e) 
		{
			e.printStackTrace();
		}*/
		
		return new ArrayList<Object>();
	}
}

@species(name = "SlaveMPI",  skills={ IMPISkill.MPI_NETWORK })
@doc ("SlaveMPI to process step and communication between others slave")
@vars({
	@variable (
			name = IMPISkill.OUTER_OLZ_AREA, 
			type = IType.GEOMETRY, 
			init = "",
			doc = { @doc ("Shape of the outer olz area")}),
	@variable (
			name = IMPISkill.INNER_OLZ_AREA, 
			type = IType.GEOMETRY, 
			init = "",
			doc = { @doc ("Shape of the inner olz area")}),
	@variable (
			name = IMPISkill.MAP_NEIGHBOR_INNEROLZ, 
			type = IType.MAP, 
			init = "",
			doc = { @doc ("Map with neighbor's rank :: innerOLZ connected to his area ")}),
	@variable (
			name = IMPISkill.MY_RANK, 
			type = IType.INT, 
			init = "",
			doc = { @doc ("Rank of the slave")}),
})
public class SlaveMPI extends GamlAgent {
	
	RequestListenerThread listener;
	int myRank;
	
	public SlaveMPI(IPopulation<? extends IAgent> s, int index) 
	{
		super(s, index);
		try 
		{
			System.out.println("USING MPI");
			/*final String[] arg = {};
			MPI.InitThread(arg, MPI.THREAD_MULTIPLE);
			
			System.out.println("MPI.COMM_WORLD.getRank() = "+MPI.COMM_WORLD.getRank());
			UniqueIDProviderService.getInstance().initMPI(MPI.COMM_WORLD.getRank());*/
			myRank = MPI.COMM_WORLD.getRank();
			
			System.out.println("Hello");
			System.out.println("MyRank = " + myRank);
			System.out.println("SizeNetwork = " + MPI.COMM_WORLD.getSize());
		} catch (MPIException | IOException e) 
		{
			System.out.println("MPIException " + e);
		}
		
		// get neighbors from grid in the model -> initialize SubOLZ
	}
	
	@getter(IMPISkill.MY_RANK)
	public int getMyRank()
	{
		return myRank;
	}
	
	@Override
	@getter (IKeyword.SHAPE)
	public IShape getGeometry()
	{
		return geometry;
	}

	@Override
	@setter(IMPISkill.MAIN_AREA)
	public void setGeometry(IShape shape)
	{
		super.setGeometry(shape);
	}
	
	@getter(IMPISkill.OUTER_OLZ_AREA)
	public IShape getOuterOLZArea()
	{
		return (IShape) getAttribute(IMPISkill.OUTER_OLZ_AREA);
	}
	
	@setter(IMPISkill.OUTER_OLZ_AREA)
	public void setOuterOLZArea(IShape shape)
	{
		setAttribute(IMPISkill.OUTER_OLZ_AREA, shape);
	}
	
	@getter(IMPISkill.INNER_OLZ_AREA)
	public IShape getInnerOLZArea()
	{
		return (IShape) getAttribute(IMPISkill.INNER_OLZ_AREA);
	}
	
	@setter(IMPISkill.INNER_OLZ_AREA)
	public void setInnerOLZArea(IShape shape)
	{
		setAttribute(IMPISkill.INNER_OLZ_AREA, shape);
	}
	
	@action(name = IMPISkill.GET_AGENT_IN_NEIGHBOR_INNER_OLZ,
			args = { @arg (
					name = IMPISkill.NEIGHBORS_RANK,
					type = IType.INT,
					doc = @doc ("neighbor rank")),
			}
	)
	public ArrayList<IAgent> getAgentInNeighborInnerOLZ(IScope scope) 
	{
		ArrayList<IAgent> agentsInInnerOLZ = DistributionRequestSender.getAgentsInNeighobrOLZ(scope, (int) scope.getArg(IMPISkill.NEIGHBORS_RANK, IType.INT));
		System.out.println("returning getAgentInNeighborInnerOLZ to gaml = "+agentsInInnerOLZ);
		
		return agentsInInnerOLZ;
	}
	
	@action(name = IMPISkill.START_LISTENER)
	public void startListener(final IScope scope)
	{
		if(listener != null)
		{
			System.out.println("Listener already started, try stopListener to stop it ");
			return;
		}
		System.out.println("startListener ");
		listener = new RequestListenerThread(this, scope);
		listener.start();
		System.out.println("startListener started");
	}

	@action(name = IMPISkill.STOP_LISTENER)
	public void stopListener(final IScope scope)
	{
		if(listener == null)
		{
			System.out.println("Listener not started, try startListener to start it");
			return;
		}
		System.out.println("stopListener ");
		listener.stopThread();
		listener = null;
	}
	
	@action(name = IMPISkill.GATHER_ATTRIBUTE_FROM_EACH_PROCESS,
			args = { @arg (
					name = IMPISkill.SPECIE_NAME_IN_MAIN_MODEL,
					type = IType.STRING,
					doc = @doc ("species to gather attribute from")),
					
					@arg (
						name = IMPISkill.ATTRIBUTE_TO_GATHER,
						type = IType.STRING,
						doc = @doc ("attribute from the species to gather"))
			})
	public List<Object> gatherAttributeFromEachProcess(final IScope scope)
	{
		String specieName = (String) scope.getArg("specieName", IType.STRING);
		String attribute = (String) scope.getArg("attribute", IType.STRING);

		System.out.println("sendGatherRequest for specie " + specieName + " for attribute :  " + attribute);
		return DistributionRequestSender.sendGatherRequest(scope, RequestType.GATHER_ATTRIBUTE_FROM_EACH_PROCESS, specieName, attribute);
	}
	
	
	@action(name = IMPISkill.UPDATE_COPY_ATTRIBUTE,
			args = { @arg (
					name = IMPISkill.MESG,
					type = IType.LIST,
					doc = @doc ("mesg message"))})
	public IList<IAgent> updateIsCopyAttribute(final IScope scope)
	{
		final IList<IAgent> agentsToUpdate = (IList<IAgent>) scope.getArg(IMPISkill.MESG, IType.LIST);
		IShape mainShape = this.getGeometry();
		
		for(var agent : agentsToUpdate)
		{
			agent.setIsCopy(agent.intersects(mainShape) ? true : false); 
			System.out.println(agent.getName() + " is a copy : " + agent.getIsCopy());
		}
		
		return agentsToUpdate;
	}
}