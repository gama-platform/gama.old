package msi.gama.headless.runtime;

import java.io.File;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;

public interface RuntimeContext {
	public IExperimentPlan buildExperimentPlan(String expName, IModel mdl);
	public IModel loadModel(File fl);
	
	
}
