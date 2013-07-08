package msi.gama.headless.core;

import msi.gama.kernel.experiment.IExperimentSpecies;

public interface IHeadLessExperiment extends IExperimentSpecies {

	public void start(int nbStep);

	// @Override
	public void setParameterValue(String name, Object value);

	//
	// public Object getParameterWithName(String name) throws GamaRuntimeException,
	// InterruptedException;

	public void setSeed(double seed);
}
