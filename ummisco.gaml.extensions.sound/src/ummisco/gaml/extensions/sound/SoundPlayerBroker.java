/*******************************************************************************************************
 *
 * SoundPlayerBroker.java, in ummisco.gaml.extensions.sound, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class SoundPlayerBroker.
 */
public class SoundPlayerBroker {

	// the maximum number of BasicPlayer instant can only be 2. Increase this
	/** The Constant MAX_NB_OF_MUSIC_PLAYERS. */
	// number will raise error.
	private static final int MAX_NB_OF_MUSIC_PLAYERS = 2;

	/** The sound player pools. */
	private final List<GamaSoundPlayer> soundPlayerPools =
			Collections.synchronizedList(new ArrayList<GamaSoundPlayer>(MAX_NB_OF_MUSIC_PLAYERS));

	/** The sound player of agents. */
	private static Map<SimulationAgent, Map<IAgent, GamaSoundPlayer>> soundPlayerOfAgents =
			new HashMap<>();

	/** The broker. */
	private static volatile SoundPlayerBroker broker = null;

	/**
	 * Gets the single instance of SoundPlayerBroker.
	 *
	 * @return single instance of SoundPlayerBroker
	 */
	public static SoundPlayerBroker getInstance() {

		if (broker == null) {
			broker = new SoundPlayerBroker();
		}

		return broker;
	}

	/**
	 * Initialize gama sound player.
	 */
	private void initializeGamaSoundPlayer() {
		synchronized (soundPlayerPools) {
			for (int i = 0; i < MAX_NB_OF_MUSIC_PLAYERS; i++) {
				soundPlayerPools.add(new GamaSoundPlayer());
			}
		}
	}

	/**
	 * Instantiates a new sound player broker.
	 */
	private SoundPlayerBroker() {
		initializeGamaSoundPlayer();
	}

	/**
	 * Gets the sound player.
	 *
	 * @param agent the agent
	 * @return the sound player
	 */
	public GamaSoundPlayer getSoundPlayer(final IAgent agent) {

		synchronized (soundPlayerOfAgents) {
			final IScope scope = agent.getScope();
			final SimulationAgent simulation = scope.getSimulation();

			Map<IAgent, GamaSoundPlayer> soundPlayersOfSimulation = soundPlayerOfAgents.get(simulation);
			if (soundPlayersOfSimulation == null) {
				soundPlayersOfSimulation = new HashMap<>();
				soundPlayerOfAgents.put(simulation, soundPlayersOfSimulation);

				simulation.postEndAction(scope1 -> {
					broker.manageMusicPlayers(simulation);
					return null;
				});

				simulation.postDisposeAction(scope1 -> {
					broker.schedulerDisposed(simulation);
					return null;
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

	/**
	 * Manage music players.
	 *
	 * @param simulation the simulation
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void manageMusicPlayers(final SimulationAgent simulation) throws GamaRuntimeException {
		GamaSoundPlayer soundPlayer;

		final Map<IAgent, GamaSoundPlayer> soundPlayersOfSimulation = soundPlayerOfAgents.get(simulation);

		// remove music players of dead agents
		final List<IAgent> deadAgents = new ArrayList<>();
		for (final IAgent a : soundPlayersOfSimulation.keySet()) {
			if (a.dead()) {
				deadAgents.add(a);
			}
		}
		for (final IAgent d : deadAgents) {
			soundPlayer = soundPlayersOfSimulation.get(d);
			soundPlayer.stop(d.getScope(), true);

			soundPlayersOfSimulation.remove(d);

			synchronized (soundPlayerPools) {
				soundPlayerPools.add(new GamaSoundPlayer());
			}
		}

		// remove music players already finished playing
		synchronized (soundPlayerOfAgents) {
			final List<IAgent> agentsToBeRemoved = new ArrayList<>();
			for (final IAgent a : soundPlayersOfSimulation.keySet()) {

				soundPlayer = soundPlayersOfSimulation.get(a);
				if (soundPlayer.canBeReused()) {
					agentsToBeRemoved.add(a);
				}
			}

			for (final IAgent a : agentsToBeRemoved) {
				soundPlayersOfSimulation.remove(a);
				synchronized (soundPlayerPools) {
					soundPlayerPools.add(new GamaSoundPlayer());
				}

			}
		}
	}

	/**
	 * Scheduler disposed.
	 *
	 * @param simulation the simulation
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void schedulerDisposed(final SimulationAgent simulation) throws GamaRuntimeException {

		final Map<IAgent, GamaSoundPlayer> soundPlayersOfSimulation = soundPlayerOfAgents.get(simulation);

		if (soundPlayersOfSimulation != null) {
			for (final GamaSoundPlayer player : soundPlayersOfSimulation.values()) {
				player.stop(simulation.getScope(), true);
			}
			synchronized (soundPlayerPools) {
				soundPlayerPools.clear();
				initializeGamaSoundPlayer();
			}

			soundPlayersOfSimulation.clear();
			soundPlayerOfAgents.remove(simulation);
		}
	}
}
