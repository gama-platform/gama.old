package msi.gama.metamodel.agent;

import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@species(name = "base_edge")
public class BaseGraphEdge extends AbstractGraphEdge {

	public BaseGraphEdge(final IPopulation s) throws GamaRuntimeException {
		super(s);
	}

}