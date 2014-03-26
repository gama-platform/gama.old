package ummisco.gaml.extensions.sound;

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

public class SoundPlayerBroker {
	
	// the maximum number of BasicPlayer instant can only be 2. Increase this number will raise error.
	private static final int MAX_NB_OF_MUSIC_PLAYERS = 2;
	
	private List<GamaSoundPlayer> soundPlayerPools = Collections.synchronizedList(new ArrayList<GamaSoundPlayer>(MAX_NB_OF_MUSIC_PLAYERS));

	private static Map<SimulationAgent, Map<IAgent, GamaSoundPlayer>> soundPlayerOfAgents = new HashMap<SimulationAgent, Map<IAgent, GamaSoundPlayer>>();
	
	private static SoundPlayerBroker broker = null;
	
	
	public static SoundPlayerBroker getInstance() {
		
		if (broker == null) {  broker = new SoundPlayerBroker(); }
		
		return broker;
	}
	
	private void initializeGamaSoundPlayer() {
		synchronized (soundPlayerPools) {
			for (int i = 0; i < MAX_NB_OF_MUSIC_PLAYERS; i++) { soundPlayerPools.add(new GamaSoundPlayer()); }
		}
	}
	
	
	private SoundPlayerBroker() {
		initializeGamaSoundPlayer();
	}
	
	
	
	public GamaSoundPlayer getSoundPlayer(final IAgent agent) {
		
		synchronized (soundPlayerOfAgents) {
			final SimulationAgent simulation = agent.getScope().getSimulationScope();
			
			Map<IAgent, GamaSoundPlayer> soundPlayersOfSimulation = soundPlayerOfAgents.get(simulation);
			if (soundPlayersOfSimulation == null) {
				soundPlayersOfSimulation = new HashMap<IAgent, GamaSoundPlayer>();
				soundPlayerOfAgents.put(simulation, soundPlayersOfSimulation);
				
				
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
			
			
			GamaSoundPlayer soundPlayerOfAgent = soundPlayersOfSimulation.get(agent);
			if (soundPlayerOfAgent == null) {
				
				synchronized (soundPlayerPools) {
					if (!soundPlayerPools.isEmpty()) {
						soundPlayerOfAgent = soundPlayerPools.remove(0);
						soundPlayersOfSimulation.put(agent, soundPlayerOfAgent);
					}
				}
			}

			return soundPlayerOfAgent;
		}
	}
	
	
	public void manageMusicPlayers(final SimulationAgent simulation) throws GamaRuntimeException {
		GamaSoundPlayer soundPlayer;
		
		Map<IAgent, GamaSoundPlayer> soundPlayersOfSimulation = soundPlayerOfAgents.get(simulation);
		
		
		// remove music players of dead agents
		List<IAgent> deadAgents = new ArrayList<IAgent>();
		for (IAgent a : soundPlayersOfSimulation.keySet()) { if (a.dead()) deadAgents.add(a); }
		for (IAgent d : deadAgents) {
			soundPlayer = soundPlayersOfSimulation.get(d);
			soundPlayer.stop(true);
			
			soundPlayersOfSimulation.remove(d); 

			synchronized (soundPlayerPools) {
				soundPlayerPools.add(new GamaSoundPlayer());
			}
		}
		
		
		// remove music players already finished playing
		synchronized (soundPlayerOfAgents) {
			List<IAgent> agentsToBeRemoved = new ArrayList<IAgent>();
			for (IAgent a : soundPlayersOfSimulation.keySet()) {
				
				soundPlayer = soundPlayersOfSimulation.get(a);
				if (soundPlayer.canBeReused()) { agentsToBeRemoved.add(a); }
			}
			
			for (IAgent a : agentsToBeRemoved) {
				soundPlayersOfSimulation.remove(a);
				synchronized (soundPlayerPools) {
					soundPlayerPools.add(new GamaSoundPlayer());
				}
				
			}
		}
	}
	
	
	public void schedulerDisposed(final SimulationAgent simulation) throws GamaRuntimeException {
		
		Map<IAgent, GamaSoundPlayer> soundPlayersOfSimulation = soundPlayerOfAgents.get(simulation);
		
		if (soundPlayersOfSimulation != null) {
			for (GamaSoundPlayer player : soundPlayersOfSimulation.values()) { player.stop(true); }
			synchronized (soundPlayerPools) {
				soundPlayerPools.clear();
				initializeGamaSoundPlayer();
			}
			
			
			soundPlayersOfSimulation.clear();
			soundPlayerOfAgents.remove(simulation);
		}
	}
}
