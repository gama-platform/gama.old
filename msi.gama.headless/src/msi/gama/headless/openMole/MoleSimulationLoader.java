package msi.gama.headless.openMole;

import java.io.File;

import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.model.IModel;

public abstract class MoleSimulationLoader {
	
	public static void loadGAMA()
	{
		HeadlessSimulationLoader.preloadGAMA();
	}
	
	public static IModel loadModel(final File modelPath)
	{
		return HeadlessSimulationLoader.loadModel(modelPath);
	}
	
	public static IMoleExperiment newExperiment(final IModel model)
	{
		return new MoleExperiment(model);
	}
	
}
