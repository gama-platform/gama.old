package msi.gama.kernel.experiment;

import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.*;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaGeometryType;

@msi.gama.precompiler.GamlAnnotations.species(name = "experimentator")
public class ExperimentatorAgent extends GamlAgent {

	ISimulation simulation;
	IExperiment experiment;

	private static final IShape SHAPE = GamaGeometryType.createPoint(new GamaPoint(-1, -1));

	public ExperimentatorAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
		super.setGeometry(SHAPE);
		index = 0;
	}

	@Override
	public ISimulation getSimulation() {
		return simulation;
	}

	@Override
	public IScheduler getScheduler() {
		return simulation.getScheduler();
	}

	@Override
	public IModel getModel() {
		return simulation.getModel();
	}

	@Override
	public IExperiment getExperiment() {
		return experiment;
	}

	@Override
	public synchronized void setLocation(final ILocation newGlobalLoc) {}

	@Override
	public synchronized void setGeometry(final IShape newGlobalGeometry) {}

	@Override
	public void step(final IScope scope) {
		experiment.getOutputManager().step(scope);
		experiment.getOutputManager().updateOutputs();
		super.step(scope);
	}

	public void setExperiment(final IExperiment exp) {
		experiment = exp;
		if ( simulation == null && experiment != null ) {
			simulation = experiment.createSimulation();
		}
	}

	public void addMicroPopulation(final WorldPopulation pop) {
		microPopulations.put(pop.getSpecies(), pop);
		pop.setHost(this);
	}

}