package idees.gama.agents;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gaml.types.IType;


@species(name = "osm_traffic_signal")
@vars({ @var(name = "highway", type = IType.STRING), @var(name = "crossing", type = IType.STRING)})
public class OsmTrafficSignalAgent extends GamlAgent{

	public OsmTrafficSignalAgent(IPopulation s) {
		super(s);
	}

}
