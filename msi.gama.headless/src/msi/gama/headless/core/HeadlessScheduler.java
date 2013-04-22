package msi.gama.headless.core;

import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class HeadlessScheduler extends AbstractScheduler {

	private final int nbStepToIterate;

	public HeadlessScheduler(final ISimulationAgent sim, final IAgent owner) {
		super(owner);
		this.nbStepToIterate = 1;
		// TODO Auto-generated constructor stub
	}

	private void doStep() {
		// IOutputManager m = GAMA.getExperiment().getOutputManager();
		try {
			IScheduler.SCHEDULER_AUTHORIZATION.acquire();

			// these initializing are needed. Why ???? (Nicolas M.)
			stepped = true;
			paused = false;
			alive = true;

			step(owner.getScope());
			// m.step(simulation.getExecutionScope());
			// m.updateOutputs();

			// HACK TO TEST

			// simulation.getModel().getModelEnvironment().getSpatialIndex().cleanCache();

			alive = true;
			paused = true;
			stepped = false;

			IScheduler.SCHEDULER_AUTHORIZATION.release();

		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			alive = false;
		} catch (InterruptedException e) {
			alive = false;
		}
	}

	@Override
	public void start() {
		for ( int i = 0; i < this.nbStepToIterate; i++ ) {
			stepByStep();
		}

	}

	@Override
	public void stepByStep() {
		doStep();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

}
