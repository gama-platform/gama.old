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
