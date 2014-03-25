package ummisco.gaml.extensions.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.kernel.experiment.AgentScheduler;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaHelper;

public class MusicPlayerBroker {

	private static Map<SimulationAgent, Map<IAgent, GamaMusicPlayer>> musicPlayerOfAgents = new HashMap<SimulationAgent, Map<IAgent, GamaMusicPlayer>>();
	
	private static MusicPlayerBroker broker = null;
	
	
	public static MusicPlayerBroker getInstance() {
		
		if (broker == null) {  broker = new MusicPlayerBroker(); }
		
		return broker;
	}
	
	
	public void removeMusicPlayer(final IAgent agent) {
		
		final SimulationAgent simulation = agent.getScope().getSimulationScope();
		
		Map<IAgent, GamaMusicPlayer> agents = musicPlayerOfAgents.get(simulation);
		
		if (agents != null) { agents.remove(agent); }
	}
	
	public GamaMusicPlayer getMusicPlayer(final IAgent agent) {
		
		final SimulationAgent simulation = agent.getScope().getSimulationScope();
		
		Map<IAgent, GamaMusicPlayer> musicPlayersOfSimulation = musicPlayerOfAgents.get(simulation);
		if (musicPlayersOfSimulation == null) {
			musicPlayersOfSimulation = new HashMap<IAgent, GamaMusicPlayer>();
			musicPlayerOfAgents.put(simulation, musicPlayersOfSimulation);
			
			
			AgentScheduler scheduler = simulation.getScheduler();
			
			scheduler.insertEndAction(new GamaHelper() {

				@Override public Object run(final IScope scope) throws GamaRuntimeException {
					broker.manageMusicPlayers(simulation);
					return null;
				}
			});
			
			
			scheduler.insertDisposeAction(new GamaHelper() {

				@Override public Object run(final IScope scope) throws GamaRuntimeException {
					broker.schedulerDisposed(simulation);
					return null;
				}
			});
		}
		
		
		GamaMusicPlayer musicPlayerOfAgent = musicPlayersOfSimulation.get(agent);
		if (musicPlayerOfAgent == null) {
			musicPlayerOfAgent = new GamaMusicPlayer();
			musicPlayersOfSimulation.put(agent, musicPlayerOfAgent);
		}
		
		return musicPlayerOfAgent;
	}
	
	
	public void manageMusicPlayers(final SimulationAgent simulation) throws GamaRuntimeException {
		GamaMusicPlayer musicPlayer;
		
		Map<IAgent, GamaMusicPlayer> musicPlayersOfSimulation = musicPlayerOfAgents.get(simulation);
		List<IAgent> deadAgents = new ArrayList<IAgent>();
		for (IAgent a : musicPlayersOfSimulation.keySet()) { if (a.dead()) deadAgents.add(a); }
		
		
		for (IAgent d : deadAgents) {
			musicPlayer = musicPlayersOfSimulation.get(d);
			musicPlayer.stop();
			
			musicPlayersOfSimulation.remove(d); 
		}
	}
	
	
	public void schedulerDisposed(final SimulationAgent simulation) throws GamaRuntimeException {
		
		System.out.println("MusicPlayerBroker :: schedulerDisposed :: musicPlayerOfAgents.size() BEFORE" + musicPlayerOfAgents.size());
		
		Map<IAgent, GamaMusicPlayer> musicPlayersOfSimulation = musicPlayerOfAgents.get(simulation);
		
		if (musicPlayersOfSimulation != null) {
			for (GamaMusicPlayer player : musicPlayersOfSimulation.values()) { player.stop(); }
			
			musicPlayersOfSimulation.clear();
			musicPlayerOfAgents.remove(simulation);
		}

		System.out.println("MusicPlayerBroker :: schedulerDisposed :: musicPlayerOfAgents.size() AFTER" + musicPlayerOfAgents.size());
	}
}
