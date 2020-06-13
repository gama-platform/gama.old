/*******************************************************************************************************
 *
 * msi.gaml.compilation.IAgentConstructor.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;

/**
 * Written by drogoul Modified on 20 ao�t 2010
 *
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
@FunctionalInterface
public interface IAgentConstructor<T extends IAgent> {

	public static class Minimal implements IAgentConstructor<MinimalAgent> {

		/**
		 * Method createOneAgent()
		 *
		 * @see msi.gaml.compilation.IAgentConstructor#createOneAgent(msi.gama.metamodel.population.IPopulation)
		 */

		@Override
		public MinimalAgent createOneAgent(final IPopulation manager, final int index) {
			return new MinimalAgent(manager, index);
		}

	}

	public static class Gaml implements IAgentConstructor<GamlAgent> {

		@Override
		public GamlAgent createOneAgent(final IPopulation manager, final int index) {
			return new GamlAgent(manager, index);
		}

	}

	Map<Class<? extends IAgent>, IAgentConstructor<? extends IAgent>> CONSTRUCTORS =
			new HashMap<Class<? extends IAgent>, IAgentConstructor<? extends IAgent>>() {

				{
					put(GamlAgent.class, new Gaml());
					put(MinimalAgent.class, new Minimal());
				}
			};

	<T extends IAgent> T createOneAgent(IPopulation<T> manager, int index);

}
