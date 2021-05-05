package msi.gama.headless.runtime;

import java.io.File;
import java.io.IOException;

import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;

public interface RuntimeContext {

	IExperimentPlan buildExperimentPlan(String expName, IModel mdl);

	IModel loadModel(File fl) throws IOException, GamaHeadlessException;

}
