package ummisco.gaml.extensions.music;

import java.util.ArrayList;
import java.util.Collections;
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
	
	private static final int NB_OF_MUSIC_PLAYERS = 2;
	
	private List<GamaMusicPlayer> musicPlayerPools = Collections.synchronizedList(new ArrayList<GamaMusicPlayer>(30));

	private static Map<SimulationAgent, Map<IAgent, GamaMusicPlayer>> musicPlayerOfAgents = new HashMap<SimulationAgent, Map<IAgent, GamaMusicPlayer>>();
	
	private static MusicPlayerBroker broker = null;
	
	
	public static MusicPlayerBroker getInstance() {
		
		if (broker == null) {  broker = new MusicPlayerBroker(); }
		
		return broker;
	}
	
	
	private MusicPlayerBroker() {
		for (int i = 0; i < NB_OF_MUSIC_PLAYERS; i++) { musicPlayerPools.add(new GamaMusicPlayer()); }
	}
	
	
	public void removeMusicPlayer(final IAgent agent) {
		
		synchronized (musicPlayerOfAgents) {
			final SimulationAgent simulation = agent.getScope().getSimulationScope();
			
			Map<IAgent, GamaMusicPlayer> agents = musicPlayerOfAgents.get(simulation);
			
			if (agents != null) { agents.remove(agent); }
		}
	}
	
	
	public GamaMusicPlayer getMusicPlayer(final IAgent agent) {
		
		synchronized (musicPlayerOfAgents) {
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
				
				synchronized (musicPlayerPools) {
					if (!musicPlayerPools.isEmpty()) {
						musicPlayerOfAgent = musicPlayerPools.remove(0);
						musicPlayersOfSimulation.put(agent, musicPlayerOfAgent);
					}
				}
			}
			
//			System.out.println("musicPlayersOfSimulation.size() = " + musicPlayersOfSimulation.size());

			return musicPlayerOfAgent;
		}
	}
	
	
	public void manageMusicPlayers(final SimulationAgent simulation) throws GamaRuntimeException {
		GamaMusicPlayer musicPlayer;
		
		Map<IAgent, GamaMusicPlayer> musicPlayersOfSimulation = musicPlayerOfAgents.get(simulation);
		
		
//		System.out.println("manageMusicPlayers :: musicPlayerPools.size() :: BEFORE :: " + musicPlayerPools.size());
		
		// remove music players of dead agents
		List<IAgent> deadAgents = new ArrayList<IAgent>();
		for (IAgent a : musicPlayersOfSimulation.keySet()) { if (a.dead()) deadAgents.add(a); }
		for (IAgent d : deadAgents) {
			musicPlayer = musicPlayersOfSimulation.get(d);
			musicPlayer.stop(true);
			
			musicPlayersOfSimulation.remove(d); 

			synchronized (musicPlayerPools) {
				musicPlayerPools.add(musicPlayer);
			}
		}

//		System.out.println("manageMusicPlayers :: musicPlayerPools.size() :: AFTER :: " + musicPlayerPools.size());
		
		
		// remove music players already finished playing
		synchronized (musicPlayerOfAgents) {
//			System.out.println("EOM removed BEFORE : musicPlayersOfSimulation.size() : " + musicPlayersOfSimulation.size());
			
			List<IAgent> agentsToBeRemoved = new ArrayList<IAgent>();
			for (IAgent a : musicPlayersOfSimulation.keySet()) {
				
				musicPlayer = musicPlayersOfSimulation.get(a);
				if (musicPlayer.isEndOfMedia() && musicPlayer.isPlayerStopped() && !musicPlayer.isRepeat()) { agentsToBeRemoved.add(a); }
			}
			
			for (IAgent a : agentsToBeRemoved) {
				musicPlayer = musicPlayersOfSimulation.remove(a);
				
				synchronized (musicPlayerPools) {
					musicPlayerPools.add(musicPlayer);
				}
				
			}

//			System.out.println("EOM removed AFTER : musicPlayersOfSimulation.size() : " + musicPlayersOfSimulation.size());
		}
	}
	
	
	public void schedulerDisposed(final SimulationAgent simulation) throws GamaRuntimeException {
		
//		System.out.println("MusicPlayerBroker :: schedulerDisposed :: musicPlayerOfAgents.size() BEFORE" + musicPlayerOfAgents.size());
		
		Map<IAgent, GamaMusicPlayer> musicPlayersOfSimulation = musicPlayerOfAgents.get(simulation);
		
		if (musicPlayersOfSimulation != null) {
			for (GamaMusicPlayer player : musicPlayersOfSimulation.values()) { player.stop(true); }
			synchronized (musicPlayerPools) {
				musicPlayerPools.addAll(musicPlayersOfSimulation.values());
			}
			
			
			musicPlayersOfSimulation.clear();
			musicPlayerOfAgents.remove(simulation);
		}

//		System.out.println("MusicPlayerBroker :: schedulerDisposed :: musicPlayerOfAgents.size() AFTER" + musicPlayerOfAgents.size());
	}
}
