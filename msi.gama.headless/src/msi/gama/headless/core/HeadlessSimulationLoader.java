package msi.gama.headless.core;

import java.io.*;
import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.headless.io.HeadlessIO;
import msi.gama.headless.runtime.HeadlessListener;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.linking.GamlLinker;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaBundleLoader;
import msi.gaml.factories.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.linking.ILinker;
import com.google.inject.Injector;

public class HeadlessSimulationLoader {

	private static Injector inject;

	private static GamlLinker linker;

	public static IHeadLessExperiment newHeadlessSimulation(final String fileName) {
		configureHeadLessSimulation();
		preloadGAMA();
		loadModel(fileName);
		return (IHeadLessExperiment) GAMA.getExperiment();
	}

	private static void configureHeadLessSimulation() {
		System.setProperty("java.awt.headless", "true");
		GuiUtils.setHeadLessMode();
		try {
			new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("coucou"))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void preloadGAMA() {
		System.out.println("GAMA configuring and loading...");
		FileUtils.setFileAccess(new HeadlessIO());
		GuiUtils.setSwtGui(new HeadlessListener());
		try {
			GamaBundleLoader.preBuildContributions();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		inject = new GamlStandaloneSetup().createInjectorAndDoEMFRegistration();
		linker = (GamlLinker) inject.getBinding(ILinker.class).getProvider().get();
		System.out.println("GAMA loading complete");
	}

	private static void loadModel(final String fileName) {
		System.out.println(fileName + " model is loading...");

		IModel lastModel = null;
		ErrorCollector collect = new ErrorCollector();
		ResourceSet rs = new ResourceSetImpl();
		Resource r = rs.getResource(URI.createURI("file:" + fileName), true);
		try {
			Map<Resource, ISyntacticElement> elements = linker.buildCompleteSyntacticTree(r);
			if ( !linker.hasErrors(r) ) {
				System.out.println("No errors in syntactic tree");
				ModelStructure ms = new ModelStructure(r, elements, collect);
				lastModel = (IModel) DescriptionFactory.getModelFactory().compile(ms, collect);
				if ( collect.hasErrors() ) {
					lastModel = null;
					// System.out.println("End compilation of " + m.getName());
				}
			}
		} catch (GamaRuntimeException e1) {
			System.out.println("Exception during compilation:" + e1.getMessage());
		} catch (InterruptedException e) {
			System.out.println("Compilation was aborted");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// collectErrors(collect);
			// fireBuildEnded(m, lastModel);
		}

		GAMA.newExperiment(IKeyword.DEFAULT, lastModel);
		System.out.println("Experiment created ");
	}

}
