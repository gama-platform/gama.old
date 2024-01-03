/*******************************************************************************************************
 *
 * IDisplayCreator.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.display.NullDisplaySurface;
import msi.gaml.compilation.GamlAddition;

/**
 * The Interface IDisplayCreator.
 */
@FunctionalInterface
public interface IDisplayCreator {

	/**
	 * The Class DisplayDescription.
	 */
	public static class DisplayDescription extends GamlAddition implements IDisplayCreator {

		/** The original. */
		private final IDisplayCreator delegate;

		/**
		 * Instantiates a new display description.
		 *
		 * @param original
		 *            the original
		 * @param name
		 *            the name
		 * @param plugin
		 *            the plugin
		 */
		public DisplayDescription(final IDisplayCreator original, final Class<? extends IDisplaySurface> support,
				final String name, final String plugin) {
			super(name, support, plugin);
			this.delegate = original;
		}

		/**
		 * Method create()
		 *
		 * @see msi.gama.common.interfaces.IDisplayCreator#create(java.lang.Object[])
		 */
		@Override
		public IDisplaySurface create(final Object... args) {
			if (delegate != null) return delegate.create(args);
			return new NullDisplaySurface();
		}

		/**
		 * Creates the.
		 *
		 * @param output
		 *            the output
		 * @param args
		 *            the args
		 * @return the i display surface
		 */
		public IDisplaySurface create(final IDisplayOutput output, final Object... args) {
			final Object[] params = new Object[args.length + 1];
			params[0] = output;
			for (int i = 0; i < args.length; i++) { params[i + 1] = args[i]; }
			return create(params);
		}

		/**
		 * Method getTitle()
		 *
		 * @see msi.gaml.interfaces.IGamlDescription#getTitle()
		 */
		@Override
		public String getTitle() { return "Display supported by " + getName() + ""; }

	}

	/**
	 * Creates the.
	 *
	 * @param args
	 *            the args
	 * @return the i display surface
	 */
	IDisplaySurface create(Object... args);

}
