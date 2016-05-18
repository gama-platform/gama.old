package msi.gama.common.util;

import java.util.Map;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IExperimentAgentCreator;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.population.IPopulation;

public abstract class ExperimentManager {

	public static final Map<String, IExperimentAgentCreator> EXPERIMENTS = new THashMap<>();

	// TODO : remove (ExperimentPlan)
	public static ExperimentAgent createExperimentAgent(String name, IPopulation pop) {
		return (ExperimentAgent) EXPERIMENTS.get(name).create(pop);
	}
}
