package idees.gama.agents;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;

@species(name = "osm_node")
@vars({ @var(name = "roads_in", type = IType.LIST, of = IType.AGENT), @var(name = "roads_out", type = IType.LIST, of = IType.AGENT), @var(name = "signal", type = IType.AGENT)})
public class OsmNodeAgent extends GamlAgent{

	public OsmNodeAgent(IPopulation s) {
		super(s);
	}

}
