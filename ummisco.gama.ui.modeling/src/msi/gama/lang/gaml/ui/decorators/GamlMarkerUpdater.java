/**
 * Created by drogoul, 4 sept. 2016
 * 
 */
package msi.gama.lang.gaml.ui.decorators;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.builder.builderState.MarkerUpdaterImpl;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.ui.markers.IMarkerContributor;
import org.eclipse.xtext.ui.resource.IStorage2UriMapper;
import org.eclipse.xtext.ui.validation.IResourceUIValidatorExtension;
import org.eclipse.xtext.ui.validation.MarkerEraser;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.validation.CheckMode;

import com.google.inject.Inject;

/**
 * The class GamlMarkerUpdater.
 *
 * @author drogoul
 * @since 4 sept. 2016
 *
 */
public class GamlMarkerUpdater extends MarkerUpdaterImpl {

	@Inject IStorage2UriMapper mapper;

	@Override
	public void updateMarkers(final Delta delta, final ResourceSet resourceSet, final IProgressMonitor monitor)
			throws OperationCanceledException {

		final URI uri = delta.getUri();
		final IResourceUIValidatorExtension validatorExtension = getResourceUIValidatorExtension(uri);
		final IMarkerContributor markerContributor = getMarkerContributor(uri);
		final CheckMode normalAndFastMode = CheckMode.NORMAL_AND_FAST;

		for (final Pair<IStorage, IProject> pair : mapper.getStorages(uri)) {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			if (pair.getFirst() instanceof IFile) {
				final IFile file = (IFile) pair.getFirst();

				if (delta.getNew() != null) {
					if (resourceSet == null)
						throw new IllegalArgumentException("resourceSet may not be null for changed resources.");

					final Resource resource = resourceSet.getResource(uri, true);

					if (validatorExtension != null) {
						validatorExtension.updateValidationMarkers(file, resource, normalAndFastMode, monitor);
					}
					if (markerContributor != null) {
						markerContributor.updateMarkers(file, resource, monitor);
					}
					// GAMA.getGui().getMetaDataProvider().storeMetadata(file,
					// info.getInfo(resource, file.getModificationStamp()),
					// true);
				} else {
					if (validatorExtension != null) {
						validatorExtension.deleteValidationMarkers(file, normalAndFastMode, monitor);
					} else {
						deleteAllValidationMarker(file, normalAndFastMode, monitor);
					}
					if (markerContributor != null) {
						markerContributor.deleteMarkers(file, monitor);
					} else {
						deleteAllContributedMarkers(file, monitor);
					}
				}
			}
		}

	}

	private void deleteAllValidationMarker(final IFile file, final CheckMode checkMode,
			final IProgressMonitor monitor) {
		final MarkerEraser markerEraser = new MarkerEraser();
		markerEraser.deleteValidationMarkers(file, checkMode, monitor);
	}

	private void deleteAllContributedMarkers(final IFile file, final IProgressMonitor monitor) {
		try {
			file.deleteMarkers(IMarkerContributor.MARKER_TYPE, true, IResource.DEPTH_ZERO);
		} catch (final CoreException e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
