package msi.gama.headless.core;

import msi.gama.kernel.experiment.IExperiment;
import msi.gama.kernel.simulation.AbstractSimulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class HeadlessSimulation extends AbstractSimulation {

	public HeadlessSimulation(final IExperiment exp) throws GamaRuntimeException {
		super(exp);
	}

	@Override
	protected void initSchedulingPolicy() {
		System.out.println("starting headless scheduler");
		this.scheduler = new HeadlessScheduler(this, experiment.getAgent());
	}

}
