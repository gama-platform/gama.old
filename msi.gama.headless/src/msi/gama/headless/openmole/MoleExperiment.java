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
package msi.gama.headless.openmole;

import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MoleExperiment implements IMoleExperiment {

	private IExperimentPlan currentExperiment;
	private final ParametersSet params;
	private final IModel model;
	private String experimentName;
	private long seed;
	private long currentStep;

	MoleExperiment(final IModel mdl) {
		this.model = mdl;
		this.params = new ParametersSet();
	}

	private IScope getScope() {
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
	}

	@Override
	public long step() {
		this.currentExperiment.getCurrentSimulation().step(this.getScope());
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
			((AbstractOutputManager) this.currentExperiment.getSimulationOutputs()).getOutputWithName(parameterName);
		if ( output == null ||
			!(output instanceof MonitorOutput) ) { throw GamaRuntimeException.error("Output unresolved"); }
		this.currentExperiment.getSimulationOutputs().step(this.getScope());
		return ((MonitorOutput) output).getLastValue();
	}

	@Override
	public Object getVariableOutput(final String parameterName) {
		this.currentExperiment.getSimulationOutputs().step(this.getScope());
		Object res = this.currentExperiment.getCurrentSimulation().getDirectVarValue(this.getScope(), parameterName);
		if ( res == null ) { throw GamaRuntimeException.error("Output unresolved"); }
		return res;
	}

	@Override
	public void dispose() {
		this.currentExperiment.dispose();
	}
}
