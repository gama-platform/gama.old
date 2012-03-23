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
import msi.gama.headless.core.HeadLessExperiment;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.core.IHeadLessExperiment;
import msi.gama.headless.io.HeadlessIO;
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
	
	public static boolean headLessSimulation = false;

	private  Set<IBuilderListener> listeners;
	
	private static boolean isHeadlessSimulation()
	{
		return headLessSimulation;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(IApplicationContext context) throws Exception {
		IHeadLessExperiment exp=HeadlessSimulationLoader.newHeadlessSimulation("/tmp/src/model1.gaml");
		
		

		
		
		
		
				
		/*Thread.sleep(1000);
		System.out.println("Initialize experiment");
		GAMA.getExperiment().initialize(new ParametersSet(), 0.0);
		Thread.sleep(1000);
		*/
		System.out.println("Starting experiment");
	/*	Runnable rnb = new Runnable(){ public void run() {GAMA.startOrPauseExperiment();}};
		Thread exec = new Thread(rnb);
		exec.start();
		*/
		//GAMA.startOrPauseExperiment();
	//	GAMA.startOrPauseExperiment();
		//GAMA.getExperiment().startCurrentSimulation();
//		System.out.println("ccoucoe 3");
//		
//		
//		
//		
		Thread.sleep(2000);

		
		//GAMA.startOrPauseExperiment();
		//
		GAMA.getExperiment().step();	
		System.out.println("Step experimentfdsqf*****************************************************************");
		GAMA.getExperiment().step();		
		System.out.println("Step experimentfdsqf*****************************************************************");
		GAMA.getExperiment().step();
		System.out.println("Step experimentfdsqf");
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
