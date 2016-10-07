/*********************************************************************************************
 * 
 *
 * 'BaseGraphEdgeAgent.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.graph;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@species(name = "base_edge")
public class BaseGraphEdgeAgent extends AbstractGraphEdgeAgent {

	public BaseGraphEdgeAgent(final IPopulation<? extends IAgent> s) throws GamaRuntimeException {
		super(s);
	}

}