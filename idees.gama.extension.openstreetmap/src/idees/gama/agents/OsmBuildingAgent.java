package idees.gama.agents;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;


@species(name = "osm_building")
@vars({ @var(name = "building", type = IType.STRING)})
public class OsmBuildingAgent extends GamlAgent{

	public OsmBuildingAgent(IPopulation s) {
		super(s);
	}

}
