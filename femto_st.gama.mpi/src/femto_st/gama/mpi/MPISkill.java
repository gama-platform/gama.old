package femto_st.gama.mpi;

import mpi.*;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.factory.StreamConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@vars ({ @variable (
		name = IMPISkill.MPI_RANK,
		type = IType.INT,
		doc = @doc ("Init MPI Brocker")),
	 @variable (
		name = IMPISkill.MPI_INIT_DONE,
		type = IType.BOOL,
		doc = @doc ("Init MPI Brocker")) })
@skill (
		name = IMPISkill.MPI_NETWORK,
		concept = { IConcept.GUI, IConcept.COMMUNICATION, IConcept.SKILL })
		
public class MPISkill extends Skill {
	boolean isMPIInit = false;

	private void initialize(final IScope scope) {
		isMPIInit = true;
	}

	private void startSkill(final IScope scope) {
		initialize(scope);
		registerSimulationEvent(scope);
	}

	@action (
			name = IMPISkill.MPI_INIT,
			args = {},
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public void mpiInit(final IScope scope) {
		if (isMPIInit) { return; }

		final String[] arg = {};
		try {
			MPI.Init(arg);
			isMPIInit = true;
            final IAgent agt = scope.getAgent();
	        agt.setAttribute (IMPISkill.MPI_INIT_DONE, IType.BOOL);
			
		} catch (final MPIException e) {
			System.out.println("MPI Init Error" + e);
		}
	}

	@action (
			name = IMPISkill.MPI_FINALIZE,
			args = {},
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public void mpiFinalize(final IScope scope) {
	    
	    boolean isMPIInit = (boolean) scope.getArg(IMPISkill.MPI_INIT_DONE, IType.BOOL);
	  
	    if (!isMPIInit) { return; }
	    
	    try {
			System.out.println("************* Call Finalize");
			MPI.Finalize();
	    } catch (final MPIException e) {
			System.out.println("MPI Finalize Error" + e);
	    }
	}

	@action (
			name = IMPISkill.MPI_SIZE,
			args = {},
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public int getMPISIZE(final IScope scope) {
		int size = 0;
		try {
			size = MPI.COMM_WORLD.getSize();
			//
		} catch (final MPIException mpiex) {
			System.out.println("MPI Size Error" + mpiex);
		}

		return size;
	}

	@action (
			name = IMPISkill.MPI_RANK,
			args = {},
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public int getMPIRANK(final IScope scope) {
		int rank = 0;
		try {
			rank = MPI.COMM_WORLD.getRank();

		} catch (final MPIException mpiex) {
			System.out.println("MPI rank Error" + mpiex);
		}

		return rank;
	}

	@action (
			name = IMPISkill.MPI_SEND,
			args = { @arg (
					name = IMPISkill.MESG,
					type = IType.LIST,
					doc = @doc ("mesg message")),
					@arg (
							name = IMPISkill.DEST,
							type = IType.INT,
							doc = @doc ("dest destinataire")),
					@arg (
							name = IMPISkill.STAG,
							type = IType.INT,
							doc = @doc ("stag message tag")) },
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public void send(final IScope scope) {

		System.out.println("xxxxxHHHHxxxxxx ");
		final IList mesg = (IList) scope.getArg(IMPISkill.MESG, IType.LIST);
		final int dest = ((Integer) scope.getArg(IMPISkill.DEST, IType.INT)).intValue();
		final int stag = ((Integer) scope.getArg(IMPISkill.STAG, IType.INT)).intValue();

		System.out.println("mesg = " + mesg);
		System.out.println("dest = " + dest);
		System.out.println("stag = " + stag);

		String conversion = StreamConverter.convertMPIObjectToStream(scope, mesg);
		System.out.println("xxxxxxxxxxx " +conversion);
		
		final byte[] message = StreamConverter.convertMPIObjectToStream(scope, mesg).getBytes();
		
		try {
			
			System.out.println("send message: "+message);
			System.out.println("message lenght: "+message.length);
			MPI.COMM_WORLD.send(message, message.length, MPI.BYTE, dest, stag);
			
		} catch (final MPIException mpiex) {
			System.out.println("MPI send Error" + mpiex);
		}

		System.out.println("End send ");
	}

	@action (
			name = IMPISkill.MPI_RECV,
			args = { @arg (
					name = IMPISkill.RCVSIZE,
					type = IType.INT,
					doc = @doc ("rdvsize recv size")),
					@arg (
							name = IMPISkill.SOURCE,
							type = IType.INT,
							doc = @doc ("source sender")),
					@arg (
							name = IMPISkill.RTAG,
							type = IType.INT,
							doc = @doc ("rtag message tag")) },
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public IList recv(final IScope scope) {
		//final int rcvSize = ((Integer) scope.getArg(IMPISkill.RCVSIZE, IType.INT)).intValue();
		final int source = ((Integer) scope.getArg(IMPISkill.SOURCE, IType.INT)).intValue();
		final int rtag = ((Integer) scope.getArg(IMPISkill.RTAG, IType.INT)).intValue();

		final int size[] = new int[1];
		byte[] message = null;


		System.out.println("Before MPI.COMM_WORLD.recv");
		try {
			
			Status st = MPI.COMM_WORLD.probe(source,0);
            int sizeOfMessage = st.getCount(MPI.BYTE);
            System.out.println("sizeOfMessage " + sizeOfMessage);
			message = new byte[sizeOfMessage];
			
			MPI.COMM_WORLD.recv(message, sizeOfMessage, MPI.BYTE, source, rtag);
		} catch (final MPIException mpiex) {
			System.out.println("MPI send Error" + mpiex);
		}
		System.out.println("after MPI.COMM_WORLD.recv");

		
		System.out.println("Before rcvMesg");

		final IList rcvMesg = (IList) StreamConverter.convertMPIStreamToObject(scope, new String(message));
		System.out.println("rcvMesg "+rcvMesg);

		return rcvMesg;
	}
	
	@action (
			name = IMPISkill.BARRIER,
			args = {},
			doc = @doc (
					value = "",
					returns = "",
					examples = { @example ("") }))
	public void doBarrier(final IScope scope) {
		try 
		{
			System.out.println("MPI BARRIER WAITING = "+ MPI.COMM_WORLD.getRank());
			Request rq = MPI.COMM_WORLD.iBarrier();
			while(!rq.test())
			{
				Thread.sleep(500);
				System.out.println("waiting "+MPI.COMM_WORLD.getRank()+" .... ");
			}
			System.out.println("MPI BARRIER END = "+ MPI.COMM_WORLD.getRank());
		} catch (final MPIException | InterruptedException mpiex) 
		{
			System.out.println("MPI barrier Error" + mpiex);
		}
	}

	private void finalizeMPI(final IScope scope) {
		try {
			MPI.Finalize();
			isMPIInit = false;
		} catch (final MPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void registerSimulationEvent(final IScope scope) {
		scope.getSimulation().postDisposeAction(scope1 -> {
			finalizeMPI(scope1);
			return null;
		});
	}
	
	public static List<Object> gatherAttributeFromMainModel(IScope scope, String specieName, String attribute)
	{
		System.out.println("gatherAttribute : " + attribute + " from specie : " +  specieName);
		
		IList<IAgent> agentListInControlerModel = scope.getExperiment().getAgents(scope.getRoot().getScope());
		for(var currentAgentInControler : agentListInControlerModel)
		{
			System.out.println("currentAgentInControler = "+currentAgentInControler.getName());
			if(currentAgentInControler instanceof SimulationAgent)
			{
				IPopulation<? extends IAgent>[] populationsInMainModel = ((ExperimentAgent)((SimulationAgent)currentAgentInControler).getExternMicroPopulationFor("pp.movingExp").getAgent(0)).getSimulation().getMicroPopulations();
				for(var population : populationsInMainModel)
				{
					System.out.println("pop name = "+population.getName());
					if(population.getName().equals(specieName))
					{
						System.out.println("Found species : "+specieName);
						var auto = population.getVar(attribute);
						
						if(auto != null)
						{
							System.out.println("Found Attribute : " + attribute);
							var agentsFromPopulation = population.getAgents(scope);
							List<Object> listOfAttribute = agentsFromPopulation.stream(scope).filter(agent -> agent.getIsCopy() == false).map(agent -> agent.getAttribute(attribute)).collect(Collectors.toList());

							System.out.println("listOfAttribute = " + listOfAttribute);
							for(IAgent agent : agentsFromPopulation.iterable(scope))
							{
								System.out.println("agent ("+agent.getName() + ") = " + agent.getAttribute(attribute).toString());
							}
							
							return listOfAttribute;
							
						}else
						{
							System.out.println("No attribute : " + attribute + " for specie : " +  specieName);
						}
					}
				}	

				System.out.println("No specie : "+specieName+" found");
				return new ArrayList<Object>();
			}
		}

		return new ArrayList<Object>();
	}
	
	@action(name = IMPISkill.GATHER_ATTRIBUTE_FROM_MAIN_MODEL,
			args = { @arg (
					name = IMPISkill.SPECIE_NAME_IN_MAIN_MODEL,
					type = IType.STRING,
					doc = @doc ("species to gather attribute from")),
					
					@arg (
						name = IMPISkill.ATTRIBUTE_TO_GATHER,
						type = IType.STRING,
						doc = @doc ("attribute from the species to gather"))
			})
	public List<Object> gatherAttributeFromMainModel(final IScope scope)
	{
		
		String specieName = (String) scope.getArg("specieName", IType.STRING);
		String attribute = (String) scope.getArg("attribute", IType.STRING);
		
		return gatherAttributeFromMainModel(scope, specieName, attribute);
	}
	
	public static ArrayList<IAgent> getAgentInArea(IScope scope, IShape shape)
	{	
		ArrayList<IAgent> agentListInImportedModel = new ArrayList<IAgent>();
		IList<IAgent> agentListInMainModel = scope.getExperiment().getAgents(scope.getRoot().getScope());
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
						IList<IAgent> listAgent = GamaListFactory.wrap(Types.LIST, listShape.stream().filter(agentShape -> agentShape.getAgent().getIsCopy() == false).map(agentShape -> agentShape.getAgent()).collect(Collectors.toList()));
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
}
