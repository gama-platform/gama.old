/*******************************************************************************************************
 *
 * SerialisedSimulationRecorder.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import msi.gama.kernel.experiment.ISimulationRecorder;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.SerialisedAgent;
import msi.gama.util.ByteArrayZipper;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SerialisedSimulationRecorder. Used to record, store, and retrieve simulation states
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 8 août 2023
 */
public class SerialisedSimulationRecorder implements ISimulationRecorder, ISerialisationConstants {

	static {
		DEBUG.ON();
	}

	/**
	 * The Class HistoryNode.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 22 oct. 2023
	 */
	private static class HistoryNode {

		/** The bytes. */
		byte[] bytes;

		/** The cycle. */
		long cycle;

		/**
		 * Instantiates a new history node.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param state
		 *            the state
		 * @date 22 oct. 2023
		 */
		public HistoryNode(final byte[] state, final long cycle) {
			bytes = state;
			this.cycle = cycle;
		}

	}

	/**
	 * The Class SimulationHistory.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 22 oct. 2023
	 */
	private static class SimulationHistory extends LinkedList<HistoryNode> {}

	/** The executor. */
	ExecutorService executor = Executors.newCachedThreadPool();

	/** The processor. */
	ISerialisationProcessor processor;

	/**
	 * Instantiates a new serialised simulation recorder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param format
	 *            the format
	 * @param zip
	 *            the zip
	 * @date 8 août 2023
	 */
	public SerialisedSimulationRecorder() {
		processor = SerialisationProcessorFactory.create(BINARY_FORMAT);
	}

	/**
	 * Record.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 8 août 2023
	 */
	@Override
	public void record(final SimulationAgent sim) {
		try {
			long startTime = System.nanoTime();
			byte[] state = processor.saveAgentToBytes(sim.getScope(), sim);
			SimulationHistory history = getSimulationHistory(sim);
			HistoryNode node = new HistoryNode(state, sim.getClock().getCycle());
			history.push(node);
			asyncZip(node, startTime);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the simulation history.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @return the simulation history
	 * @date 22 oct. 2023
	 */
	private SimulationHistory getSimulationHistory(final SimulationAgent sim) {
		SimulationHistory history = (SimulationHistory) sim.getAttribute(SerialisedAgent.HISTORY_KEY);
		if (history == null) {
			history = new SimulationHistory();
			sim.setAttribute(SerialisedAgent.HISTORY_KEY, history);
		}
		return history;
	}

	/**
	 * Async zip.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param node
	 *            the node
	 * @date 8 août 2023
	 */
	protected void asyncZip(final HistoryNode node, final long startTime) {
		executor.execute(() -> {
			node.bytes = ByteArrayZipper.zip(node.bytes);
			DEBUG.OUT("Serialised in " + processor.getFormat() + " and compressed to " + node.bytes.length / 1000000d
					+ "Mb in " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");

		});
	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the sim
	 * @date 8 août 2023
	 */
	@Override
	public void restore(final SimulationAgent sim) {
		try {
			synchronized (sim) {
				SimulationHistory history = getSimulationHistory(sim);
				HistoryNode node = history.pop();
				if (node != null && node.cycle == sim.getClock().getCycle()) { node = history.pop(); }
				if (node != null) {
					long startTime = System.nanoTime();
					processor.restoreAgentFromBytes(sim, ByteArrayZipper.unzip(node.bytes));
					DEBUG.OUT("Deserialise from " + processor.getFormat() + " in "
							+ TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Can step back.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 * @return true, if successful
	 * @date 9 août 2023
	 */
	@Override
	public boolean canStepBack(final SimulationAgent sim) {
		return getSimulationHistory(sim).size() > 0;

	}

}
