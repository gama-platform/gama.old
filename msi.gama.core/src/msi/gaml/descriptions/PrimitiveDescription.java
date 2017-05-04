/*********************************************************************************************
 *
 * 'PrimitiveDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.lang.reflect.AccessibleObject;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;

@SuppressWarnings ({ "rawtypes" })
public class PrimitiveDescription extends ActionDescription {

	private GamaHelper helper;
	private AccessibleObject method;
	private String plugin;

	public PrimitiveDescription(final IDescription superDesc, final EObject source,
			final Iterable<IDescription> children, final Facets facets, final String plugin) {
		super(IKeyword.PRIMITIVE, superDesc, children, source, facets);
		this.plugin = plugin;
	}

	@Override
	public String getDefiningPlugin() {
		return plugin;
	}

	@Override
	public boolean validateChildren() {
		return true;
	}

	@Override
	public String getDocumentation() {
		String documentation;
		final doc d = getDocAnnotation();
		if (d == null) {
			documentation = "";
		} else {
			if (d.deprecated().isEmpty())
				documentation = d.value() + Strings.LN;
			else
				documentation = d.deprecated() + Strings.LN;
		}
		// Only arguments
		return documentation + super.getArgDocumentation();
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

	// @Override
	// public String getShortDescription() {
	// final doc d = getDocAnnotation();
	// final String doc = d == null ? null : d.value();
	// String s = super.getShortDescription();
	// if (getEnclosingDescription() != null && (getEnclosingDescription().redefinesAction(getName()) || isBuiltIn())
	// && doc != null && !doc.isEmpty()) {
	// s += ": " + doc + "<br/>";
	// }
	// return s;
	//
	// }

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
		enclosing = null;
	}
}
