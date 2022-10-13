/*******************************************************************************************************
 *
 * IExperiment.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.core;

import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.file.json.GamaJsonList;
import msi.gaml.expressions.IExpression;

/**
 * The Interface IExperiment.
 */
public interface IExperiment {

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	IModel getModel();

	/**
	 * Gets the experiment plan.
	 *
	 * @return the experiment plan
	 */
	IExperimentPlan getExperimentPlan();

	/**
	 * Gets the simulation.
	 *
	 * @return the simulation
	 */
	SimulationAgent getSimulation();

	/**
	 * Setup.
	 *
	 * @param experimentName
	 *            the experiment name
	 * @param seed
	 *            the seed
	 */
	void setup(final String experimentName, final double seed);

	/**
	 * Setup.
	 *
	 * @param experimentName
	 *            the experiment name
	 * @param seed
	 *            the seed
	 * @param manualExperimentJob
	 */
	void setup(final String experimentName, final double seed, final GamaJsonList params,
			ManualExperimentJob manualExperimentJob);

	/**
	 * Step.
	 *
	 * @return the long
	 */
	long step();

	/**
	 * Checks if is interrupted.
	 *
	 * @return true, if is interrupted
	 */
	boolean isInterrupted();

	/**
	 * Sets the parameter.
	 *
	 * @param parameterName
	 *            the parameter name
	 * @param value
	 *            the value
	 */
	void setParameter(final String parameterName, final Object value);

	/**
	 * Gets the output.
	 *
	 * @param parameterName
	 *            the parameter name
	 * @return the output
	 */
	Object getOutput(final String parameterName);

	/**
	 * Gets the variable output.
	 *
	 * @param parameterName
	 *            the parameter name
	 * @return the variable output
	 */
	Object getVariableOutput(final String parameterName);

	/**
	 * Compile expression.
	 *
	 * @param expression
	 *            the expression
	 * @return the i expression
	 */
	IExpression compileExpression(final String expression);

	/**
	 * Evaluate expression.
	 *
	 * @param exp
	 *            the exp
	 * @return the object
	 */
	Object evaluateExpression(IExpression exp);

	/**
	 * Evaluate expression.
	 *
	 * @param exp
	 *            the exp
	 * @return the object
	 */
	Object evaluateExpression(String exp);

	/**
	 * Dispose.
	 */
	void dispose();

}
