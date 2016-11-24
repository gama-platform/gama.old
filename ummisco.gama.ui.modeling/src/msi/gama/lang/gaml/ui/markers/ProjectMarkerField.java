/*********************************************************************************************
 *
 * 'ProjectMarkerField.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.markers;

import org.eclipse.ui.views.markers.*;

public class ProjectMarkerField extends MarkerField {

	public ProjectMarkerField() {}

	@Override
	public String getValue(final MarkerItem item) {
		if ( item.getMarker() == null ) { return null; }
		return item.getMarker().getResource().getProject().getName();
	}

}
