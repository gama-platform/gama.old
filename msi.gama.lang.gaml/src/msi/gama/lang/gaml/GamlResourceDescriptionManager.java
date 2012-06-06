/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;
import org.eclipse.xtext.util.IResourceScopeCache;

/**
 * The class GamlResourceDescriptionManager.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager {

	@Override
	public IResourceScopeCache getCache() {
		// To remove one day ?
		return IResourceScopeCache.NullImpl.INSTANCE;
	}

}
