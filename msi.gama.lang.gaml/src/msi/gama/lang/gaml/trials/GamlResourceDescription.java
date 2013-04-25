/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml.trials;

import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.impl.DefaultResourceDescription;
import org.eclipse.xtext.util.IResourceScopeCache;
import com.google.inject.Inject;

/**
 * The class GamlResourceDescription.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */

@Deprecated
public class GamlResourceDescription extends DefaultResourceDescription {

	/**
	 * Default constructor
	 * 
	 * @param resource
	 * @param strategy
	 * @param cache
	 */
	@Inject
	public GamlResourceDescription(final Resource resource, final IDefaultResourceDescriptionStrategy strategy,
		final IResourceScopeCache cache) {
		super(resource, strategy, cache);
	}

	@Override
	protected List<IEObjectDescription> computeExportedObjects() {
		List<IEObjectDescription> result = computeExportedObjects();
		// GuiUtils.debug("Exported objects from " + this.getURI().lastSegment());
		for ( IEObjectDescription o : result ) {
			// GuiUtils.debug("     " + o.toString());
		}
		return result;
	}

}
