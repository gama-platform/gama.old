/*******************************************************************************************************
 *
 * PrimitiveDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.AccessibleObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gaml.compilation.IGamaHelper;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;

/**
 * The Class PrimitiveDescription.
 */
@SuppressWarnings ({ "rawtypes" })
public class PrimitiveDescription extends ActionDescription {

	/** The helper. */
	private IGamaHelper helper;

	/** The method. */
	private AccessibleObject method;

	/** The plugin. */
	private String plugin;

	/** The documentation. */
	private String documentation;

	/**
	 * Instantiates a new primitive description.
	 *
	 * @param superDesc
	 *            the super desc
	 * @param source
	 *            the source
	 * @param children
	 *            the children
	 * @param facets
	 *            the facets
	 * @param plugin
	 *            the plugin
	 */
	public PrimitiveDescription(final IDescription superDesc, final EObject source,
			final Iterable<IDescription> children, final Facets facets, final String plugin) {
		super(IKeyword.PRIMITIVE, superDesc, children, source, facets);
		this.plugin = plugin;
	}

	@Override
	public boolean visitOwnChildrenRecursively(final DescriptionVisitor visitor) {
		return true;
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		return true;
	}

	@Override
	public String getDefiningPlugin() { return plugin; }

	@Override
	public boolean validateChildren() {
		return true;
	}

	@Override
	public String getDocumentation() {
		if (documentation != null) return documentation;
		final doc d = getDocAnnotation();
		if (d == null) {
			documentation = "";
		} else if (d.deprecated().isEmpty()) {
			documentation = d.value() + Strings.LN;
		} else {
			documentation = d.value() + Strings.LN + Strings.LN + d.deprecated() + Strings.LN;
		}
		// Only arguments
		documentation = documentation + getArgDocumentation();
		return documentation;
	}

	@Override
	public String getArgDocumentation() {
		if (getArgNames().size() > 0) {
			final StringBuilder sb = new StringBuilder(200);
			Map<String, arg> argAnnotations = getArgs();
			final List<String> args = ImmutableList.copyOf(Iterables.transform(getFormalArgs(), desc -> {

				final StringBuilder sb1 = new StringBuilder(100);

				String name = desc.getName();
				sb1.append("<li><b>").append(Strings.TAB).append(name).append("</b>, type ").append(desc.getGamlType());
				if (desc.hasFacet(IKeyword.DEFAULT) && desc.getFacetExpr(IKeyword.DEFAULT) != null) {
					sb1.append(" <i>(default: ").append(desc.getFacetExpr(IKeyword.DEFAULT).serialize(false))
							.append(")</i>");
				}
				arg a = argAnnotations.get(name);
				if (a != null) {
					doc[] d = a.doc();
					if (d.length > 0) { sb1.append("; ").append(d[0].value()); }
				}
				sb1.append("</li>");

				return sb1.toString();
			}));
			sb.append("Arguments accepted: ").append("<br/><ul>");
			for (final String a : args) { sb.append(a); }
			sb.append("</ul><br/>");
			return sb.toString();
		}
		return "";
	}

	/**
	 * Gets the deprecated.
	 *
	 * @return the deprecated
	 */
	public String getDeprecated() {
		final doc d = getDocAnnotation();
		if (d == null) return null;
		String deprecated = d.deprecated();
		if (deprecated.isEmpty()) return null;
		return deprecated;
	}

	/**
	 * Gets the doc annotation.
	 *
	 * @return the doc annotation
	 */
	public doc getDocAnnotation() {
		doc d = null;
		if (method != null && method.isAnnotationPresent(doc.class)) {
			d = method.getAnnotation(doc.class);
		} else if (method != null && method.isAnnotationPresent(action.class)) {
			final doc[] docs = method.getAnnotation(action.class).doc();
			if (docs.length > 0) { d = docs[0]; }
		}
		return d;
	}

	/**
	 * Gets the args.
	 *
	 * @return the args
	 */
	public Map<String, arg> getArgs() {
		if (method == null || !method.isAnnotationPresent(action.class)) return Collections.EMPTY_MAP;
		action annot = method.getAnnotation(action.class);
		arg[] list = annot.args();
		if (list.length == 0) return Collections.EMPTY_MAP;
		Map<String, arg> result = new LinkedHashMap<>();
		for (arg a : list) { result.put(a.name(), a); }
		return result;
	}

	/**
	 * Gets the helper.
	 *
	 * @return the helper
	 */
	public IGamaHelper getHelper() { return helper; }

	@Override
	public PrimitiveDescription validate() {
		return this;
	}

	/**
	 * Sets the helper.
	 *
	 * @param helper
	 *            the helper
	 * @param method
	 *            the method
	 */
	public void setHelper(final IGamaHelper helper, final AccessibleObject method) {
		this.helper = helper;
		this.method = method;
	}

	@Override
	public PrimitiveDescription copy(final IDescription into) {
		final PrimitiveDescription desc = new PrimitiveDescription(into, element, children, getFacetsCopy(), plugin);
		desc.originName = getOriginName();
		desc.setHelper(helper, method);
		return desc;
	}

	/**
	 * @param plugin
	 *            name
	 */
	@Override
	public void setDefiningPlugin(final String plugin) { this.plugin = plugin; }

	@Override
	public void dispose() {
		enclosing = null;
	}
}
