package msi.gama.headless.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import msi.gama.common.interfaces.IBuilderListener;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.ErrorCollector;
import msi.gama.common.util.FileUtils;
import msi.gama.common.util.GuiUtils;
import msi.gama.headless.io.HeadlessIO;
import msi.gama.headless.runtime.HeadlessListener;
import msi.gama.kernel.experiment.AbstractExperiment;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.utils.GamlToSyntacticElements;
import msi.gama.outputs.FileOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.GamaBundleLoader;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.ModelStructure;
import msi.gaml.types.IType;



public class HeadlessSimulationLoader  {
	private  static Set<IBuilderListener> listeners;
	
	public static IHeadLessExperiment  newHeadlessSimulation(String fileName)
	{
		configureHeadLessSimulation();
		preloadGAMA();
		loadModel(fileName);
		return (IHeadLessExperiment)GAMA.getExperiment();
	}
	
	private static void configureHeadLessSimulation()
	{
		System.setProperty("java.awt.headless","true");	
		GuiUtils.setHeadLessMode();
		listeners = new HashSet();
		try {
			new  BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("coucou"))));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void preloadGAMA()
	{
		System.out.println("GAMA configuring and loading...");
		FileUtils.setFileAccess(new HeadlessIO());
		GuiUtils.setSwtGui(new HeadlessListener());
		try {
			GamaBundleLoader.preBuildContributions();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("GAMA loading complete");
	}

	private static void loadModel(String fileName)
	{
		System.out.println( fileName + " model is loading...");

		IModel lastModel = null;
		ErrorCollector collect = new ErrorCollector();
		GamlStandaloneSetup.doSetup();		
		ResourceSet rs = new ResourceSetImpl();
		Resource r = rs.getResource(URI.createURI("file:"+fileName), true);
		fireBuildStarted(r);
		try {
			Map<Resource, ISyntacticElement> elements =
				GamlToSyntacticElements.buildSyntacticTree(r, collect);
			if ( !collect.hasErrors() ) {
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
			//collectErrors(collect);
			//fireBuildEnded(m, lastModel);
		}
		
		GAMA.newExperiment(IKeyword.DEFAULT, lastModel);
		System.out.println("Experiment created " );
	}
	
	private static void fireBuildStarted(final Resource r) {
		for ( IBuilderListener l : listeners ) {
			l.beforeBuilding(r);
		}
	}

	private static void fireBuildEnded(final Model m, final IModel result) {
		// System.out.println("Informing of the end of the build");
		for ( IBuilderListener l : new ArrayList<IBuilderListener>(listeners) ) {
			l.afterBuilding(m.eResource(), result);
		}
	}


	

}
