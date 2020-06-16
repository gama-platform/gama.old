package msi.gama.headless.core;

import msi.gama.headless.job.ExperimentJob.OutputType;
import msi.gama.headless.job.ListenedVariable;

public interface IRichExperiment extends IExperiment{
	public RichOutput getRichOutput(final ListenedVariable v);
	public OutputType getTypeOf(final String name);
}
