/*******************************************************************************************************
 *
 * IGamlDescription.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gama.precompiler.GamlProperties;

/**
 * The interface IGamlDescription. Represents objects that can be presented in the online documentation.
 *
 * @author drogoul
 * @since 27 avr. 2012
 *
 */
public interface IGamlDescription extends INamed {

	/**
	 * The Interface Doc. A simple interface that allows retrieving the documentation of a description, either directly
	 * or using a key. Used for actions and their arguments
	 */
	public interface Doc {

		/**
		 * Gets the.
		 *
		 * @return the string
		 */
		String get();

		/**
		 * Gets the.
		 *
		 * @param key
		 *            the key
		 * @return the string
		 */
		default String get(final String key) {
			return get();
		}

		/**
		 * Append.
		 *
		 * @param string
		 *            the string
		 */
		default Doc append(final String string) {
			// Nothing by default;
			return this;
		}

		/**
		 * Prepend.
		 *
		 * @param string
		 *            the string
		 * @return the doc
		 */
		default Doc prepend(final String string) {
			return this;
		}
	}

	/**
	 * The ConstantDoc.
	 */
	record ConstantDoc(String value) implements Doc {

		@Override
		public String get() {
			return value;
		}

		@Override
		public String toString() {
			return value;
		}

	}

	/**
	 * The Class RegularDoc.
	 */
	class RegularDoc implements Doc {

		/** The builder. */
		final StringBuilder builder;

		/**
		 * Instantiates a new regular doc.
		 *
		 * @param sb
		 *            the sb
		 */
		public RegularDoc(final StringBuilder sb) {
			builder = sb;
		}

		/**
		 * Instantiates a new regular doc.
		 *
		 * @param value
		 *            the value
		 */
		public RegularDoc(final String value) {
			builder = new StringBuilder(200);
			append(value);
		}

		@Override
		public Doc append(final String string) {
			builder.append(string);
			return this;
		}

		@Override
		public Doc prepend(final String string) {
			builder.insert(0, string);
			return this;
		}

		@Override
		public String get() {
			return builder.toString();
		}

		@Override
		public String toString() {
			return get();
		}

	}

	/** The empty. */
	Doc EMPTY_DOC = () -> "";

	/**
	 * Returns the title of this object (ie. the first line in the online documentation)
	 *
	 * @return a string representing the title of this object (default is its name)
	 */
	default String getTitle() { return getName(); }

	/**
	 * Returns the documentation attached to this object. Never null
	 *
	 * @return a string that represents the documentation of this object
	 */
	default Doc getDocumentation() { return EMPTY_DOC; }

	/**
	 * Returns the plugin in which this object has been defined (if it has one)
	 *
	 * @return a string containing the identifier of the plugin in which this object has been defined, or null
	 */
	default String getDefiningPlugin() {
		// Null by default
		return null;
	}

	/**
	 * Collect meta information.
	 *
	 * @param meta
	 *            the meta
	 */
	default void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.PLUGINS, getDefiningPlugin());
	}

}
