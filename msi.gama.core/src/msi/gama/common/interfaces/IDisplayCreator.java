/*********************************************************************************************
 *
 *
 * 'IDisplayCreator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import java.util.Set;
import msi.gaml.descriptions.IGamlDescription;

public interface IDisplayCreator {

	public static class DisplayDescription implements IDisplayCreator, IGamlDescription {

		private final IDisplayCreator original;
		private final String name, plugin;

		public DisplayDescription(final IDisplayCreator original, final String name, final String plugin) {
			this.original = original;
			this.name = name;
			this.plugin = plugin;
		}

		/**
		 * Method create()
		 * @see msi.gama.common.interfaces.IDisplayCreator#create(java.lang.Object[])
		 */
		@Override
		public IDisplaySurface create(final Object ... args) {
			return original.create(args);
		}

		/**
		 * Method getName()
		 * @see msi.gama.common.interfaces.INamed#getName()
		 */
		@Override
		public String getName() {
			return name;
		}

		/**
		 * Method setName()
		 * @see msi.gama.common.interfaces.INamed#setName(java.lang.String)
		 */
		@Override
		public void setName(final String newName) {}

		/**
		 * Method serialize()
		 * @see msi.gama.common.interfaces.IGamlable#serialize(boolean)
		 */
		@Override
		public String serialize(final boolean includingBuiltIn) {
			return getName();
		}

		/**
		 * Method getTitle()
		 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
		 */
		@Override
		public String getTitle() {
			return "Display supported by " + getName() + " technology";
		}

		/**
		 * Method getDocumentation()
		 * @see msi.gaml.descriptions.IGamlDescription#getDocumentation()
		 */
		@Override
		public String getDocumentation() {
			return "";
		}

		/**
		 * Method getDefiningPlugin()
		 * @see msi.gaml.descriptions.IGamlDescription#getDefiningPlugin()
		 */
		@Override
		public String getDefiningPlugin() {
			return plugin;
		}

		/**
		 * Method collectPlugins()
		 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
		 */
		@Override
		public void collectPlugins(final Set<String> plugins) {
			plugins.add(plugin);
		}
	}

	IDisplaySurface create(Object ... args);

}
