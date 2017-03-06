/*********************************************************************************************
 *
 * 'IDisplayCreator.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.outputs.display.NullDisplaySurface;
import msi.gama.precompiler.GamlProperties;

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
		 * 
		 * @see msi.gama.common.interfaces.IDisplayCreator#create(java.lang.Object[])
		 */
		@Override
		public IDisplaySurface create(final Object... args) {
			if (original != null)
				return original.create(args);
			return new NullDisplaySurface();
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
			return "Display supported by " + getName() + " technology";
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

	IDisplaySurface create(Object... args);

}
