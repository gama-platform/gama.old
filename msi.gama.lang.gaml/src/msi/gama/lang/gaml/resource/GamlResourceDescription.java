/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml.resource;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;
import org.eclipse.xtext.util.IResourceScopeCache;

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
	public GamlResourceDescription(final Resource resource,
		final IDefaultResourceDescriptionStrategy strategy, final IResourceScopeCache cache) {
		super(resource, strategy, cache);
	}

}
