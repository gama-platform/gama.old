/*********************************************************************************************
 *
 * 'GamlDecorator.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.decorators;

import static org.eclipse.core.resources.IMarker.PROBLEM;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.jface.viewers.IDecoration.BOTTOM_LEFT;
import static ummisco.gama.ui.navigator.contents.VirtualContent.DESCRIPTORS;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import msi.gama.common.GamlFileExtension;
import ummisco.gama.ui.navigator.contents.VirtualContent;

/**
 * Simple decorator for error and warning
 *
 */
public class GamlDecorator implements ILightweightLabelDecorator {

	public static String decoratorId = "msi.gama.light.decorator";

	@Override
	public void addListener(final ILabelProviderListener listener) {}

	@Override
	public void decorate(final Object element, final IDecoration deco) {
		if (element instanceof VirtualContent) {
			deco.addOverlay(((VirtualContent<?>) element).getOverlay(), BOTTOM_LEFT);
		} else if (element instanceof IFile) {
			final IFile r = (IFile) element;
			if (GamlFileExtension.isAny(r.getName()))
				try {
					deco.addOverlay(DESCRIPTORS.get(r.findMaxProblemSeverity(PROBLEM, true, DEPTH_INFINITE)),
							BOTTOM_LEFT);
				} catch (final CoreException e) {}
		}
	}

	@Override
	public void dispose() {}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {}

}