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
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gaml.interfaces.IGamlDescription;

/**
 * The Interface IDisplayCreator.
 */
@FunctionalInterface
public interface IDisplayCreator {

	/**
	 * The Class DisplayDescription.
	 */
	public static class DisplayDescription implements IDisplayCreator, IGamlDescription {

		/** The original. */
		private final IDisplayCreator delegate;

		/** The plugin. */
		private final String name, plugin;

		/** The documentation. */
		Doc documentation;

		/** The support. */
		private final Class<? extends IDisplaySurface> support;

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
			this.delegate = original;
			this.name = name;
			this.plugin = plugin;
			this.support = support;
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
		public String getTitle() { return "Display supported by " + getName() + ""; }

		/**
		 * Gets the doc annotation.
		 *
		 * @return the doc annotation
		 */
		public doc getDocAnnotation() {
			return support != null && support.isAnnotationPresent(doc.class) ? support.getAnnotation(doc.class) : null;
		}

		@Override
		public Doc getDocumentation() {
			if (documentation == null) {
				final doc d = getDocAnnotation();
				if (d == null) {
					documentation = EMPTY_DOC;
				} else {
					documentation = new RegularDoc(new StringBuilder(200));
					String s = d.value();
					if (s != null && !s.isEmpty()) { documentation.append(s).append("<br/>"); }
					usage[] usages = d.usages();
					for (usage u : usages) { documentation.append(u.value()).append("<br/>"); }
					s = d.deprecated();
					if (s != null && !s.isEmpty()) {
						documentation.append("<b>Deprecated</b>: ").append("<i>").append(s).append("</i><br/>");
					}
				}
			}
			return documentation;
		}

		/**
		 * Method getDefiningPlugin()
		 *
		 * @see msi.gaml.interfaces.IGamlDescription#getDefiningPlugin()
		 */
		@Override
		public String getDefiningPlugin() { return plugin; }

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
