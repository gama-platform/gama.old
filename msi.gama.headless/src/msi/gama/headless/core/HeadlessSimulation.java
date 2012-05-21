package msi.gama.headless.core;

import java.util.List;
import java.util.Map;

import msi.gama.kernel.experiment.IExperiment;
import msi.gama.kernel.simulation.AbstractSimulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;


public class HeadlessSimulation extends AbstractSimulation {

	public HeadlessSimulation(IExperiment exp) throws GamaRuntimeException {
		super(exp);
	}

	@Override
	protected void initializeWorldPopulation() {
		worldPopulation = new WorldPopulation(getModel().getWorldSpecies());
	}

	@Override
	protected void initializeWorld(final Map<String, Object> parameters)
		throws GamaRuntimeException, InterruptedException {
		IGamlPopulation g = (IGamlPopulation) getWorldPopulation();
		g.initializeFor(getGlobalScope());
		List<? extends IAgent> newAgents =
			g.createAgents(getGlobalScope(), 1, GamaList.with(parameters), false);
		IAgent world = newAgents.get(0);
		world.schedule();
		world.initializeMicroPopulations(getGlobalScope());
	}


}
