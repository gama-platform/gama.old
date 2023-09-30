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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import msi.gama.kernel.experiment.ISimulationRecorder;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.tree.GamaNode;
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
			GamaNode<byte[]> current = sim.createNewHistoryNode(state);
			asyncZip(current, startTime);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Async zip.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param node
	 *            the node
	 * @date 8 août 2023
	 */
	protected void asyncZip(final GamaNode<byte[]> node, final long startTime) {
		executor.execute(() -> {
			node.setData(ByteArrayZipper.zip(node.getData()));
			DEBUG.OUT(
					"Serialised in " + processor.getFormat() + " and compressed to " + node.getData().length / 1000000d
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
			GamaNode<byte[]> current = sim.getPreviousHistoryNode();
			if (current != null) {
				long startTime = System.nanoTime();
				processor.restoreAgentFromBytes(sim, ByteArrayZipper.unzip(current.getData()));
				DEBUG.OUT("Deserialise from " + processor.getFormat() + " in "
						+ TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + "ms");
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
		GamaNode<byte[]> current = sim.getCurrentHistoryNode();
		return current != null && current.getParent() != null;
	}

}
