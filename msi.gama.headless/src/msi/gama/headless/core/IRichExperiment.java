package msi.gama.headless.core;

import msi.gama.headless.job.ExperimentJob.OutputType;

public interface IRichExperiment extends IExperiment{
	public RichOutput getRichOutput(final String parameterName);
	public OutputType getTypeOf(final String name);
}
