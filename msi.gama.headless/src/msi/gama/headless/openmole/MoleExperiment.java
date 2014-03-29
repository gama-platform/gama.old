package msi.gama.headless.openmole;

import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.experiment.ExperimentSpecies;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class MoleExperiment implements IMoleExperiment {
	private  ExperimentSpecies currentExperiment; 
	private ParametersSet params;
	private IModel model;
	private String experimentName;
	private long seed;
	private long currentStep;
	
	MoleExperiment(IModel mdl)
	{
		this.model = mdl;
		this.params = new ParametersSet();
	}
	
	private IScope getScope()
	{
		return this.currentExperiment.getCurrentSimulation().getScope();
	}
	

	public void setup(final String expName) {
		this.seed = 0;
		this.loadCurrentExperiment(expName);
	}
	
	public void setup(final String expName, final long sd) {
		this.seed = sd;
		this.loadCurrentExperiment(expName);
	}
	
	private synchronized void loadCurrentExperiment(final String expName)
	{
		this.experimentName = expName;
		this.currentStep = 0;
		this.currentExperiment = HeadlessSimulationLoader.newHeadlessSimulation(model, experimentName, this.params);
	}
	
	public long step() {
		this.currentExperiment.getCurrentSimulation().step(this.getScope());
		return currentStep ++;
	}

	public void setParameter(String parameterName, Object value) {
		if(this.params.containsKey(parameterName))
		{
			this.params.remove(parameterName);
		}
		
		this.params.put(parameterName, value);
	}

	public Object getOutput(final String parameterName) {
		IOutput output = ((AbstractOutputManager) this.currentExperiment.getSimulationOutputs()).getOutputWithName(parameterName);
		if (output ==null || ! (output instanceof MonitorOutput) )
			throw new GamaRuntimeException(new Throwable("Output unresolved"));
		this.currentExperiment.getSimulationOutputs().step(this.getScope());
		return ((MonitorOutput) output).getLastValue();
	}

	public Object getVariableOutput(final String parameterName) {
		this.currentExperiment.getSimulationOutputs().step(this.getScope());
		Object res = this.currentExperiment.getCurrentSimulation().getDirectVarValue(this.getScope(), parameterName);
		if (res ==null  )
			throw new GamaRuntimeException(new Throwable("Output unresolved"));
		return res;
	}

	public void dispose()
	{
		this.currentExperiment.dispose();
	}
}
