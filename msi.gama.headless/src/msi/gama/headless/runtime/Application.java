package msi.gama.headless.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import msi.gama.common.interfaces.IBuilderListener;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.ErrorCollector;
import msi.gama.common.util.FileUtils;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtIO;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.GamlStandaloneSetup;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.utils.GamlToSyntacticElements;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GamaBundleLoader;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.ModelStructure;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication {
	private  Set<IBuilderListener> listeners;

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(IApplicationContext context) throws Exception {
		// TODO Auto-generated method stub
		listeners = new HashSet();
		System.out.println("Configuring file access through SWT");
		FileUtils.setFileAccess(new SwtIO());
		GuiUtils.setSwtGui(new HeadlessListener());
		System.out.println("coucou start dfzff");
		try {
			GamaBundleLoader.preBuildContributions();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		IModel lastModel = null;
		System.setProperty("java.awt.headless","true");	

		ErrorCollector collect = new ErrorCollector();
		GamlStandaloneSetup.doSetup();		
		ResourceSet rs = new ResourceSetImpl();
		Resource r = rs.getResource(URI.createURI("file:/tmp/src/model1.gaml"), true);
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
		System.out.println("Experiment created " + Thread.currentThread().getId());
		
		Thread.sleep(1000);
		System.out.println("Initialize experiment");
		GAMA.getExperiment().initialize(new ParametersSet(), 0.0);
		Thread.sleep(1000);
		
		System.out.println("Starting experiment");
	/*	Runnable rnb = new Runnable(){ public void run() {GAMA.startOrPauseExperiment();}};
		Thread exec = new Thread(rnb);
		exec.start();
		*/
		GAMA.startOrPauseExperiment();
	//	GAMA.startOrPauseExperiment();
		//GAMA.getExperiment().startCurrentSimulation();
//		System.out.println("ccoucoe 3");
//		
//		
//		
//		
		Thread.sleep(5000);
		System.out.println("Stopping experiment");

		GAMA.getExperiment().stop();
		Thread.sleep(2000);
		System.out.println("Step experiment");

	GAMA.getExperiment().step();
//		System.out.println("ccoucoe 4");
//
//		GAMA.getExperiment().step();
//		
//		System.out.println("coucou start end vsqdfdsqdf");
//		
		Thread.sleep(1000000);
		
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
	private void fireBuildStarted(final Resource r) {
		for ( IBuilderListener l : listeners ) {
			l.beforeBuilding(r);
		}
	}

	private void fireBuildEnded(final Model m, final IModel result) {
		// System.out.println("Informing of the end of the build");
		for ( IBuilderListener l : new ArrayList<IBuilderListener>(listeners) ) {
			l.afterBuilding(m.eResource(), result);
		}
	}


}
