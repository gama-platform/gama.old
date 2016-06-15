/*********************************************************************************************
 *
 *
 * 'GamlDecorator.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.decorators;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
/**
 * Copyright (c) 2011 Cloudsmith Inc. and other contributors, as listed below.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Cloudsmith
 *
 */
import msi.gama.gui.navigator.*;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * Simple decorator for error and warning
 *
 */
public class GamlDecorator implements ILightweightLabelDecorator {

	public static String decoratorId = "msi.gama.light.decorator";

	private final ImageDescriptor[] overlay = new ImageDescriptor[1];
	private final IResourceVisitor visitor = new IResourceVisitor() {

		@Override
		public boolean visit(final IResource resource) throws CoreException {
			if ( resource instanceof IFile && FileMetaDataProvider.isGAML((IFile) resource) ) {
				overlay[0] = getOkImageDescriptor();
				return false;
			}
			return true;
		}
	};

	// private final IResourceChangeListener listener = new IResourceChangeListener() {
	//
	// @Override
	// public void resourceChanged(final IResourceChangeEvent event) {
	// IMarkerDelta[] markerDeltas = event.findMarkerDeltas(IMarker.PROBLEM, true);
	// if ( markerDeltas.length > 0 ) {
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	//
	// @Override
	// public void run() {
	// PlatformUI.getWorkbench().getDecoratorManager().update(decoratorId);
	//
	// }
	// });
	// }
	// }
	// };

	public GamlDecorator() {
		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// workspace.addResourceChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.
	 * ILabelProviderListener)
	 */
	@Override
	public void addListener(final ILabelProviderListener listener) {}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 * org.eclipse.jface.viewers.IDecoration)
	 */
	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		if ( element instanceof WrappedFile ) {
			decorate((WrappedFile) element, decoration);
			return;
		}
		if ( element instanceof VirtualContent ) {
			decorate((VirtualContent) element, decoration);
			return;
		}
		// See plugin.xml . Only applicable to IResource or VirtualContent
		IResource resource = (IResource) element;
		if ( !resource.isAccessible() ) {
			if ( resource instanceof IProject ) {
				decoration.addOverlay(getClosedImageDescriptor(), IDecoration.BOTTOM_LEFT);
				return;
			}
		}
		if ( NavigatorLabelProvider.isResource(resource) ) { return; }
		int severity = -1;
		try {
			severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// e.printStackTrace();
		}
		overlay[0] = null;
		if ( severity == IMarker.SEVERITY_ERROR ) {
			overlay[0] = getErrorImageDescriptor();
		} else if ( severity == IMarker.SEVERITY_WARNING ) {
			overlay[0] = getWarningImageDescriptor();
		} else if ( resource instanceof IFile && FileMetaDataProvider.isGAML((IFile) resource) ) {
			overlay[0] = getOkImageDescriptor();
		} else if ( resource instanceof IContainer ) {
			try {
				((IContainer) resource).accept(visitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if ( overlay[0] != null ) {
			decoration.addOverlay(overlay[0], IDecoration.BOTTOM_LEFT);
		}
	}

	private void decorate(final WrappedFile element, final IDecoration decoration) {
		IFile file = element.getFile();
		if ( FileMetaDataProvider.isGAML(file) ) {
			try {
				int severity = file.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO);
				if ( severity == IMarker.SEVERITY_ERROR ) {
					decoration.addOverlay(getErrorImageDescriptor(), IDecoration.BOTTOM_LEFT);
				} else if ( severity == IMarker.SEVERITY_WARNING ) {
					decoration.addOverlay(getWarningImageDescriptor(), IDecoration.BOTTOM_LEFT);
				} else {
					decoration.addOverlay(getOkImageDescriptor(), IDecoration.BOTTOM_LEFT);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private void decorate(final VirtualContent element, final IDecoration decoration) {
		if ( !element.canBeDecorated() ) { return; }
		Object[] resources = element.getNavigatorChildren();
		int severity = -1;
		for ( Object o : resources ) {
			if ( o instanceof IResource ) {
				try {
					IResource r = (IResource) o;
					if ( r.isAccessible() ) {
						int s = ((IResource) o).findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
						if ( s > severity ) {
							severity = s;
						}
						if ( severity == IMarker.SEVERITY_ERROR ) {
							break;
						}
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		if ( severity == IMarker.SEVERITY_ERROR ) {
			decoration.addOverlay(getErrorImageDescriptor(), IDecoration.BOTTOM_LEFT);
		} else if ( severity == IMarker.SEVERITY_WARNING ) {
			decoration.addOverlay(getWarningImageDescriptor(), IDecoration.BOTTOM_LEFT);
		} else {
			decoration.addOverlay(getOkImageDescriptor(), IDecoration.BOTTOM_LEFT);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// if ( listener != null ) {
		// ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
		// }
	}

	private ImageDescriptor getOkImageDescriptor() {
		return GamaIcons.create("overlay.ok2").descriptor();
		// IGamaIcons.OVERLAY_OK.descriptor()
	}

	private ImageDescriptor getErrorImageDescriptor() {
		return GamaIcons.create("overlay.error2").descriptor();
	}

	private ImageDescriptor getWarningImageDescriptor() {
		return GamaIcons.create("overlay.warning2").descriptor();
	}

	private ImageDescriptor getClosedImageDescriptor() {
		return GamaIcons.create("overlay.closed2").descriptor();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.
	 * ILabelProviderListener)
	 */
	@Override
	public void removeListener(final ILabelProviderListener listener) {}

}