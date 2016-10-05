package ummisco.gama.serializer.gamaType.reduced;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.TOrderedHashMap;

public class GamaSimulationAgentReducer {
	// new2_model_model0 shape -- value : POLYGON ((0 100, 0 0, 100 0, 100 100,
	// 0 100)) at location[50.0;50.0;0.0]
	// new2_model_model0 name -- value : new2_model_model0
	// new2_model_model0 peers -- value : []
	// new2_model_model0 host -- value : toto0
	// new2_model_model0 location -- value : location[50.0;50.0;0.0]
	// new2_model_model0 members -- value :
	// msi.gama.metamodel.population.MetaPopulation@75a97f53
	// new2_model_model0 agents -- value : [my_species0, my_species1, people0]
	// new2_model_model0 seed -- value : 0.97700297461628
	// new2_model_model0 rng -- value : mersenne
	// new2_model_model0 step -- value : 1.0
	// new2_model_model0 time -- value : 0.0
	// new2_model_model0 cycle -- value : 0
	// new2_model_model0 duration -- value : 0
	// new2_model_model0 total_duration -- value : 0
	// new2_model_model0 average_duration -- value : 0.0
	// new2_model_model0 machine_time -- value : 1.453305042117E12
	// new2_model_model0 current_date -- value : null
	// new2_model_model0 starting_date -- value : null
	////////
	// new2_model_model0 toto -- value : null
	// new2_model_model0 a -- value : 0.0
	// new2_model_model0 my_species -- value : Population of my_species
	// new2_model_model0 people -- value : Population of people

	String name;
	String speciesName;
	IShape geometry;
	Map<Object, Object> attributes;
	Double seed;
	int cycle;
	double step;

	List<RemoteAgent> agents;

	public GamaSimulationAgentReducer() {
		attributes = new TOrderedHashMap<>();
		agents = new ArrayList<RemoteAgent>();
	}

	public GamaSimulationAgentReducer(final SimulationAgent agt) {
		this.name = agt.getName();
		speciesName = agt.getSpecies().getName();
		this.geometry = agt.getGeometry();
		seed = agt.getSeed();
		// TODO : to complete
		step = agt.getClock().getStep();
		cycle = agt.getClock().getCycle();

		// this.attributes = agt.getAttributes();
		agents = new ArrayList<RemoteAgent>();

		for (final IAgent agent : agt.getAgents(agt.getScope())) {
			agents.add(new RemoteAgent((GamlAgent) agent));
		}
	}

}
