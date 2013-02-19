package msi.gama.metamodel.agent;

import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

@species(name = "graph_node")
public class GraphAgent extends GamlAgent {

	public GraphAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

	@action(name = "distance_to", virtual = true, args = { @arg(name = "other", optional = false, type = { IType.AGENT_STR }) })
	public Integer distanceTo(final IScope scope) {
		return 0;
	}
}
