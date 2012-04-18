package msi.gama.headless.runtime;

import msi.gama.headless.core.*;
import msi.gama.runtime.GAMA;
import org.eclipse.equinox.app.*;

public class Application implements IApplication {

	public static boolean headLessSimulation = false;

	private static boolean isHeadlessSimulation() {
		return headLessSimulation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception {
		IHeadLessExperiment exp =
			HeadlessSimulationLoader.newHeadlessSimulation("/tmp/src/model1.gaml");

		/*
		 * Thread.sleep(1000);
		 * System.out.println("Initialize experiment");
		 * GAMA.getExperiment().initialize(new ParametersSet(), 0.0);
		 * Thread.sleep(1000);
		 */
		System.out.println("Starting experiment");
		/*
		 * Runnable rnb = new Runnable(){ public void run() {GAMA.startOrPauseExperiment();}};
		 * Thread exec = new Thread(rnb);
		 * exec.start();
		 */
		// GAMA.startOrPauseExperiment();
		// GAMA.startOrPauseExperiment();
		// GAMA.getExperiment().startCurrentSimulation();
		// System.out.println("ccoucoe 3");
		//
		//
		//
		//
		Thread.sleep(2000);

		// GAMA.startOrPauseExperiment();
		//
		GAMA.getExperiment().step();
		System.out
			.println("Step experimentfdsqf*****************************************************************");
		GAMA.getExperiment().step();
		System.out
			.println("Step experimentfdsqf*****************************************************************");
		GAMA.getExperiment().step();
		System.out.println("Step experimentfdsqf");
		// System.out.println("ccoucoe 4");
		//
		// GAMA.getExperiment().step();
		//
		// System.out.println("coucou start end vsqdfdsqdf");
		//
		Thread.sleep(1000000);

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
