package MPISkill;

import mpi.MPI;
import mpi.MPIException;
import mpi.Request;
import mpi.Status;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.factory.StreamConverter;

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
		
public class MPISkill extends Skill 
{

	static
	{
		DEBUG.ON();
	}
	boolean isMPIInit = false;

	private void startSkill(final IScope scope) 
	{
		isMPIInit = true;
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
			DEBUG.OUT("MPI Init Error" + e);
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
	    	DEBUG.OUT("************* Call Finalize");
			MPI.Finalize();
	    } catch (final MPIException e) {
	    	DEBUG.OUT("MPI Finalize Error" + e);
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
			DEBUG.OUT("MPI Size Error" + mpiex);
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
			DEBUG.OUT("MPI rank Error" + mpiex);
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

		final IList mesg = (IList) scope.getArg(IMPISkill.MESG, IType.LIST);
		final int dest = ((Integer) scope.getArg(IMPISkill.DEST, IType.INT)).intValue();
		final int stag = ((Integer) scope.getArg(IMPISkill.STAG, IType.INT)).intValue();

		DEBUG.OUT("mesg = " + mesg);
		DEBUG.OUT("dest = " + dest);
		DEBUG.OUT("stag = " + stag);

		String conversion = StreamConverter.convertObjectToStream(scope, mesg);
		DEBUG.OUT("conversion: " +conversion);
		
		final byte[] message = conversion.getBytes();
		
		try {
			
			DEBUG.OUT("send message: "+message);
			DEBUG.OUT("message lenght: "+message.length);
			MPI.COMM_WORLD.send(message, message.length, MPI.BYTE, dest, stag);
			
		} catch (final MPIException mpiex) {
			DEBUG.OUT("MPI send Error" + mpiex);
		}

		DEBUG.OUT("End send ");
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


		DEBUG.OUT("Before MPI.COMM_WORLD.recv");
		try {
			
			Status st = MPI.COMM_WORLD.probe(source, rtag);
            int sizeOfMessage = st.getCount(MPI.BYTE);
            DEBUG.OUT("sizeOfMessage " + sizeOfMessage);
			message = new byte[sizeOfMessage];
			
			MPI.COMM_WORLD.recv(message, sizeOfMessage, MPI.BYTE, source, rtag);
		} catch (final MPIException mpiex) {
			DEBUG.OUT("MPI send Error" + mpiex);
		}
		DEBUG.OUT("after MPI.COMM_WORLD.recv");

		
		DEBUG.OUT("Before rcvMesg");

		final IList rcvMesg = (IList) StreamConverter.convertNetworkStreamToObject(scope, new String(message));
		DEBUG.OUT("rcvMesg "+rcvMesg);

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
			DEBUG.OUT("MPI BARRIER WAITING = "+ MPI.COMM_WORLD.getRank());
			Request rq = MPI.COMM_WORLD.iBarrier();
			while(!rq.test())
			{
				Thread.sleep(500);
				DEBUG.OUT("waiting "+MPI.COMM_WORLD.getRank()+" .... ");
			}
			DEBUG.OUT("MPI BARRIER END = "+ MPI.COMM_WORLD.getRank());
		} catch (final MPIException | InterruptedException mpiex) 
		{
			DEBUG.OUT("MPI barrier Error" + mpiex);
		}
	}

	private void finalizeMPI(final IScope scope) {
		try {
			MPI.Finalize();
			isMPIInit = false;
		} catch (final MPIException e) {
			e.printStackTrace();
		}
	}
}
