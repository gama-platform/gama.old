package msi.gama.headless.core;

import java.util.Map;

import msi.gama.common.util.GuiUtils;
import msi.gama.headless.runtime.HeadlessListener;
import msi.gama.kernel.experiment.ExperimentSpecies;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
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
	public static ExperimentSpecies newHeadlessSimulation(final IModel model,
			final String expName,
		final ParametersSet params) throws GamaRuntimeException {
		// FIXME Verify all this.
		configureHeadLessSimulation();
		// preloadGAMA();  
		// IModel model = loadModel(fileName);
		// if ( model == null ) {
		// System.out.println("GAMA cannot load model " + fileName);
		// return null;
		// }
		ExperimentSpecies exp1 = (ExperimentSpecies) model.getExperiment(expName);
		if ( exp1 == null ) {
			System.out.println("Experiment " + expName + " cannot be created");
			return null;
		}
		waitLoading(exp1);
		for ( Map.Entry<String, Object> entry : params.entrySet() ) {
			exp1.setParameterValue(entry.getKey(), entry.getValue());
		}
		exp1.createAgent();
		SimulationAgent sim = exp1.getAgent().createSimulation(
				new ParametersSet(), true);
		// GAMA.controller.newHeadlessExperiment(exp1);
		waitLoading(exp1);
		return exp1;
	}

	private static void waitLoading(final ExperimentSpecies exp) {
		System.out.println("Simulation loading...");
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void configureHeadLessSimulation() {
		System.setProperty("java.awt.headless", "true");
		GuiUtils.setHeadLessMode();
	}

	public static void preloadGAMA() {
		System.out.println("GAMA configuring and loading...");
		GuiUtils.setSwtGui(new HeadlessListener());
		try {
			GamaBundleLoader.preBuildContributions();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Impossible to load GAMA");
			System.exit(-1);
		}
		injector = new GamlStandaloneSetup().createInjectorAndDoEMFRegistration();
		System.out.println("GAMA loading complete");
	}

	public static IModel loadModel(final String fileName) {
		System.out.println(fileName + " model is loading...");
		IModel lastModel = null;
		ResourceSet rs = new ResourceSetImpl();
		GamlResource r = (GamlResource) rs.getResource(URI.createURI("file:///" + fileName), true);
		if ( r != null && r.getErrors().isEmpty() ) {
			try {
				GamlJavaValidator validator = injector.getInstance(GamlJavaValidator.class);
				lastModel = validator.build(r);
				if ( !r.getErrors().isEmpty() ) {
					lastModel = null;
					System.out.println("GAMA cannot build model " + fileName);
					for ( Resource.Diagnostic d : r.getErrors() ) {
						System.out.println(">> Error " + d.getMessage());
					}
				}

			} catch (GamaRuntimeException e1) {
				System.out.println("Exception during compilation:" + e1.getMessage());
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			System.out.println("Experiment created ");
		} else {
			System.out.println("Xtext cannot parse model " + fileName);
		}
		return lastModel;
	}

}
