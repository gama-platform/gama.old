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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;

/**
 * The class GamlResourceDescriptionManager.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager {

	@Override
	protected IResourceDescription internalGetResourceDescription(final Resource resource,
		final IDefaultResourceDescriptionStrategy strategy) {
		return new GamlResourceDescription(resource, strategy, getCache());
	}

}
