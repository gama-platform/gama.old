/*********************************************************************************************
 * 
 *
 * 'IAgentConstructor.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 20 ao�t 2010
 * 
 * @todo Description
 * 
 */
public interface IAgentConstructor {

	public abstract IAgent createOneAgent(IPopulation manager) throws GamaRuntimeException;

}
