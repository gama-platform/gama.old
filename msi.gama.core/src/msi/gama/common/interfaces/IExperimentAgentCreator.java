/*********************************************************************************************
 *
 * 'IExperimentAgentCreator.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlProperties;

@FunctionalInterface
public interface IExperimentAgentCreator {

	public static class ExperimentAgentDescription implements IExperimentAgentCreator, IGamlDescription {

		private final IExperimentAgentCreator original;
		private final String name, plugin;

		public ExperimentAgentDescription(final IExperimentAgentCreator original, final String name,
				final String plugin) {
			this.original = original;
			this.name = name;
			this.plugin = plugin;
		}

		/**
		 * Method create()
		 * 
		 * @see msi.gama.common.interfaces.IExperimentAgentCreator#create(java.lang.Object[])
		 */
		@Override
		public IExperimentAgent create(final IPopulation<? extends IAgent> pop) {
			return original.create(pop);
		}

		/**
		 * Method getName()
		 * 
		 * @see msi.gama.common.interfaces.INamed#getName()
		 */
		@Override
		public String getName() {
			return name;
		}

		/**
		 * Method setName()
		 * 
		 * @see msi.gama.common.interfaces.INamed#setName(java.lang.String)
		 */
		@Override
		public void setName(final String newName) {}

		/**
		 * Method serialize()
		 * 
		 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
		 */
		@Override
		public String serialize(final boolean includingBuiltIn) {
			return getName();
		}

		/**
		 * Method getTitle()
		 * 
		 * @see msi.gama.common.interfaces.IGamlDescription#getTitle()
		 */
		@Override
		public String getTitle() {
			return "Experiment Agent supported by " + getName() + " technology";
		}

		/**
		 * Method getDocumentation()
		 * 
		 * @see msi.gama.common.interfaces.IGamlDescription#getDocumentation()
		 */
		@Override
		public String getDocumentation() {
			return "";
		}

		/**
		 * Method getDefiningPlugin()
		 * 
		 * @see msi.gama.common.interfaces.IGamlDescription#getDefiningPlugin()
		 */
		@Override
		public String getDefiningPlugin() {
			return plugin;
		}

		/**
		 * Method collectPlugins()
		 * 
		 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
		 */
		@Override
		public void collectMetaInformation(final GamlProperties meta) {
			meta.put(GamlProperties.PLUGINS, plugin);
		}
	}

	IExperimentAgent create(IPopulation<? extends IAgent> pop);

}