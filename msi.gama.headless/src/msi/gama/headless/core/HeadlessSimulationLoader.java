package msi.gama.headless.core;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.headless.runtime.HeadlessListener;
import msi.gama.kernel.experiment.ExperimentSpecies;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaBundleLoader;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import com.google.inject.Injector;

public class HeadlessSimulationLoader {

	static Injector injector;

	/**
	 * 
	 * load in headless mode a specified model and create an experiment
	 * @param fileName model to load
	 * @param params parameters of the experiment
	 * @return the loaded experiment
	 * @throws GamaRuntimeException
	 * @throws InterruptedException
	 */
	public static synchronized ExperimentSpecies newHeadlessSimulation(final IModel model,final String expName,final ParametersSet params) throws GamaRuntimeException {
		// FIXME Verify all this.
		configureHeadLessSimulation();
		ExperimentSpecies currentExperiment = (ExperimentSpecies) model.getExperiment(expName);
		if ( currentExperiment == null ) {
			throw new GamaRuntimeException(new Throwable("Experiment " + expName + " cannot be created")); 
		}
		
		//Little Hack
		//waitLoading(exp1);
		
		for ( Map.Entry<String, Object> entry : params.entrySet() ) {
			currentExperiment.setParameterValue(entry.getKey(), entry.getValue());
		}
		currentExperiment.createAgent();
		SimulationAgent sim = currentExperiment.getAgent().createSimulation(
				new ParametersSet(), true);
		 GAMA.controller.newHeadlessExperiment(currentExperiment);
		//Little
		//waitLoading(currentExperiment);
		return currentExperiment;
	}
/*
	private static void waitLoading(final ExperimentSpecies exp) {
		System.out.println("Simulation loading...");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
*/
	private static void configureHeadLessSimulation() {
		System.setProperty("java.awt.headless", "true");
		GuiUtils.setHeadLessMode();
	}

	public static void preloadGAMA() {
		Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("GAMA configuring and loading...");
		GuiUtils.setSwtGui(new HeadlessListener());
		try {
			GamaBundleLoader.preBuildContributions();
		} catch (Exception e1) {
			throw new GamaRuntimeException(e1); 
		}
		
		//SEED HACK
		GamaPreferences.CORE_SEED_DEFINED.set(true);
		GamaPreferences.CORE_SEED.set(1.0);
		//SEED HACK
		
		injector = new GamlStandaloneSetup().createInjectorAndDoEMFRegistration();
		Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("GAMA loading complete");
	}

	public static IModel loadModel(File myFile) {
	//	System.out.println("coucocu "+myFile.getAbsolutePath());
		return loadModel(myFile.getAbsolutePath());
	}	
	
	public static synchronized  IModel loadModel(final String fileName)
	{
		Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer(fileName+ " model is loading...");
		IModel lastModel = null;
		ResourceSet rs = new ResourceSetImpl();
		
		GamlResource r = (GamlResource) rs.getResource(URI.createURI("file:///" + fileName), true);
		if ( r != null && r.getErrors().isEmpty() ) {
			try {
			//	Injector mInjector = new GamlStandaloneSetup().createInjectorAndDoEMFRegistration();
				
				GamlJavaValidator validator = new GamlJavaValidator(); //.getInstance(GamlJavaValidator.class);
				lastModel = validator.build(r);
				if ( !r.getErrors().isEmpty() ) {
					lastModel = null;
					Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("GAMA cannot build model " + fileName);
					for ( Resource.Diagnostic d : r.getErrors() ) {
						Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer(">> Error " + d.getMessage());
					}
				}

			} catch (GamaRuntimeException e1) {
				throw e1;
			} catch (Exception e2) {
				throw new RuntimeException(e2);
			}
			Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("Experiment created ");
		} else {
			Logger.getLogger(HeadlessSimulationLoader.class.getName()).finer("Xtext cannot parse model " + fileName);
		}
		return lastModel;
	}

}
