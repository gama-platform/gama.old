package msi.gama.headless.core;

import msi.gama.kernel.simulation.GamlSimulation;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class HeadlessSimulation extends GamlSimulation {

	public HeadlessSimulation(final IPopulation exp) throws GamaRuntimeException {
		super(exp);
	}

	@Override
	protected void initSchedulingPolicy() {
		System.out.println("starting headless scheduler");
		this.scheduler = new HeadlessScheduler(this, experiment.getAgent());
	}

}
