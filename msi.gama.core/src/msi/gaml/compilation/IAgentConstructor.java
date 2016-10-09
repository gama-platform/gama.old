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

import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 20 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public interface IAgentConstructor<T extends IAgent> {

	public static class Minimal implements IAgentConstructor<MinimalAgent> {

		/**
		 * Method createOneAgent()
		 * 
		 * @see msi.gaml.compilation.IAgentConstructor#createOneAgent(msi.gama.metamodel.population.IPopulation)
		 */

		@Override
		public MinimalAgent createOneAgent(final IPopulation manager) throws GamaRuntimeException {
			return new MinimalAgent(manager);
		}

	}

	public static class Gaml implements IAgentConstructor<GamlAgent> {

		@Override
		public GamlAgent createOneAgent(final IPopulation manager) throws GamaRuntimeException {
			return new GamlAgent(manager);
		}

	}

	public static Map<Class<? extends IAgent>, IAgentConstructor<? extends IAgent>> CONSTRUCTORS = new HashMap<Class<? extends IAgent>, IAgentConstructor<? extends IAgent>>() {

		{
			put(GamlAgent.class, new Gaml());
			put(MinimalAgent.class, new Minimal());
		}
	};

	public <T extends IAgent> T createOneAgent(IPopulation<T> manager) throws GamaRuntimeException;

}
