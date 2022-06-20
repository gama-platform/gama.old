package femto_st.gama.mpi;


import mpi.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import msi.gama.common.UniqueIDProviderService;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
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
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.factory.StreamConverter;

/*
 * 
 * todo check Scheduler       
 * 1 Thread with Nx(N-1) requests >>>>> N Thread with (N-1) requests
  		-> Lancer un thread d'écoute au début de la simulation
 * 
 * une cellule -> une liste de cellule (modulable)
 		map : cellule_name  ->  rank of owner
 * 
 * 
 * todo #cp $1 gamlChanged.gaml
		#sed -i "/s/MPI-REGEX-WIDTH/$2/g" $1
		
		pour taille de grille custom
		
 * todo check NetworkSkills
 * todo define type d'experiment (MPI_experiment)
 * todo shapefile au lieu de grille
 * 
 * todo : initialisation de la simulation   (à discuter avec Alexis)
 		-> parralélisation de l'init de la simulation
 		
 		
 * todo : si agent se situe sur une OLZ à l'init : donner un uniqueID de l'une des 2 zones et la transmettre à l'autre proc		
 * done : Check si l'agent existe déja avant de l'envoyé 
 */


enum RequestType
{
	GET_ALL_AGENT,
	GET_AGENT_IN_INNER_OLZ,
	GET_AGENT_IN_OUTER_OLZ,
	GET_AGENT_IN_MAIN_AREA
}

class AgentRequest implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	RequestType requestType;
	int source;
	
	AgentRequest(RequestType requestType, int source)
	{
		this.requestType = requestType;
		this.source = source;
	}
	AgentRequest(int requestTypeOrdinal, int source)
	{
		System.out.println("requestTypeOrdinal = "+requestTypeOrdinal);
		this.requestType = RequestType.values()[requestTypeOrdinal];
		this.source = source;
	}
	
	
	AgentRequest()
	{
	}
	
	public void writeObject(ObjectOutputStream oos) throws IOException 
	{
		System.out.println("writeObject begin");
		//oos.defaultWriteObject();
		System.out.println("requestType write");
        oos.writeInt(requestType.ordinal());
		System.out.println("source write");
        oos.writeInt(source);
        //oos.flush();
    }

	public void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
    {
		System.out.println("readObject begin");
		//ois.defaultReadObject();
		System.out.println("requestType read");
    	Integer requestType = ois.readInt(); // requestType
		System.out.println("requestType read");
    	Integer source = ois.readInt(); // source

		System.out.println("requestType creating");
        this.requestType = RequestType.values()[requestType];
		System.out.println("requestType creating");
        this.source = source;
        
	    System.out.println("Request received = "+this);    
    }

    public String toString(){
    	return "source: " + this.source + "   ////    RequestType : "+requestType.name();
    }
}

class RequestListenerThread extends Thread
{
	
	private SlaveMPI slaveMPI;
	private int myRank;
	private IScope scope;
	private volatile boolean running;
	
	private List<Thread> threadList;
	
