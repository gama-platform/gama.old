/*********************************************************************************************
 * 
 *
 * 'FakeApplication.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.headless.runtime;

import msi.gama.headless.job.ExperimentJob;

public class FakeApplication extends Thread {// implements Runnable {

	private ExperimentJob si = null;
	private SimulationRuntime runtime = null;

	public FakeApplication(final ExperimentJob sim, final SimulationRuntime rn) {
		si = sim;
		this.runtime= rn;
	}

	
	@Override
	public void run() {
		try {
			si.loadAndBuild();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		si.play();
		this.runtime.closeSimulation(this);
	}

}
