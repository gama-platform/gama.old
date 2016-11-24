/*********************************************************************************************
 *
 * 'GamlDescriptionMarkerField.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.views.markers.MarkerSeverityAndDescriptionField;
import org.eclipse.ui.views.markers.MarkerItem;

public class GamlDescriptionMarkerField extends MarkerSeverityAndDescriptionField {

	public GamlDescriptionMarkerField() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.views.markers.MarkerField#update(org.eclipse.jface.viewers
	 * .ViewerCell)
	 */
	@Override
	public void update(final ViewerCell cell) {
		final MarkerItem item = (MarkerItem) cell.getElement();
		Image image = null;
		if (item.getMarker() == null) {
			image = GamlMarkerImageProvider.getImage(item.getAttributeValue(IMarker.MESSAGE, "")).image();
		} else {
			try {
				if (item.getMarker().isSubtypeOf(IMarker.TASK)) {
					image = GamlMarkerImageProvider.getImage(-1).image();
				} else {
					image = GamlMarkerImageProvider
							.getImage(item.getMarker().getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING))
							.image();
				}
			} catch (final CoreException e) {
				// e.printStackTrace();
			}
		}

		cell.setText(getValue(item));
		cell.setImage(image);
	}

}
