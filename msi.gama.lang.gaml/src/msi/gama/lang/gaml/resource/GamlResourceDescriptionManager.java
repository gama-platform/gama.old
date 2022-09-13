/*******************************************************************************************************
 *
 * GamlResourceDescriptionManager.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

import com.google.inject.Inject;

import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.scoping.BuiltinGlobalScopeProvider;

/**
 * The class GamlResourceDescriptionManager.
 *
 * @author drogoul
 * @since 20 avr. 2012
 *
 */
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager
		implements IResourceDescription.Manager.AllChangeAware {

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
		Map<URI, String> imports;
		if (!(candidate instanceof GamlResourceDescription)) {
			// Seems to happen, although it shouldnt !
			imports = GamlResourceIndexer.allImportsOf(candidate.getURI());
		} else {
			imports = GamlResourceIndexer.allImportsOf(((GamlResourceDescription) candidate).getResource());
		}
		if (imports.isEmpty()) return false;
		for (Delta d : deltas) {
			if (d.haveEObjectDescriptionsChanged() && imports.containsKey(properlyEncodedURI(d.getUri()))) return true;
		}
		return false;
		// return super.isAffected(deltas, candidate, context);
	}

	@Override
	public boolean isAffectedByAny(final Collection<Delta> deltas, final IResourceDescription candidate,
			final IResourceDescriptions context) throws IllegalArgumentException {
		return isAffected(deltas, candidate, context);
	}
}
