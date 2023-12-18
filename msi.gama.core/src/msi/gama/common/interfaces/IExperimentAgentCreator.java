/*******************************************************************************************************
 *
 * IExperimentAgentCreator.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gaml.interfaces.IGamlDescription;

/**
 * The Interface IExperimentAgentCreator.
 */
@FunctionalInterface
public interface IExperimentAgentCreator {

	/**
	 * The Class ExperimentAgentDescription.
	 */
	public static class ExperimentAgentDescription implements IExperimentAgentCreator, IGamlDescription {

		/** The original. */
		private final IExperimentAgentCreator original;

		/** The plugin. */
		private final String name, plugin;

		/** The clazz. */
		private final Class<? extends IExperimentAgent> clazz;

		/**
		 * Instantiates a new experiment agent description.
		 *
		 * @param original
		 *            the original
		 * @param name
		 *            the name
		 * @param plugin
		 *            the plugin
		 */
		public ExperimentAgentDescription(final IExperimentAgentCreator original,
				final Class<? extends IExperimentAgent> clazz, final String name, final String plugin) {
			this.clazz = clazz;
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
		public IExperimentAgent create(final IPopulation<? extends IAgent> pop, final int index) {
			return original.create(pop, index);
		}

		/**
		 * Method getName()
		 *
		 * @see msi.gaml.interfaces.INamed#getName()
		 */
		@Override
		public String getName() { return name; }

		/**
		 * Method setName()
		 *
		 * @see msi.gaml.interfaces.INamed#setName(java.lang.String)
		 */
		@Override
		public void setName(final String newName) {}

		/**
		 * Method serialize()
		 *
		 * @see msi.gaml.interfaces.IGamlable#serializeToGaml(boolean)
		 */
		@Override
		public String serializeToGaml(final boolean includingBuiltIn) {
			return getName();
		}

		/**
		 * Method getTitle()
		 *
		 * @see msi.gaml.interfaces.IGamlDescription#getTitle()
		 */
		@Override
		public String getTitle() { return "Experiment Agent supported by " + getName() + " technology"; }

		/**
		 * Method getDefiningPlugin()
		 *
		 * @see msi.gaml.interfaces.IGamlDescription#getDefiningPlugin()
		 */
		@Override
		public String getDefiningPlugin() { return plugin; }

		@Override
		public Class<? extends IExperimentAgent> getJavaBase() { return clazz; }

	}

	/**
	 * Creates the.
	 *
	 * @param pop
	 *            the pop
	 * @param index
	 *            the index
	 * @return the i experiment agent
	 */
	IExperimentAgent create(IPopulation<? extends IAgent> pop, int index);

	/**
	 * Gets the java base.
	 *
	 * @return the java base
	 */
	default Class<? extends IExperimentAgent> getJavaBase() { return ExperimentAgent.class; }

}