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

import msi.gama.headless.core.Simulation;

public class FakeApplication extends Thread {// implements Runnable {

	// public Thread myT;
	private Simulation si = null;

	public FakeApplication(final Simulation sim) {
		si = sim;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// for (int i = 0; i < 8; i++)
		// si.nextStepDone();

		si.play();
	}

}
