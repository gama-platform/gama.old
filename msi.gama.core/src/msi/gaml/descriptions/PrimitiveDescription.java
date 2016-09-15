/*********************************************************************************************
 *
 *
 * 'PrimitiveDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.AccessibleObject;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;

public class PrimitiveDescription extends StatementDescription {

	private GamaHelper helper;
	private AccessibleObject method;
	private String plugin;

	public PrimitiveDescription(final IDescription superDesc, final EObject source, final ChildrenProvider children,
			final Facets facets, final String plugin) {
		super(IKeyword.PRIMITIVE, superDesc, children, true, source, facets);
		this.plugin = plugin;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	@Override
	public String getDocumentation() {
		final doc d = getDocAnnotation();
		if (d == null) {
			return "";
		}
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

		if (getArgNames().size() > 0) {
			final List<String> args = ImmutableList
					.copyOf(Iterables.transform(getArgs(), new Function<IDescription, String>() {

						@Override
						public String apply(final IDescription desc) {
							final StringBuilder sb = new StringBuilder(100);
							sb.append("<li><b>").append(Strings.TAB).append(desc.getName()).append("</b> of type ")
									.append(desc.getType());
							if (desc.hasFacet(IKeyword.DEFAULT)) {
								sb.append(" <i>(default: ").append(desc.getFacetExpr(IKeyword.DEFAULT).serialize(false))
										.append(")</i>");
							}
							sb.append("</li>").append(Strings.LN);

							return sb.toString();
						}
					}));
			sb.append("Arguments accepted : ").append("<br/><ul>").append(Strings.LN);
			for (final String a : args) {
				sb.append(a);
			}
			sb.append("</ul><br/>");
		}

		return sb.toString();
	}

	public doc getDocAnnotation() {
		doc d = null;
		if (method != null && method.isAnnotationPresent(doc.class)) {
			d = method.getAnnotation(doc.class);
		} else {
			if (method != null && method.isAnnotationPresent(action.class)) {
				final doc[] docs = method.getAnnotation(action.class).doc();
				if (docs.length > 0)
					d = docs[0];
			}
		}
		return d;
	}

	public GamaHelper getHelper() {
		return helper;
	}

	@Override
	public PrimitiveDescription validate() {
		return this;
	}

	public void setHelper(final GamaHelper helper, final AccessibleObject method) {
		this.helper = helper;
		this.method = method;
	}

	@Override
	public PrimitiveDescription copy(final IDescription into) {
		final PrimitiveDescription desc = new PrimitiveDescription(into, element,
				args != null ? new ChildrenProvider(args.values()) : ChildrenProvider.NONE, getFacetsCopy(), plugin);
		desc.originName = getOriginName();
		desc.setHelper(helper, method);
		return desc;
	}

	/**
	 * @param plugin
	 *            name
	 */
	@Override
	public void setDefiningPlugin(final String plugin) {
		this.plugin = plugin;
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.PLUGINS, plugin);
		meta.put(GamlProperties.ACTIONS, getName());
	}

	@Override
	public void dispose() {
		previousDescription = null;
		enclosing = null;
		denotedSpecies = null;
	}
}
