/*******************************************************************************************************
 *
 * AbstractProto.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gaml.compilation.GamlAddition;
import msi.gaml.interfaces.IGamlDescription;

/**
 * Class AbstractProto.
 *
 * @author drogoul
 * @since 17 déc. 2014
 *
 */
public abstract class AbstractProto extends GamlAddition implements IGamlDescription {

	/** The deprecated. */
	protected String deprecated;

	/**
	 * Instantiates a new abstract proto.
	 *
	 * @param name
	 *            the name
	 * @param support
	 *            the support
	 * @param plugin
	 *            the plugin
	 */
	protected AbstractProto(final String name, final AnnotatedElement support, final String plugin) {
		super(name, support, plugin);
	}

	/**
	 * Gets the deprecated.
	 *
	 * @return the deprecated
	 */
	public String getDeprecated() {
		if (deprecated != null) return deprecated.isEmpty() ? null : deprecated;
		final doc d = getDocAnnotation();
		if (d == null) return null;
		deprecated = d.deprecated();
		if (deprecated.isEmpty()) return null;
		return deprecated;
	}

	/**
	 * Gets the main doc.
	 *
	 * @return the main doc
	 */
	public String getMainDoc() {
		final doc d = getDocAnnotation();
		if (d == null) return null;
		final String s = d.value();
		if (s.isEmpty()) return null;
		return s;
	}

	/**
	 * Method getTitle()
	 *
	 * @see msi.gaml.interfaces.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() { return ""; }

	/**
	 * Gets the usages.
	 *
	 * @return the usages
	 */
	public Iterable<usage> getUsages() {
		final doc d = getDocAnnotation();
		if (d != null) {
			final usage[] tt = d.usages();
			if (tt.length > 0) return Arrays.asList(tt);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * @return
	 */
	public abstract int getKind();
}
