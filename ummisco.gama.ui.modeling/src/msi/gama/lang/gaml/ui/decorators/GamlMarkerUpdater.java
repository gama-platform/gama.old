/*******************************************************************************************************
 *
 * GamlMarkerUpdater.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.decorators;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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

import ummisco.gama.dev.utils.DEBUG;

/**
 * The class GamlMarkerUpdater.
 *
 * @author drogoul
 * @since 4 sept. 2016
 *
 */
public class GamlMarkerUpdater extends MarkerUpdaterImpl {

	/** The eraser. */
	final MarkerEraser eraser = new MarkerEraser();

	/** The mapper. */
	@Inject IStorage2UriMapper mapper;

	@Override
	public void updateMarkers(final Delta delta, final ResourceSet resourceSet, final IProgressMonitor monitor) {

		final URI uri = delta.getUri();
		final IResourceUIValidatorExtension validatorExtension = getResourceUIValidatorExtension(uri);
		final IMarkerContributor markerContributor = getMarkerContributor(uri);
		final CheckMode normalAndFastMode = CheckMode.NORMAL_AND_FAST;
		for (final Pair<IStorage, IProject> pair : mapper.getStorages(uri)) {
			if (monitor.isCanceled()) return;
			if (pair.getFirst() instanceof IFile file) {
				if (delta.getNew() != null) {
					if (resourceSet == null) return;
					final Resource resource = resourceSet.getResource(uri, true);
					if (validatorExtension != null) {
						validatorExtension.updateValidationMarkers(file, resource, normalAndFastMode, monitor);
					}
					if (markerContributor != null) { markerContributor.updateMarkers(file, resource, monitor); }
					// GAMA.getGui().getMetaDataProvider().storeMetadata(file,
					// info.getInfo(resource, file.getModificationStamp()),
					// true);
				} else {
					if (validatorExtension != null) {
						validatorExtension.deleteValidationMarkers(file, normalAndFastMode, monitor);
					} else {
						eraser.deleteValidationMarkers(file, normalAndFastMode, monitor);
					}
					if (markerContributor != null) {
						markerContributor.deleteMarkers(file, monitor);
					} else {
						try {
							file.deleteMarkers(IMarkerContributor.MARKER_TYPE, true, IResource.DEPTH_ZERO);
						} catch (final CoreException e) {
							DEBUG.ERR(e.getMessage());
						}
					}
				}
			}
		}

	}

}
