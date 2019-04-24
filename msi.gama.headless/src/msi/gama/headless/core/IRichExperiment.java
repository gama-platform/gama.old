package msi.gama.headless.core;

import msi.gama.headless.job.ExperimentJob.ListenedVariable;
import msi.gama.headless.job.ExperimentJob.OutputType;

public interface IRichExperiment extends IExperiment{
	public RichOutput getRichOutput(final ListenedVariable v);
	public OutputType getTypeOf(final String name);
}
