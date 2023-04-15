/*******************************************************************************************************
 *
 * IExperimentJob.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.job;

import static msi.gama.headless.common.Globals.CONSOLE_OUTPUT_FILENAME;
import static msi.gama.headless.common.Globals.OUTPUT_PATH;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.headless.core.GamaHeadlessException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Interface IExperimentJob.
 */
public interface IExperimentJob extends Runnable {

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
		DebugStream(final IExperimentJob si) throws FileNotFoundException {
			super(OUTPUT_PATH + "/" + CONSOLE_OUTPUT_FILENAME + "-" + si.getExperimentID() + ".txt");
			DEBUG.REGISTER_LOG_WRITER(this);
		}

		@Override
		public void close() throws IOException {
			super.close();
			DEBUG.UNREGISTER_LOG_WRITER();
		}

	}

	@Override
	default void run() {
		try (final DebugStream file = new DebugStream(this)) {
			loadAndBuild();
			playAndDispose();
		} catch (final Exception e) {
			DEBUG.ERR(e);
		}
	}

	/**
	 * Gets the experiment ID.
	 *
	 * @return the experiment ID
	 */
	String getExperimentID();

	/**
	 * Gets the experiment name.
	 *
	 * @return the experiment name
	 */
	String getExperimentName();

	/**
	 * Gets the model name.
	 *
	 * @return the model name
	 */
	String getModelName();

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	List<Parameter> getParameters();

	/**
	 * Gets the outputs.
	 *
	 * @return the outputs
	 */
	List<Output> getOutputs();

	/**
	 * Adds the parameter.
	 *
	 * @param p
	 *            the p
	 */
	void addParameter(final Parameter p);

	/**
	 * Adds the output.
	 *
	 * @param p
	 *            the p
	 */
	void addOutput(final Output p);

	/**
	 * Gets the output names.
	 *
	 * @return the output names
	 */
	List<String> getOutputNames();

	/**
	 * Removes the output with name.
	 *
	 * @param name
	 *            the name
	 */
	void removeOutputWithName(final String name);

	/**
	 * Sets the output frame rate.
	 *
	 * @param name
	 *            the name
	 * @param frate
	 *            the frate
	 */
	void setOutputFrameRate(final String name, final int frate);

	/**
	 * Sets the parameter value of.
	 *
	 * @param name
	 *            the name
	 * @param val
	 *            the val
	 */
	void setParameterValueOf(final String name, final Object val);

	/**
	 * Sets the seed.
	 *
	 * @param s
	 *            the new seed
	 */
	void setSeed(final double s);

	/**
	 * Gets the seed.
	 *
	 * @return the seed
	 */
	double getSeed();

	/**
	 * Gets the step.
	 *
	 * @return the step
	 */
	long getStep();

	/**
	 * Sets the final step.
	 *
	 * @param step
	 *            the new final step
	 */
	void setFinalStep(long step);

	/**
	 * Load and build.
	 *
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	void loadAndBuild() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException,
			GamaHeadlessException;

	/**
	 * As XML document.
	 *
	 * @param doc
	 *            the doc
	 * @return the element
	 */
	Element asXMLDocument(Document doc);

	/**
	 * Play and dispose.
	 */
	void playAndDispose();

	/**
	 * Play.
	 */
	void play();

	/**
	 * Dispose.
	 */
	void dispose();

	/**
	 * Do step.
	 */
	void doStep();

	void doBackStep();
}
