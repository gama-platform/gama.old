/*******************************************************************************************************
 *
 * AbstractProto.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
import msi.gaml.interfaces.IGamlDescription;

/**
 * Class AbstractProto.
 *
 * @author drogoul
 * @since 17 déc. 2014
 *
 */
public abstract class AbstractProto implements IGamlDescription {

	/** The name. */
	protected String name;

	/** The plugin. */
	protected String plugin;

	/** The support. */
	protected AnnotatedElement support;

	/** The deprecated. */
	protected String deprecated;

	/** The documentation. */
	protected Doc documentation;

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
		this.name = name;
		this.plugin = plugin;
		this.support = support;
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

	@Override
	public String getName() { return name; }

	/**
	 * Method getTitle()
	 *
	 * @see msi.gaml.interfaces.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() { return ""; }

	/**
	 * Method setName()
	 *
	 * @see msi.gaml.interfaces.INamed#setName(java.lang.String)
	 */
	@Override
	public void setName(final String newName) {}

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

	@Override
	public String getDefiningPlugin() { return plugin; }

	/**
	 * Gets the support.
	 *
	 * @return the support
	 */
	public AnnotatedElement getSupport() { return support; }

	/**
	 * Sets the support.
	 *
	 * @param support
	 *            the new support
	 */
	public void setSupport(final AnnotatedElement support) { this.support = support; }

	/**
	 * Gets the doc annotation.
	 *
	 * @return the doc annotation
	 */
	public doc getDocAnnotation() {
		return support != null && support.isAnnotationPresent(doc.class) ? support.getAnnotation(doc.class) : null;
	}

	/**
	 * @return
	 */
	public abstract int getKind();
}
