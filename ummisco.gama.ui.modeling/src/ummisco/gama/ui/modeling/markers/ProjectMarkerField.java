package ummisco.gama.ui.modeling.markers;

import org.eclipse.ui.views.markers.*;

public class ProjectMarkerField extends MarkerField {

	public ProjectMarkerField() {}

	@Override
	public String getValue(final MarkerItem item) {
		if ( item.getMarker() == null ) { return null; }
		return item.getMarker().getResource().getProject().getName();
	}

}
