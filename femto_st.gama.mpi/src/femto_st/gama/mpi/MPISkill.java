package femto_st.gama.mpi;

import mpi.*;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import ummisco.gama.serializer.factory.StreamConverter;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
	    
	    final IAgent agt = scope.getAgent();
	    boolean isMPIInit = (boolean) scope.getArg(IMPISkill.MPI_INIT_DONE, IType.BOOL);
	    
	    if (!isMPIInit) { return; }
	    
	    final String[] arg = {};
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
		final int[] size = new int[1];
		size[0] = message.length;

		
		System.out.println("size of message : "+size[0]);
		try {
			System.out.println("send size: "+Arrays.toString(size));
			MPI.COMM_WORLD.send(size, 1, MPI.INT, dest, stag);
			System.out.println("send message: "+message.length);
			MPI.COMM_WORLD.send(message, message.length, MPI.BYTE, dest, stag);
			System.out.println("end try");

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
			MPI.COMM_WORLD.recv(size, 1, MPI.INT, source, rtag);
			message = new byte[size[0]];
			MPI.COMM_WORLD.recv(message, size[0], MPI.BYTE, source, rtag);
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
	
	@action(name = "testing")
	public <T extends IAgent> IList<T> test(final IScope scope)
	{

		List<T> allAgentsInImportedModel = new ArrayList<T>();
		
		var list = scope.getExperiment().getAgents(scope.getRoot().getScope());
		System.out.println("list = "+list);
		for(var let : list)
		{
			if(let instanceof SimulationAgent)
			{
				IPopulation<? extends IAgent> movingAgents = ((ExperimentAgent)((SimulationAgent)let).getExternMicroPopulationFor("pp.movingExp").getAgent(0)).getSimulation().getPopulationFor("movingAgent");
				for(var agent : movingAgents)
				{
					System.out.println("agent = "+agent.getName());
				}

				IPopulation<? extends IAgent>[] allPops = ((ExperimentAgent)((SimulationAgent)let).getExternMicroPopulationFor("pp.movingExp").getAgent(0)).getSimulation().getMicroPopulations();

				System.out.println("HELLOOOOOOOOOOOOOOOOO ");
				for(var pop : allPops)
				{
					System.out.println("pop name = "+pop.getName());
					System.out.println("0");
					List<T> casting = (List<T>) pop.getAgents(scope);
					System.out.println("1");
					allAgentsInImportedModel.addAll(casting);
					System.out.println("2");
					System.out.println("allAgentsInImportedModel "+allAgentsInImportedModel.toString());

					System.out.println("pop agent "+pop.getAgents(scope));
				}
				/*System.out.println("let "+((SimulationAgent)let).getSpeciesName());
				System.out.println("1 "+let.getModel().getAgents(scope.getRoot().getScope()));
				System.out.println("2 "+let.getModel().getAllSpecies());
				System.out.println("3 movingAgent pop = "+let.getModel().getSpecies("movingAgent").getPopulation(scope));
				System.out.println("3 movingAgent pop = "+let.getModel().getSpecies("movingAgent").getPopulation(scope.getRoot().getScope()));
				System.out.println("4 "+let.getModel().getAgents(let.getScope()));
				
				System.out.println("5 "+let.getModel().getSpecies("OLZ_model").getAgents(scope));
				System.out.println("6 "+let.getModel().getSpecies("OLZ_model").getAgents(scope.getRoot().getScope()));

				System.out.println("7 "+let.getModel().getSpecies("OLZ_model").getMicroSpecies().get(0).getAgents(scope));
				IList<ISpecies> listAgent = let.getModel().getSpecies("OLZ_model").getMicroSpecies();
				System.out.println("listAgent value"+listAgent);
				
				for(var auto : listAgent.iterable(scope))
				{
					System.out.println("auto : "+auto);
					System.out.println("auto POP : "+auto.getPopulation(scope));
					System.out.println("auto root POP : "+auto.getPopulation(scope.getRoot().getScope()));
				}*/
				System.out.println("result = "+allAgentsInImportedModel);
				return (IList<T>) allAgentsInImportedModel;
			}
		}
		
		return null;
	}
}
