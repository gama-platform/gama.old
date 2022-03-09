/*******************************************************************************************************
 *
 * SimulationRuntime.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.runtime;

import static msi.gama.headless.common.Globals.CONSOLE_OUTPUT_FILENAME;
import static msi.gama.headless.common.Globals.OUTPUT_PATH;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import msi.gama.headless.job.ExperimentJob;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Interface SimulationRuntime.
 */
public interface SimulationRuntime {

	/** The undefined queue size. */
	int UNDEFINED_QUEUE_SIZE = 32; // Integer.MAX_VALUE;

	/**
	 * The Class DebugStream.
	 */
	class DebugStream extends FileOutputStream {

		/**
		 * Instantiates a new debug stream.
		 *
		 * @throws FileNotFoundException
		 *             the file not found exception
		 */
		DebugStream(final ExperimentJob si) throws FileNotFoundException {
			super(OUTPUT_PATH + "/" + CONSOLE_OUTPUT_FILENAME + "-" + si.getExperimentID() + ".txt");
			DEBUG.REGISTER_LOG_WRITER(this);
		}

		@Override
		public void close() throws IOException {
			super.close();
			DEBUG.UNREGISTER_LOG_WRITER();
		}

	}

	/**
	 * Push simulation.
	 *
	 * @param s
	 *            the s
	 */
	void pushSimulation(ExperimentJob s);

	/**
	 * Checks if is performing simulation.
	 *
	 * @return true, if is performing simulation
	 */
	boolean isPerformingSimulation();

}
