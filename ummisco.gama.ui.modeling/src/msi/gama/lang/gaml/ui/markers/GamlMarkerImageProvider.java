/*******************************************************************************************************
 *
 * GamlMarkerImageProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.internal.ide.IMarkerImageProvider;

import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * The Class GamlMarkerImageProvider.
 */
public class GamlMarkerImageProvider implements IMarkerImageProvider {

	/**
	 * Instantiates a new gaml marker image provider.
	 */
	public GamlMarkerImageProvider() {}

	/**
	 * Returns the relative path for the image
	 * to be used for displaying an marker in the workbench.
	 * This path is relative to the plugin location
	 *
	 * Returns <code>null</code> if there is no appropriate image.
	 *
	 * @param marker The marker to get an image path for.
	 *
	 */
	@Override
	public String getImagePath(final IMarker marker) {
		GamaIcon icon = getImage(marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING));
		if ( icon == null ) { return null; }
		String iconPath = "/icons/full/";
		return iconPath + icon.getCode() + ".png";
	}

	/**
	 * Gets the image.
	 *
	 * @param description the description
	 * @return the image
	 */
	public static GamaIcon getImage(final String description) {
		if ( description.contains("Errors") ) {
			return getImage(IMarker.SEVERITY_ERROR);
		} else if ( description.contains("Warnings") ) {
			return getImage(IMarker.SEVERITY_WARNING);
		} else if ( description.contains("Info") ) {
			return getImage(IMarker.SEVERITY_INFO);
		} else if ( description.contains("Task") ) {
			return getImage(-1);
		} else {
			return null;
		}
	}

	/**
	 * Gets the image.
	 *
	 * @param severity the severity
	 * @return the image
	 */
	public static GamaIcon getImage(final int severity) {
		switch (severity) {
			case IMarker.SEVERITY_ERROR:
				return GamaIcons.create("marker.error2");
			case IMarker.SEVERITY_WARNING:
				return GamaIcons.create("marker.warning2");
			case IMarker.SEVERITY_INFO:
				return GamaIcons.create("marker.info2");
			case -1: {
				return GamaIcons.create("marker.task2");
			}
		}

		return null;
	}

}
