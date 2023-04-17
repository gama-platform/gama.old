/*******************************************************************************************************
 *
 * GamlResourceDescriptionManager.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import static msi.gama.lang.gaml.indexer.GamlResourceIndexer.isImported;
import static msi.gama.lang.gaml.resource.GamlResourceServices.properlyEncodedURI;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;
import org.eclipse.xtext.util.OnChangeEvictingCache;

import com.google.inject.Inject;

import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The class GamlResourceDescriptionManager.
 *
 * @author drogoul
 * @since 20 avr. 2012
 *
 */
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager
		implements IResourceDescription.Manager.AllChangeAware {

	static {
		DEBUG.OFF();
	}

	@Override
	public IResourceDescription getResourceDescription(final Resource resource) {
		IResourceDescription r = super.getResourceDescription(resource);
		if (resource instanceof GamlResource gr) {
			if (r instanceof GamlResourceDescription) return r;
			OnChangeEvictingCache cache = (OnChangeEvictingCache) super.getCache();
			cache.clear(resource);
			// DEBUG.OUT("Removing old resource description for: " + resource.getURI().lastSegment());
			return super.getResourceDescription(resource);
		}
		return r;
	}

	/** The provider. */
	@Inject BuiltinGlobalScopeProvider provider;

	@Override
	protected IResourceDescription internalGetResourceDescription(final Resource resource,
			final IDefaultResourceDescriptionStrategy strategy) {
		return new GamlResourceDescription((GamlResource) resource, strategy, getCache(), provider);
	}

	@Override
	public boolean isAffected(final Collection<Delta> deltas, final IResourceDescription candidate,
			final IResourceDescriptions context) {
		URI candidateURI = properlyEncodedURI(candidate.getURI());
		Map<URI, String> imports = GamlResourceIndexer.allImportsOfProperlyEncoded(candidateURI);
		if (imports.isEmpty()) return false;
		for (Delta d : deltas) {
			URI uri = properlyEncodedURI(d.getUri());
			if (isImported(uri, candidateURI) || imports.containsKey(uri)) return true;
		}
		return super.isAffected(deltas, candidate, context);
	}

	@Override
	public boolean isAffectedByAny(final Collection<Delta> deltas, final IResourceDescription candidate,
			final IResourceDescriptions context) throws IllegalArgumentException {
		return isAffected(deltas, candidate, context);
	}
}
