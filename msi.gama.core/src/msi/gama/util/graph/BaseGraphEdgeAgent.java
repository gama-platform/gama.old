package msi.gama.util.graph;

import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@species(name = "base_edge")
public class BaseGraphEdgeAgent extends AbstractGraphEdgeAgent {

	public BaseGraphEdgeAgent(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

}