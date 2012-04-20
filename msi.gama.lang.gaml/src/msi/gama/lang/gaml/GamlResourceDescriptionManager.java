/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

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
