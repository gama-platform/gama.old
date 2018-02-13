package msi.gama.headless.runtime;

import java.io.File;
import java.io.IOException;

import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;

public interface RuntimeContext {
	public IExperimentPlan buildExperimentPlan(String expName, IModel mdl);

	public IModel loadModel(File fl) throws IOException, GamaHeadlessException;

}
