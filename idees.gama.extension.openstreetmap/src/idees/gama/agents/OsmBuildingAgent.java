package idees.gama.agents;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;


@species(name = "osm_building")
@vars({ @var(name = "building", type = IType.STRING), @var(name = "barrier", type = IType.STRING),@var(name = "shop", type = IType.STRING),
	@var(name = "height", type = IType.FLOAT),@var(name = "building:levels", type = IType.INT),@var(name = "wall", type = IType.BOOL),@var(name = "bridge", type = IType.BOOL)})
public class OsmBuildingAgent extends GamlAgent{

	public OsmBuildingAgent(IPopulation s) {
		super(s);
	}

}
