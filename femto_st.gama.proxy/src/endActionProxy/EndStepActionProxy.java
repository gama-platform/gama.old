package endActionProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import MPICommunication.MPIRequestSender;
import mpi.MPI;
import mpi.MPIException;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.statements.IExecutable;
import proxy.ProxyAgent;
import synchronizationMode.LocalSynchronizationMode;
import ummisco.gama.dev.utils.DEBUG;

public class EndStepActionProxy implements IExecutable 
{

	static
	{
		DEBUG.ON();
	}
	
	public EndStepActionProxy()
	{
		DEBUG.OUT("EndActionProxy created");
	}
	
	@Override
	public Object executeOn(IScope scope) throws GamaRuntimeException 
	{
		try {
			MPI.COMM_WORLD.barrier();
		} catch (MPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateDistantAgent(scope);
		
		try {
			MPI.COMM_WORLD.barrier();
		} catch (MPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private void updateDistantAgent(IScope scope)
	{
		var microPops = scope.getSimulation().getMicroPopulations();

		DEBUG.OUT("microPops : " + microPops);
		Map<Integer, ArrayList<IAgent>> updatedAgents = new HashMap<Integer, ArrayList<IAgent>>();
		for(var microPop : microPops)
		{
			DEBUG.OUT("pop : " + microPop.toString());
			
			var agents = microPop.getAgents(scope).iterable(scope);
			DEBUG.OUT("agents : " + agents);
			
			for(var agent : agents)
			{
				if(agent instanceof ProxyAgent)
				{
					DEBUG.OUT(agent.getName() + " is a proxy");
					DEBUG.OUT(((ProxyAgent)agent).getSynchroMode());
					
					if(((ProxyAgent)agent).getSynchroMode() instanceof LocalSynchronizationMode)
					{
						LocalSynchronizationMode mode = (LocalSynchronizationMode) ((ProxyAgent)agent).getSynchroMode();
						DEBUG.OUT("mode " + mode.getProcsWithDistantAgent());
						
						if(mode.getProcsWithDistantAgent().size() != 0 )
						{
							mode.getProcsWithDistantAgent().forEach((procWithCopy) -> 
								updatedAgents.compute(procWithCopy, (k, v) -> {
						            v = v != null ? new ArrayList<IAgent>(v) : new ArrayList<IAgent>();
						            v.add(agent);
						            return v;
						        }));
						}
					}else
					{
						DEBUG.OUT("Distant mode ");
					}
				}
			}
			
			
		}
		
		DEBUG.OUT("sending sendUpdatedAgentsFromOLZ");
		MPIRequestSender.sendUpdatedAgentsFromOLZ(updatedAgents);
	}
	
	private void sendAgentCopyToNeighbor(IScope scope)
	{
		// TODO
	}

}
