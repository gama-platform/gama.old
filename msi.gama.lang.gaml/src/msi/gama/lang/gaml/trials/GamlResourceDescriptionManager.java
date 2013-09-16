/**
 * Created by drogoul, 20 avr. 2012
 * 
 */
package msi.gama.lang.gaml.trials;

import java.util.Collection;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IContainer.Manager;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionManager;

/**
 * The class GamlResourceDescriptionManager.
 * 
 * @author drogoul
 * @since 20 avr. 2012
 * 
 */
@Deprecated
public class GamlResourceDescriptionManager extends DefaultResourceDescriptionManager {

	// @Override
	// public IResourceScopeCache getCache() {
	// // To remove one day ?
	// return IResourceScopeCache.NullImpl.INSTANCE;
	// }

	@Override
	public Manager getContainerManager() {
		return super.getContainerManager();
	}

	@Override
	public boolean isAffected(final Delta delta, final IResourceDescription candidate) throws IllegalArgumentException {
		// GuiUtils.debug("GamlResourceDescriptionManager.isAffected delta " + delta.haveEObjectDescriptionsChanged() +
		// " candidate " + candidate.getURI());
		return super.isAffected(delta, candidate);
	}

	@Override
	public boolean isAffected(final Collection<Delta> deltas, final IResourceDescription candidate,
		final IResourceDescriptions context) {
		// GuiUtils.debug("GamlResourceDescriptionManager.isAffected candidate " + candidate.getURI() + " context " +
		// context.getAllResourceDescriptions());
		boolean result = super.isAffected(deltas, candidate, context);
		return result;
	}

	@Override
	protected boolean isAffected(final Collection<QualifiedName> importedNames, final IResourceDescription description) {
		// GuiUtils.debug("GamlResourceDescriptionManager.isAffected " + importedNames + " resource " +
		// description.getURI());
		return super.isAffected(importedNames, description);
	}

	@Override
	protected IResourceDescription internalGetResourceDescription(final Resource resource,
		final IDefaultResourceDescriptionStrategy strategy) {
		return new GamlResourceDescription(resource, strategy, getCache());
	}

}
