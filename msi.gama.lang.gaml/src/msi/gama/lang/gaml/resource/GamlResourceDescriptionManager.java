/*********************************************************************************************
 * 
 * 
 * 'GamlResourceDescriptionManager.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.resource;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.DescriptionUtils;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;

import com.google.inject.Inject;

import msi.gama.lang.gaml.validation.GamlJavaValidator;

/**
 * The class GamlResourceDescriptionManager.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager {

	@Inject
	private DescriptionUtils descriptionUtils;

	@Override
	protected IResourceDescription internalGetResourceDescription(final Resource resource,
			final IDefaultResourceDescriptionStrategy strategy) {
		return new GamlResourceDescription(resource, strategy, getCache());
	}

	@Override
	public boolean isAffected(final Collection<Delta> deltas, final IResourceDescription candidate,
			final IResourceDescriptions context) {
		final boolean result = false;
		final Set<String> imports = GamlJavaValidator.GLOBAL_URI_IMPORTS_CACHE_HACK.get(candidate.getURI());
		if (imports != null) {
			for (final Delta d : deltas) {
				final String relative = URI.decode(d.getUri().deresolve(candidate.getURI()).toString());
				if (imports.contains(relative)) {
					return true;
				}
			}
		}
		return super.isAffected(deltas, candidate, context);
	}
}
