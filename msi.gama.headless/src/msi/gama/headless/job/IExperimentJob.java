/*******************************************************************************************************
 *
 * IExperimentJob.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.job;

import java.io.IOException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.headless.core.GamaHeadlessException;

/**
 * The Interface IExperimentJob.
 */
public interface IExperimentJob {

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
	 * @param p the p
	 */
	void addParameter(final Parameter p);

	/**
	 * Adds the output.
	 *
	 * @param p the p
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
	 * @param name the name
	 */
	void removeOutputWithName(final String name);

	/**
	 * Sets the output frame rate.
	 *
	 * @param name the name
	 * @param frate the frate
	 */
	void setOutputFrameRate(final String name, final int frate);

	/**
	 * Sets the parameter value of.
	 *
	 * @param name the name
	 * @param val the val
	 */
	void setParameterValueOf(final String name, final Object val);

	/**
	 * Sets the seed.
	 *
	 * @param s the new seed
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
	 * @param step the new final step
	 */
	void setFinalStep(long step);

	/**
	 * Load and build.
	 *
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	void loadAndBuild() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException,
			GamaHeadlessException;

	/**
	 * As XML document.
	 *
	 * @param doc the doc
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
}
