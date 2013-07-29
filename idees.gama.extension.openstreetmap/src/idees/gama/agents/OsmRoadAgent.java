package idees.gama.agents;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;


@species(name = "osm_road")
@vars({ @var(name = "highway", type = IType.STRING), @var(name = "lanes", type = IType.INT), 
	@var(name = "motorroad", type = IType.BOOL)})
public class OsmRoadAgent extends GamlAgent{

	public OsmRoadAgent(IPopulation s) {
		super(s);
	}

}
