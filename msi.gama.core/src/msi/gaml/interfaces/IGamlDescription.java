/*******************************************************************************************************
 *
 * IGamlDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.interfaces;

import java.util.LinkedHashMap;
import java.util.Map;

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
		 * Gets the string value of the documentation. Never null.
		 *
		 * @return the string
		 */
		String get();

		/**
		 * Gets the string value of the documentation of the sub-element corresponding to the key.
		 *
		 * @param key
		 *            the key
		 * @return the string
		 */
		default Doc get(final String key) {
			return EMPTY_DOC;
		}

		/**
		 * Append a string to the current string value of the documentation.
		 *
		 * @param string
		 *            the string
		 */
		default Doc append(final String string) {
			return this;
		}

		/**
		 * Prepend a string to the current string value of the documentation.
		 *
		 * @param string
		 *            the string
		 * @return the doc
		 */
		default Doc prepend(final String string) {
			return this;
		}

		/**
		 * Adds a subdocumentation at the specific key.
		 *
		 * @param key
		 *            the key
		 * @param doc
		 *            the doc
		 */
		default void set(final String header, final String key, final Doc doc) {}

	}

	/**
	 * Constant documentation that cannot change once instantiated
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
	 * A documentation built around a StringBuilder, allowing to append and prepend elements
	 */
	class RegularDoc implements Doc {

		/** The builder. */
		final StringBuilder builder;

		/** The subdocs. */
		final Map<String, Map<String, Doc>> subdocs = new LinkedHashMap<>();

		/**
		 * Instantiates a new regular doc.
		 *
		 * @param sb
		 *            the sb
		 */
		public RegularDoc(final CharSequence sb) {
			builder = new StringBuilder(sb);
		}

		/**
		 * Instantiates a new regular doc.
		 */
		public RegularDoc() {
			this("");
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
			if (subdocs.isEmpty()) return builder.toString();
			StringBuilder sb = new StringBuilder(builder.toString());
			for (String header : subdocs.keySet()) {
				sb.append("<hr/>").append(header).append("<br/><ul>");
				subdocs.get(header).forEach((name, doc) -> {
					sb.append("<li><b>").append(name).append("</b>: ").append(doc.toString());
				});
				sb.append("</ul><br/>");
			}
			return sb.toString();
		}

		@Override
		public String toString() {
			return get();
		}

		/**
		 * Sets a sub-documentation
		 *
		 * @param key
		 *            the key
		 * @param doc
		 *            the doc
		 */
		@Override
		public void set(final String header, final String key, final Doc doc) {
			Map<String, Doc> category = subdocs.get(header);
			if (category == null) {
				category = new LinkedHashMap<>();
				subdocs.put(header, category);
			}
			category.put(key, doc);
		}

		@Override
		public Doc get(final String key) {
			for (String s : subdocs.keySet()) {
				Doc doc = subdocs.get(s).get(key);
				if (doc != null) return doc;
			}
			return EMPTY_DOC;
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
	 * Returns the documentation attached to this object. Never null. Default is an empty documentation
	 *
	 * @return a string that represents the documentation of this object
	 */
	default Doc getDocumentation() { return EMPTY_DOC; }

	/**
	 * Returns the plugin in which this object has been defined (if it has one)
	 *
	 * @return a string containing the identifier of the plugin in which this object has been defined, or null. Default
	 *         is null
	 */
	default String getDefiningPlugin() { return null; }

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
