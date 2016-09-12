/**
 * Created by drogoul, 11 sept. 2016
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.builder.clustering.ClusteringBuilderState;
import org.eclipse.xtext.builder.impl.BuildData;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsData;

import gnu.trove.set.hash.THashSet;

/**
 * The class GamlBuilderState.
 *
 * @author drogoul
 * @since 11 sept. 2016
 *
 */
public class GamlBuilderState extends ClusteringBuilderState {

	BuildData data;
	final Set<URI> alreadyQueuedInSession = new THashSet();

	@Override
	protected Collection<Delta> doUpdate(final BuildData buildData, final ResourceDescriptionsData newData,
			final IProgressMonitor monitor) {
		if (data != buildData) {
			alreadyQueuedInSession.clear();
			data = buildData;
		}
		return super.doUpdate(buildData, newData, monitor);
	}

	public void queueNewUri(final URI uri) {
		if (alreadyQueuedInSession.contains(uri))
			return;
		alreadyQueuedInSession.add(uri);
		if (data == null)
			return;
		// if (data.getURIQueue().contains(uri))
		// return;
		System.out.println("Queuing " + uri.lastSegment());
		data.queueURI(uri);
	}

}
