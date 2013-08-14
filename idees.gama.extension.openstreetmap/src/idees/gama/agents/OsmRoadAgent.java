package idees.gama.agents;

import java.util.List;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;


@species(name = "osm_road")
@vars({ @var(name = "highway", type = IType.STRING), @var(name = "lanes", type = IType.INT),
	@var(name = "motorroad", type = IType.BOOL), @var(name = "oneway", type = IType.BOOL),@var(name = "maxspeed", type = IType.FLOAT), @var(name = "agents_on", type = IType.LIST, of = IType.LIST)})
public class OsmRoadAgent extends GamlAgent{

	public OsmRoadAgent(IPopulation s) {
		super(s);
	}

}
