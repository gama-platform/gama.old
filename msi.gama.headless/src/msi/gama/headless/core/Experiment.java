/*******************************************************************************************************
 *
 * Experiment.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.core;

import msi.gama.headless.server.GamaServerExperimentJob;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.IExpression;

/**
 * The Class Experiment.
 */
public class Experiment implements IExperiment {

	/** The Constant DEFAULT_SEED_VALUE. */
	public static final double DEFAULT_SEED_VALUE = 0;

	/** The current experiment. */
	protected IExperimentPlan currentExperiment = null;

	/** The params. */
	protected ParametersSet params = new ParametersSet();

	/** The model. */
	final protected IModel model;

	/** The experiment name. */
	protected String experimentName = null;

	/** The seed. */
	protected double seed = DEFAULT_SEED_VALUE;

	/** The current step. */
	protected long currentStep;

	/**
	 * Instantiates a new experiment.
	 *
	 * @param mdl
	 *            the mdl
	 */
	public Experiment(final IModel mdl) {
		this.model = mdl;
	}

	@Override
	public SimulationAgent getSimulation() {
		return currentExperiment == null ? null : currentExperiment.getCurrentSimulation();
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	protected IScope getScope() {
		SimulationAgent sim = getSimulation();
		return sim == null ? null : sim.getScope();
	}

	@Override
	public void setup(final String expName, final double sd) {
		this.seed = sd;
		this.loadCurrentExperiment(expName);
	}

	/**
	 * Setup.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param expName
	 *            the exp name
	 * @param sd
	 *            the sd
	 * @param params
	 *            the params
	 * @param ec
	 *            the ec
	 * @date 28 oct. 2023
	 */
	@Override
	public void setup(final String expName, final double sd, final IList params, final GamaServerExperimentJob ec) {
		this.seed = sd;
		this.loadCurrentExperiment(expName, params, ec);
	}

	/**
	 * Load current experiment.
	 *
	 * @param expName
	 *            the exp name
	 */
	private synchronized void loadCurrentExperiment(final String expName, final IList p,
			final GamaServerExperimentJob ec) {
		this.experimentName = expName;
		this.currentStep = 0;

		final ExperimentPlan curExperiment = (ExperimentPlan) model.getExperiment(expName);
		curExperiment.setHeadless(true);
		curExperiment.setController(ec.controller);
		curExperiment.setParameterValues(p);
		curExperiment.open(seed);
		if (!GAMA.getControllers().contains(curExperiment.getController())) {
			GAMA.getControllers().add(curExperiment.getController());
		}
		this.currentExperiment = curExperiment;
		this.currentExperiment.setHeadless(true);
	}

	/**
	 * Load current experiment.
	 *
	 * @param expName
	 *            the exp name
	 */
	private synchronized void loadCurrentExperiment(final String expName) {
		this.experimentName = expName;
		this.currentStep = 0;
		this.currentExperiment = GAMA.addHeadlessExperiment(model, experimentName, this.params, seed);
		this.currentExperiment.setHeadless(true);
	}

	@Override
	public long step() {
		currentExperiment.getAgent().step(currentExperiment.getAgent().getScope());
		return currentStep++;

	}

	@Override
	public long backStep() {
		currentExperiment.getAgent().backward(currentExperiment.getAgent().getScope());
		return currentStep--;

	}

	@Override
	public void setParameter(final String parameterName, final Object value) {
		this.params.put(parameterName, value);
	}

	@Override
	public Object getOutput(final String parameterName) {
		final IOutput output =
				((AbstractOutputManager) getSimulation().getOutputManager()).getOutputWithOriginalName(parameterName);
		if (output == null) throw GamaRuntimeException.error("Output does not exist: " + parameterName, getScope());
		if (!(output instanceof MonitorOutput))
			throw GamaRuntimeException.error("Output " + parameterName + " is not an alphanumeric data.", getScope());
		output.update();
		return ((MonitorOutput) output).getLastValue();
	}

	@Override
	public Object getVariableOutput(final String parameterName) {
		final Object res = getSimulation().getDirectVarValue(getScope(), parameterName);
		if (res == null) throw GamaRuntimeException.error("Output unresolved: " + parameterName, getScope());
		return res;
	}

	@Override
	public void dispose() {
		GAMA.closeExperiment(currentExperiment);
		currentExperiment = null;
	}

	@Override
	public boolean isInterrupted() {
		final SimulationAgent sim = currentExperiment.getCurrentSimulation();
		if (currentExperiment.isBatch() && sim == null) return false;
		return sim == null || sim.dead() || sim.getScope().interrupted();
	}

	@Override
	public IModel getModel() { return this.model; }

	@Override
	public IExperimentPlan getExperimentPlan() { return this.currentExperiment; }

	@Override
	public IExpression compileExpression(final String expression) {
		return GAML.compileExpression(expression, this.getSimulation(), false);
	}

	@Override
	public Object evaluateExpression(final IExpression exp) {
		return exp.value(this.getSimulation().getScope());
	}

	@Override
	public Object evaluateExpression(final String exp) {
		final IExpression localExpression = compileExpression(exp);
		return evaluateExpression(localExpression);
	}

}