	RequestListenerThread(SlaveMPI slaveMPI, IScope scope, int myRank)
	{
		this.slaveMPI = slaveMPI;
		this.myRank = myRank;
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
				
				int value = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
				System.out.println("value = "+value);
				AgentRequest request = new AgentRequest(value, st.getSource());
				System.out.println("Request " + request.toString());
				Thread th = new Thread(new ProcessRequestRunnable(request, this.slaveMPI, this.scope));
				threadList.add(th);
				System.out.println("threadList " + threadList);
				th.start();
				System.out.println("??????");
				
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
	AgentRequest request;
	SlaveMPI slaveMPI;
	IScope scope;
	
	ProcessRequestRunnable(AgentRequest request, SlaveMPI slaveMPI, IScope scope)
	{
		System.out.println("ProcessRequestRunnable constructor");
		this.request = request;
		this.slaveMPI = slaveMPI;
		this.scope = scope;
		
		System.out.println("request constructor = " + request);
		System.out.println("slaveMPI constructor = " + slaveMPI);
	}
	
	/*void getAgentInMainArea() throws MPIException
	{
		ArrayList<IAgent> agents = DistributionUtils.getAgentInArea(this.scope, (IShape) slaveMPI.getAttribute(IMPISkill.MAIN_AREA));
		String message = StreamConverter.convertMPIObjectToStream(this.scope, agents);

		int[] size_msg_send = new int[1];
		size_msg_send[0] = message.length();	
		
		MPI.COMM_WORLD.send(size_msg_send, 1, MPI.INT, request.source, 0);
		
		byte[] msg = message.getBytes();
		int size_msg = message.getBytes().length;
		MPI.COMM_WORLD.send(msg, size_msg, MPI.BYTE, request.source, 0);
		System.out.println("msg sent");
	}
	
	void getAgentInOuterOLZ() throws MPIException
	{
		System.out.println("getAgentInOuterOLZ begin ");
		ArrayList<IAgent> agents = DistributionUtils.getAgentInArea(this.scope, (IShape) slaveMPI.getAttribute(IMPISkill.OUTER_OLZ_AREA));
		String message = StreamConverter.convertMPIObjectToStream(this.scope, agents);
		
		int[] size_msg_send = new int[1];
		size_msg_send[0] = message.length();	
		
		MPI.COMM_WORLD.send(size_msg_send, 1, MPI.INT, request.source, 0);
		
		byte[] msg = message.getBytes();
		int size_msg = message.getBytes().length;
		MPI.COMM_WORLD.send(msg, size_msg, MPI.BYTE, request.source, 0);
		System.out.println("msg sent");
	}
	
	void getAgentInInnerOLZ() throws MPIException
	{
		System.out.println("getAgentInInnerOLZ begin ");
		ArrayList<IAgent> agents;
		String message;

		System.out.println("248");
		IShape shape = (IShape) slaveMPI.getAttribute(IMPISkill.INNER_OLZ_AREA);
		System.out.println("shape getAgentInInnerOLZ = "+shape);
		
		agents = DistributionUtils.getAgentInArea(this.scope, (IShape) shape);
		System.out.println("agents in area "+agents);
		
		message = StreamConverter.convertMPIObjectToStream(this.scope, agents);
		System.out.println("message string convert "+message);	
		
		System.out.println("before assigning size_msg_send ");	
		
		int[] size_msg_send = new int[1];
		System.out.println("message.length() "+message.length());	
		size_msg_send[0] = message.length();
		
		System.out.println("sending size_msg_send "+size_msg_send[0]);		
		
		MPI.COMM_WORLD.send(size_msg_send, 1, MPI.INT, request.source, 0);
		System.out.println("size_msg_send sent");
		
		byte[] msg = message.getBytes();
		int size_msg = message.getBytes().length;

		System.out.println("before sending msg "+new String(msg));
		MPI.COMM_WORLD.send(msg, size_msg, MPI.BYTE, request.source, 0);
		System.out.println("msg sent");
	}*/
	
	void getAgentsIn(String area) throws MPIException
	{
		System.out.println("getAgentsIn area : "+area+" begin ");
		ArrayList<IAgent> agents;
		String message;

		System.out.println("248");
		IShape shape;
		if(area.equals("shape"))
		{
			shape = slaveMPI.getGeometry();
		}else
		{
			shape = (IShape) slaveMPI.getAttribute(area);
		}
		System.out.println("shape getAgentsIn "+area+" = "+shape);
		
		agents = DistributionUtils.getAgentInArea(this.scope, (IShape) shape);
		System.out.println("agents in area "+agents);
		
		message = StreamConverter.convertMPIObjectToStream(this.scope, agents);
		System.out.println("message string convert "+message);	
		
		System.out.println("before assigning size_msg_send ");	
		
		int[] size_msg_send = new int[1];
		System.out.println("message.length() "+message.length());	
		size_msg_send[0] = message.length();
		
		System.out.println("sending size_msg_send "+size_msg_send[0]);		
		
		MPI.COMM_WORLD.send(size_msg_send, 1, MPI.INT, request.source, 0); // @2
		System.out.println("size_msg_send sent");
		
		byte[] msg = message.getBytes();
		int size_msg = message.getBytes().length;

		System.out.println("before sending msg "+new String(msg));
		MPI.COMM_WORLD.send(msg, size_msg, MPI.BYTE, request.source, 0);  // @3
		System.out.println("msg sent");
	}
	
	public void run()
	{
		System.out.println("ProcessRequestRunnable run begin");
		try 
		{
			System.out.println("Request RequestType = "+request.requestType.name());
			switch(request.requestType)
			{
				case GET_AGENT_IN_MAIN_AREA : 
					getAgentsIn(IMPISkill.MAIN_AREA);
					System.out.println("END OF GET_AGENT_IN_MAIN_AREA");
					break;
				
				case GET_AGENT_IN_INNER_OLZ : 
					getAgentsIn(IMPISkill.INNER_OLZ_AREA);			
					System.out.println("END OF GET_AGENT_IN_INNER_OLZ");
					break;
					
				case GET_AGENT_IN_OUTER_OLZ : 
					getAgentsIn(IMPISkill.OUTER_OLZ_AREA);	
					System.out.println("END OF GET_AGENT_IN_OUTER_OLZ");			
					break;
					
				case GET_ALL_AGENT : 
					getAgentsIn(IMPISkill.MAIN_AREA);
					//getAgentsIn(IMPISkill.INNER_OLZ_AREA);
					getAgentsIn(IMPISkill.OUTER_OLZ_AREA);
					System.out.println("END OF GET_ALL_AGENT");				
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

class DistributionUtils
{	
	
	public static ArrayList<IAgent> getAgentInArea(IScope scope, IShape shape)
	{	
		ArrayList<IAgent> agentListInImportedModel = new ArrayList<IAgent>();
		var agentListInMainModel = scope.getExperiment().getAgents(scope.getRoot().getScope());
		System.out.println("agentListInMainModel = "+agentListInMainModel);
		for(var let : agentListInMainModel)
		{
			System.out.println("let = "+let.getName());
			if(let instanceof SimulationAgent)
			{
				IPopulation<? extends IAgent>[] pops = ((ExperimentAgent)((SimulationAgent)let).getExternMicroPopulationFor("pp.movingExp").getAgent(0)).getSimulation().getMicroPopulations();
				System.out.println("pops = "+pops);
				for(var pop : pops)
				{
					System.out.println("pop name = "+pop.getName());
				}
				for(var pop : pops)
				{
					if(!pop.getName().equals("cell"))
					{
						System.out.println("pop name = "+pop.getName());
						for(var agent : pop)
						{
							System.out.println("agent name :  "+agent.getName());
							System.out.println("agent location : "+agent.getLocation());
							System.out.println("agent intersect shape : "+agent.getLocation().intersects(shape));
						}
						
						System.out.println("Spatial.Queries.inside( scope : "+scope+", pop :"+pop+", shape :"+shape+")");
						IList<? extends IShape> listShape = Spatial.Queries.inside(scope, pop, shape);
						System.out.println("listShape : "+listShape);
						IList<IAgent> listAgent = GamaListFactory.wrap(Types.LIST, listShape.stream().map(t -> t.getAgent()).collect(Collectors.toList()));
						System.out.println("listAgent : "+listAgent);
						
						agentListInImportedModel.addAll(listAgent);
					}
				}
				
				System.out.println("result of test "+agentListInImportedModel);
				return agentListInImportedModel;
			}
		}
		System.out.println("NO IMPORTED MODEL FOUND");
		return agentListInImportedModel;
	}
	
	public static ArrayList<IAgent> processCommunication(IScope scope, int neighborRank, RequestType requestType)
	{	
		try 
		{
			int[] requestOrdinal = new int[1];
			requestOrdinal[0] = requestType.ordinal();
			System.out.println("requestOrdinal = "+requestOrdinal[0]);
			
			MPI.COMM_WORLD.send(requestOrdinal, 1, MPI.INT, neighborRank, IMPISkill.REQUEST_TAG); // @1
			System.out.println("requestOrdinal sent");
	
			int[] sizeOfMessage = new int[1];
			System.out.println("before receiving sizeOfMessage");
			MPI.COMM_WORLD.recv(sizeOfMessage, 1, MPI.INT, neighborRank, 0); // @2 // getting size of the list
			System.out.println("sizeOfMessage = "+sizeOfMessage[0]);
			byte[] message = new byte[sizeOfMessage[0]];
			
			System.out.println("before receiving message");
			MPI.COMM_WORLD.recv(message, sizeOfMessage[0], MPI.BYTE, neighborRank, 0); // @3 // getting the list
			System.out.println("message "+new String(message));
			
			ArrayList<IAgent> rcvMesg = (ArrayList<IAgent>) StreamConverter.convertMPIStreamToObject(scope, new String(message));
			System.out.println("IAgent : \n\n");
			for(var auto : rcvMesg)
			{
				System.out.println("auto class name = "+auto.getClass().getName());
				System.out.println("auto index "+auto.getIndex());
				System.out.println("auto getGamlType "+auto.getGamlType());
				System.out.println("auto getName "+auto.getName());
				System.out.println("auto getUniqueID "+auto.getUniqueID());
				//System.out.println("auto getGamlType "+auto.restoreTo(scope, scope.getSimulation().getPopulation()));
			}
			//System.out.println("rcvMesg Object "+ rcvMesg);
			
			return rcvMesg;
			
		} catch (MPIException e) 
		{
			e.printStackTrace();
		}
		
		return null;
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
			System.out.println("Starting MPI");
			
			PrintStream fileOut = new PrintStream("filename"+myRank+".txt");
			System.setOut(fileOut);
			
			System.out.println("Hello");
			System.out.println("MyRank = " + myRank);
			System.out.println("SizeNetwork = " + MPI.COMM_WORLD.getSize());
		} catch (MPIException | IOException e) 
		{
			System.out.println("MPIException " + e);
		}
		
		// todo define this ^ in other function to be called after the init of everyone
		// maybe with MPI.Barrier() to block all
		
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
	
	@action(name = IMPISkill.GET_AGENT_IN_NEIGHBOR_OUTER_OLZ,
			args = { @arg (
					name = IMPISkill.NEIGHBORS_RANK,
					type = IType.INT,
					doc = @doc ("neighbor rank")),
			}
	)
	public ArrayList<IAgent> getAgentInNeighborOuterOLZ(IScope scope) 
	{
		ArrayList<IAgent> agentsInOuterOLZ = DistributionUtils.processCommunication(scope, (int) scope.getArg(IMPISkill.NEIGHBORS_RANK, IType.INT), RequestType.GET_AGENT_IN_OUTER_OLZ);
		System.out.println("returning getAgentInNeighborOuterOLZ to gaml = "+agentsInOuterOLZ);
		agentsInOuterOLZ.removeAll(Collections.singleton(null));
		return agentsInOuterOLZ;
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
		ArrayList<IAgent> agentsInInnerOLZ = DistributionUtils.processCommunication(scope, (int) scope.getArg(IMPISkill.NEIGHBORS_RANK, IType.INT), RequestType.GET_AGENT_IN_INNER_OLZ);
		System.out.println("returning getAgentInNeighborInnerOLZ to gaml = "+agentsInInnerOLZ);
		agentsInInnerOLZ.removeAll(Collections.singleton(null));
		
		return agentsInInnerOLZ;
	}
	
	@action(name = IMPISkill.GET_AGENT_IN_NEIGHBOR_MAIN_AREA,
			args = { @arg (
					name = IMPISkill.NEIGHBORS_RANK,
					type = IType.INT,
					doc = @doc ("neighbor rank")),
			}
	)
	public ArrayList<IAgent> getAgentInNeighborMainArea(IScope scope) 
	{
		ArrayList<IAgent> agentsInMain = DistributionUtils.processCommunication(scope, (int) scope.getArg(IMPISkill.NEIGHBORS_RANK, IType.INT), RequestType.GET_AGENT_IN_MAIN_AREA);
		System.out.println("returning agentsInMain to gaml = "+agentsInMain);
		agentsInMain.removeAll(Collections.singleton(null));
		return agentsInMain;
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
		listener = new RequestListenerThread(this, scope, myRank);
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
	

	@action(name = IMPISkill.GET_ALL_AGENT_IN_NEIGHBOR,
			args = { @arg (
					name = IMPISkill.NEIGHBORS_RANK,
					type = IType.INT,
					doc = @doc ("neighbor rank")),
			}
	)
	public static Set<IAgent> getAllAgentFromNeighbor(final IScope scope)
	{
		
		System.out.println("getAllAgentFromNeighbor begin");
		//System.out.println("getAgentInNeighborsInnerOLZ inside begin");
		//ArrayList<IAgent> inner = DistributionUtils.processCommunication(scope, (int) scope.getArg(IMPISkill.NEIGHBORS_RANK, IType.INT), RequestType.GET_AGENT_IN_INNER_OLZ);
		
		System.out.println("getAgentInNeighborsOuterOLZ inside begin");
		ArrayList<IAgent> outer = DistributionUtils.processCommunication(scope, (int) scope.getArg(IMPISkill.NEIGHBORS_RANK, IType.INT), RequestType.GET_AGENT_IN_OUTER_OLZ);
		
		System.out.println("getAgentInNeighborsInMainArea inside begin");
		ArrayList<IAgent> main = DistributionUtils.processCommunication(scope, (int) scope.getArg(IMPISkill.NEIGHBORS_RANK, IType.INT),RequestType.GET_AGENT_IN_MAIN_AREA);
		
		System.out.println("outer "+outer);
		System.out.println("main "+main);
		
		main.addAll(outer);
		
		Set<IAgent> uniqueAgent = new HashSet<IAgent>(main); // only one copy of agent in the list
		
		return uniqueAgent;
	}
}