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

import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class Experiment implements IExperiment {

	public static final long DEFAULT_SEED_VALUE = 0;

	protected IExperimentPlan currentExperiment;
	protected SimulationAgent currentSimulation;
	protected ParametersSet params;
	protected IModel model;
	protected String experimentName;
	protected long seed;
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
	public void setup(final String expName, final long sd) {
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
		currentExperiment.getController().userStep();
		currentExperiment.getAgent().getSimulation().step(this.getScope());
		return currentStep++;

	}

	@Override
	public void setParameter(final String parameterName, final Object value) {
		if ( this.params.containsKey(parameterName) ) {
			this.params.remove(parameterName);
		}
		this.params.put(parameterName, value);
	}

	@Override
	public Object getOutput(final String parameterName) {
		IOutput output =
			((AbstractOutputManager) currentSimulation.getOutputManager()).getOutputWithOriginalName(parameterName);

		if ( output == null ||
			!(output instanceof MonitorOutput) ) { throw GamaRuntimeException.error("Output unresolved"); }
		// this.currentExperiment.getSimulationOutputs().step(this.getScope());
		output.update();
		return ((MonitorOutput) output).getLastValue();
	}

	@Override
	public Object getVariableOutput(final String parameterName) {
		// this.currentExperiment.getSimulationOutputs().step(this.getScope());
		Object res = this.currentExperiment.getCurrentSimulation().getDirectVarValue(this.getScope(), parameterName);
		if ( res == null ) { throw GamaRuntimeException.error("Output unresolved"); }
		return res;
	}

	@Override
	public void dispose() {
		GAMA.closeExperiment(currentExperiment);
	}

	@Override
	public boolean isInterrupted() {
		return currentExperiment.getAgent().getSimulation() == null ||
			currentExperiment.getAgent().getSimulation().dead() ||
			currentExperiment.getAgent().getSimulation().getScope().interrupted();// currentExperiment.getAgent().dead(); //().interrupted();
	}
}
