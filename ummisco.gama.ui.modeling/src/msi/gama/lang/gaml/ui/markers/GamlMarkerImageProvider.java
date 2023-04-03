/*******************************************************************************************************
 *
 * GamlMarkerImageProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.internal.ide.IMarkerImageProvider;

import msi.gama.application.workbench.ThemeHelper;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The Class GamlMarkerImageProvider.
 */
public class GamlMarkerImageProvider implements IMarkerImageProvider {

	/**
	 * Instantiates a new gaml marker image provider.
	 */
	public GamlMarkerImageProvider() {}

	/**
	 * Returns the relative path for the image to be used for displaying an marker in the workbench. This path is
	 * relative to the plugin location
	 *
	 * Returns <code>null</code> if there is no appropriate image.
	 *
	 * @param marker
	 *            The marker to get an image path for.
	 *
	 */
	@Override
	public String getImagePath(final IMarker marker) {
		GamaIcon icon = getImage(marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING));
		if (icon == null) return null;
		String iconPath = "/icons/full/";
		return iconPath + icon.getCode() + ".png";
	}

	/**
	 * Gets the image.
	 *
	 * @param description
	 *            the description
	 * @return the image
	 */
	public static GamaIcon getImage(final String description) {
		if (description.contains("Errors")) return getImage(IMarker.SEVERITY_ERROR);
		if (description.contains("Warnings")) return getImage(IMarker.SEVERITY_WARNING);
		if (description.contains("Info")) return getImage(IMarker.SEVERITY_INFO);
		if (description.contains("Task")) return getImage(-1);
		return null;
	}

	/**
	 * Gets the image.
	 *
	 * @param severity
	 *            the severity
	 * @return the image
	 */
	public static GamaIcon getImage(final int severity) {
		return switch (severity) {
			case IMarker.SEVERITY_ERROR -> GamaIcon
					.named(ThemeHelper.isDark() ? IGamaIcons.MARKER_ERROR_DARK : IGamaIcons.MARKER_ERROR);
			case IMarker.SEVERITY_WARNING -> GamaIcon.named(IGamaIcons.MARKER_WARNING);
			case IMarker.SEVERITY_INFO -> GamaIcon
					.named(ThemeHelper.isDark() ? IGamaIcons.MARKER_INFO_DARK : IGamaIcons.MARKER_INFO);
			case -1 -> GamaIcon.named(IGamaIcons.MARKER_TASK);
			default -> null;
		};

	}

}
