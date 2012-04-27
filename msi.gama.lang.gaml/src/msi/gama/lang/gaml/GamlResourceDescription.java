/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml;

import java.util.List;
import msi.gama.common.util.GuiUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
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

	@Override
	protected List<IEObjectDescription> computeExportedObjects() {
		List<IEObjectDescription> result = super.computeExportedObjects();
		GuiUtils.debug("Size of exported object descriptions from " + getURI() + ": " +
			result.size());
		return result;
	}

}
