/*******************************************************************************************************
 *
 * GamlResourceDescription.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider;

/**
 * The class GamlResourceDescription.
 *
 * @author drogoul
 * @since 20 avr. 2012
 *
 */

public class GamlResourceDescription extends DefaultResourceDescription {

	/**
	 * Default constructor
	 *
	 * @param resource
	 * @param strategy
	 * @param cache
	 */
	final BuiltinGlobalScopeProvider provider;

	/**
	 * Instantiates a new gaml resource description.
	 *
	 * @param resource
	 *            the resource
	 * @param strategy
	 *            the strategy
	 * @param cache
	 *            the cache
	 * @param provider
	 *            default constructor
	 */
	@Inject
	public GamlResourceDescription(final GamlResource resource, final IDefaultResourceDescriptionStrategy strategy,
			final IResourceScopeCache cache, final BuiltinGlobalScopeProvider provider) {
		super(resource, strategy, cache);
		this.provider = provider;

	}

	@Override
	public Iterable<QualifiedName> getImportedNames() {
		final Iterable<QualifiedName> result = super.getImportedNames();
		return Iterables.filter(result, input -> !provider.contains(input));
	}

	@Override
	public GamlResource getResource() { 
		return (GamlResource) super.getResource();
	}

}
