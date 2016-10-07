/*********************************************************************************************
 *
 *
 * 'MoleExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.core;

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

public class Experiment implements IExperiment {

	public static final double DEFAULT_SEED_VALUE = 0;

	protected IExperimentPlan currentExperiment;
	protected SimulationAgent currentSimulation;
	protected ParametersSet params;
	protected IModel model;
	protected String experimentName;
	protected double seed;
	protected long currentStep;

	protected Experiment() {
		super();
		currentExperiment = null;
		params = new ParametersSet();
		model = null;
		experimentName = null;
		seed = DEFAULT_SEED_VALUE;
	}

	public Experiment(final IModel mdl) {
		this();
		this.model = mdl;
	}

	protected IScope getScope() {
		return this.currentExperiment.getCurrentSimulation().getScope();
	}

	@Override
	public void setup(final String expName) {
		this.seed = 0;
		this.loadCurrentExperiment(expName);
	}

	@Override
	public void setup(final String expName, final double sd) {
		this.seed = sd;
		this.loadCurrentExperiment(expName);
	}

	private synchronized void loadCurrentExperiment(final String expName) {
		this.experimentName = expName;
		this.currentStep = 0;
		this.currentExperiment = GAMA.addHeadlessExperiment(model, experimentName, this.params, seed);
		this.currentSimulation = this.currentExperiment.getAgent().getSimulation();
		// this.currentExperiment.isHeadless()
		this.currentExperiment.setHeadless(true);
	}

	@Override
	public long step() {
		if (currentExperiment.isBatch()) {
			// Currently, the batch have the own way to control their
			// simulations so we call the experiment to do step instead of
			// demand simulation
			// MUST BE RE-ORGANIZE [ THE MULTI-SIMULATION + HEADLESS SIMULATION
			// + BATCH SIMULATION ]
			// AD commented this
			// currentExperiment.getAgent().getSimulation().removeAgent();
			currentExperiment.getController().getScheduler().paused = false;
			currentExperiment.getAgent().step(currentExperiment.getAgent().getScope());
		} else {
			currentExperiment.getAgent().getSimulation().step(this.getScope());
		}
		return currentStep++;

	}

	@Override
	public void setParameter(final String parameterName, final Object value) {
		if (this.params.containsKey(parameterName)) {
			this.params.remove(parameterName);
		}
		this.params.put(parameterName, value);
	}

	@Override
	public Object getOutput(final String parameterName) {
		final IOutput output = ((AbstractOutputManager) currentSimulation.getOutputManager())
				.getOutputWithOriginalName(parameterName);
		// System.out.
		if (output == null)
			throw GamaRuntimeException.error("Output does not exist: " + parameterName);
		if (!(output instanceof MonitorOutput))
			throw GamaRuntimeException.error("Output " + parameterName + " is not an alphanumeric data.");
		output.update();
		return ((MonitorOutput) output).getLastValue();
	}

	@Override
	public Object getVariableOutput(final String parameterName) {
		// this.currentExperiment.getSimulationOutputs().step(this.getScope());
		final Object res = this.currentExperiment.getCurrentSimulation().getDirectVarValue(this.getScope(),
				parameterName);
		if (res == null) {
			throw GamaRuntimeException.error("Output unresolved: " + parameterName);
		}
		return res;
	}

	@Override
	public void dispose() {
		GAMA.closeExperiment(currentExperiment);
	}

	@Override
	public boolean isInterrupted() {
		return currentExperiment.getAgent().getSimulation() == null
				|| currentExperiment.getAgent().getSimulation().dead()
				|| currentExperiment.getAgent().getSimulation().getScope().interrupted();// currentExperiment.getAgent().dead();
																							// //().interrupted();
	}

	@Override
	public IModel getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}

	@Override
	public IExperimentPlan getExperimentPlan() {
		return this.currentExperiment;
	}
}
