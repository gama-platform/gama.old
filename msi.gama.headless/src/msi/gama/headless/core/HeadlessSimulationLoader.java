package msi.gama.headless.core;

import java.util.Map;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.headless.runtime.HeadlessListener;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.GamlJavaValidator;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaBundleLoader;
import org.eclipse.emf.common.util.URI;
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
	public static ExperimentSpecies newHeadlessSimulation(final String fileName) {
		configureHeadLessSimulation();
		preloadGAMA();
		IModel model = loadModel(fileName);
		IExperimentSpecies tt = model.getExperiment("preyPred");
		
		//IHeadLessExperiment exp = (IHeadLessExperiment) model.getExperiment("preyPred");;
		ExperimentSpecies exp = (ExperimentSpecies) model.getExperiment("preyPred");;
		
		HeadLessExperiment hexp=new HeadLessExperiment(exp);
		System.out.println("coucouc " + "  pouet " + tt.getClass().getName());

		/*
		 * for (ISpecies sp : ((GamlModelSpecies)model).getExperiments()) {
		 * System.out.println("coucouc " + "  pouet "+ sp.getName());
		 * if (sp instanceof IExperimentSpecies) {
		 * System.out.println("coucouc " + "  experiment "+ sp.getName());
		 * 
		 * exp = (IHeadLessExperiment) sp;
		 * }
		 * }
		 */
		waitLoading(exp);
		return exp; // (IHeadLessExperiment) GAMA.getExperiment();
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
	public static ExperimentSpecies newHeadlessSimulation(final String fileName, final ParametersSet params)
		throws GamaRuntimeException {
		// FIXME Verify all this.
		ExperimentSpecies exp = newHeadlessSimulation(fileName);
		exp.open();

		System.out.println("coucoucou  simulation is open.....");
		for ( Map.Entry<String, Object> entry : params.entrySet() ) {
			exp.setParameterValue(entry.getKey(), entry.getValue());
		}
		// FIXME ???
		// exp.schedule();
		waitLoading(exp);
		return exp;

	}

	private static void waitLoading(final ExperimentSpecies exp) {
		System.out.println("Simulation loading...");
		do {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println("test " + exp);
			//System.out.println("test2 " + exp.getModel());
			// System.out.println("test2 " + exp.isLoading());
		} while (exp.getCurrentSimulation() != null && false);
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

	private static IModel loadModel(final String fileName) {
		System.out.println(fileName + " model is loading...");

		IModel lastModel = null;
		ResourceSet rs = new ResourceSetImpl();
		GamlResource r = (GamlResource) rs.getResource(URI.createURI("file:///" + fileName), true);
		try {
			// GamlJavaValidator validator = new GamlJavaValidator(); //(GamlJavaValidator)
			// injector.getInstance(EValidator.class);

			GamlJavaValidator validator = injector.getInstance(GamlJavaValidator.class);
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
		// FIXME Experiment default no longer exists. Needs to specify one name
		GAMA.controller.newExperiment("preyPred", lastModel);
		System.out.println("Experiment created ");
		return lastModel;
	}

}
