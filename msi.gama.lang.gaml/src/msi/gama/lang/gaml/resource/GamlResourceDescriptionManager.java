/*******************************************************************************************************
 *
 * GamlResourceDescriptionManager.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

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
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager {
	/*
	 * To allow resources to strore bin files : extends StorageAwareResourceDescriptionManager / To listen to all deltas
	 * (even w/o) changes: implements IResourceDescription.Manager.AllChangeAware
	 */

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

	/**
	 * Internal get resource description.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param resource
	 *            the resource
	 * @param strategy
	 *            the strategy
	 * @return the i resource description
	 * @date 13 janv. 2024
	 */
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
			if (/* isImported(uri, candidateURI) || */ imports.containsKey(uri)) return true;
		}
		return false;
		// return super.isAffected(deltas, candidate, context);
	}

	/**
	 * Checks if is affected by any.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param deltas
	 *            the deltas
	 * @param candidate
	 *            the candidate
	 * @param context
	 *            the context
	 * @return true, if is affected by any
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @date 13 janv. 2024
	 */
	// @Override
	// public boolean isAffectedByAny(final Collection<Delta> deltas, final IResourceDescription candidate,
	// final IResourceDescriptions context) throws IllegalArgumentException {
	// return isAffected(deltas, candidate, context);
	// }
}
