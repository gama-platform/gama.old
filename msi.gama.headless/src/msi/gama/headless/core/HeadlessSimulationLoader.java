package msi.gama.headless.core;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.headless.runtime.HeadlessListener;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaBundleLoader;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import com.google.inject.Injector;

public class HeadlessSimulationLoader {

	static Injector injector;

	/**
	 * load in headless mode a specified model and create an experiment
	 * @param fileName model to load
	 * @return the loaded experiment
	 * @throws InterruptedException
	 */
	public static IHeadLessExperiment newHeadlessSimulation(final String fileName) {
		configureHeadLessSimulation();
		preloadGAMA();
		loadModel(fileName);
		IHeadLessExperiment exp = (IHeadLessExperiment) GAMA.getExperiment();
		waitLoading(exp);
		return (IHeadLessExperiment) GAMA.getExperiment();
	}

	/**
	 * 
	 * load in headless mode a specified model and create an experiment
	 * @param fileName model to load
	 * @param params parameters of the experiment
	 * @return the loaded experiment
	 * @throws GamaRuntimeException
	 * @throws InterruptedException
	 */
	public static IHeadLessExperiment newHeadlessSimulation(final String fileName,
		final ParametersSet params) throws GamaRuntimeException, InterruptedException {
		IHeadLessExperiment exp = newHeadlessSimulation(fileName);
		GAMA.getExperiment().initialize(params, Math.random());
		waitLoading(exp);
		return exp;

	}

	private static void waitLoading(final IHeadLessExperiment exp) {
		System.out.println("Simulation loading...");
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (exp.getCurrentSimulation() != null && exp.getCurrentSimulation().isLoading());
	}

	private static void configureHeadLessSimulation() {
		System.setProperty("java.awt.headless", "true");
		GuiUtils.setHeadLessMode();
	}

	private static void preloadGAMA() {
		System.out.println("GAMA configuring and loading...");
		GuiUtils.setSwtGui(new HeadlessListener());
		try {
			GamaBundleLoader.preBuildContributions();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		injector = new GamlStandaloneSetup().createInjectorAndDoEMFRegistration();
		System.out.println("GAMA loading complete");
	}

	private static void loadModel(final String fileName) {
		System.out.println(fileName + " model is loading...");

		IModel lastModel = null;
		ResourceSet rs = new ResourceSetImpl();
		GamlResource r = (GamlResource) rs.getResource(URI.createURI("file:///" + fileName), true);
		try {
			GamlJavaValidator validator =
				(GamlJavaValidator) injector.getInstance(EValidator.class);
			lastModel = validator.build(r);
			if ( !r.getErrors().isEmpty() ) {
				lastModel = null;
				// System.out.println("End compilation of " + m.getName());
			}

		} catch (GamaRuntimeException e1) {
			System.out.println("Exception during compilation:" + e1.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// collectErrors(collect);
			// fireBuildEnded(m, lastModel);
		}

		GAMA.newExperiment(IKeyword.DEFAULT, lastModel);
		System.out.println("Experiment created ");
	}

	private static void loadExperiment(final ParametersSet param) {
		/*
		 * if(para == null)
		 * {
		 * 
		 * }
		 */
	}

}
