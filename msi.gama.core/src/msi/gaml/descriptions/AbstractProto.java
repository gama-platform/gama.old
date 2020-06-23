/*******************************************************************************************************
 *
 * msi.gaml.descriptions.AbstractProto.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;

import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;

/**
 * Class AbstractProto.
 *
 * @author drogoul
 * @since 17 déc. 2014
 *
 */
public abstract class AbstractProto implements IGamlDescription {

	protected String name;
	protected String plugin;
	protected AnnotatedElement support;
	protected String deprecated;

	protected AbstractProto(final String name, final AnnotatedElement support, final String plugin) {
		this.name = name;
		this.plugin = plugin;
		this.support = support;
	}

	@Override
	public String getDocumentation() {
		final doc d = getDocAnnotation();
		if (d == null) { return ""; }
		final StringBuilder sb = new StringBuilder(200);
		String s = d.value();
		if (s != null && !s.isEmpty()) {
			sb.append(s);
			sb.append("<br/>");
		}
		s = d.deprecated();
		if (s != null && !s.isEmpty()) {
			sb.append("<b>Deprecated</b>: ");
			sb.append("<i>");
			sb.append(s);
			sb.append("</i><br/>");
		}

		return sb.toString();
	}

	public String getDeprecated() {
		if (deprecated != null) { return deprecated.isEmpty() ? null : deprecated; }
		final doc d = getDocAnnotation();
		if (d == null) { return null; }
		deprecated = d.deprecated();
		if (deprecated.isEmpty()) { return null; }
		return deprecated;
	}

	public String getMainDoc() {
		final doc d = getDocAnnotation();
		if (d == null) { return null; }
		final String s = d.value();
		if (s.isEmpty()) { return null; }
		return s;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Method getTitle()
	 * 
	 * @see msi.gama.common.interfaces.IGamlDescription#getTitle()
	 */
	@Override
	public String getTitle() {
		return "";
	}

	/**
	 * Method setName()
	 * 
	 * @see msi.gama.common.interfaces.INamed#setName(java.lang.String)
	 */
	@Override
	public void setName(final String newName) {}

	public Iterable<usage> getUsages() {
		final doc d = getDocAnnotation();
		if (d != null) {
			final usage[] tt = d.usages();
			if (tt.length > 0) { return Arrays.asList(tt); }
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}
	//
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// meta.put(GamlProperties.PLUGINS, plugin);
	// }

	public AnnotatedElement getSupport() {
		return support;
	}

	public void setSupport(final AnnotatedElement support) {
		this.support = support;
	}

	public doc getDocAnnotation() {
		doc d = null;
		if (support != null && support.isAnnotationPresent(doc.class)) {
			d = support.getAnnotation(doc.class);
		}
		return d;
	}

	/**
	 * @return
	 */
	public abstract int getKind();
}
