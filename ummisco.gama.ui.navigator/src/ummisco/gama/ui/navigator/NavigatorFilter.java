package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.*;

import msi.gama.gui.metadata.FileMetaDataProvider;

public class NavigatorFilter extends ViewerFilter {

	public NavigatorFilter() {}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		if ( parentElement instanceof TreePath && element instanceof IFile ) {
			TreePath p = (TreePath) parentElement;
			if ( p.getLastSegment() instanceof IFolder ) {
				IResource r = FileMetaDataProvider.shapeFileSupportedBy((IFile) element);
				if ( r != null ) {
					// System.out.println("Filtering out " + element);
					return false;
				}
			}
		}
		return true;
	}

}
