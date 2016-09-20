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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import gnu.trove.map.hash.TIntObjectHashMap;
import ummisco.gama.ui.navigator.NavigatorLabelProvider;
import ummisco.gama.ui.navigator.VirtualContent;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * Simple decorator for error and warning
 *
 */
public class GamlDecorator implements ILightweightLabelDecorator {

	public static String decoratorId = "msi.gama.light.decorator";
	private static final TIntObjectHashMap<ImageDescriptor> DESCRIPTORS = new TIntObjectHashMap() {
		{
			put(-1, GamaIcons.create("overlay.ok2").descriptor());
			put(IMarker.SEVERITY_INFO, GamaIcons.create("overlay.ok2").descriptor());
			put(IMarker.SEVERITY_WARNING, GamaIcons.create("overlay.warning2").descriptor());
			put(IMarker.SEVERITY_ERROR, GamaIcons.create("overlay.error2").descriptor());
		}
	};

	@Override
	public void addListener(final ILabelProviderListener listener) {
	}

	@Override
	public void decorate(final Object element, final IDecoration decoration) {

		// if (element instanceof WrappedSyntacticContent) {
		// boolean = ((WrappedSyntacticContent) element).find;
		// if (markers.length > 0) {
		// System.out.println("Markers found");
		// final Map<String, Object> m = markers[0].getAttributes();
		// if (m != null) {
		// System.out.println("m");
		// }
		// }
		// }
		if (element instanceof VirtualContent) {
			final VirtualContent element1 = (VirtualContent) element;
			if (element1.canBeDecorated()) {
				decoration.addOverlay(DESCRIPTORS.get(element1.findMaxProblemSeverity()), IDecoration.BOTTOM_LEFT);
			}
			return;
		}
		// See plugin.xml . Only applicable to IResource or VirtualContent
		final IResource resource = (IResource) element;
		if (!resource.isAccessible()) {
			if (resource instanceof IProject) {
				decoration.addOverlay(GamaIcons.create("overlay.closed2").descriptor(), IDecoration.BOTTOM_LEFT);
			}
			return;
		}
		if (NavigatorLabelProvider.isResource(resource)) {
			return;
		}
		try {

			decoration.addOverlay(
					DESCRIPTORS.get(resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)),
					IDecoration.BOTTOM_LEFT);
		} catch (final CoreException e) {
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}

}