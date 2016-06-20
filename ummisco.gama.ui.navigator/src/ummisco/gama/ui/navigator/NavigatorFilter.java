package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ummisco.gama.ui.metadata.FileMetaDataProvider;

public class NavigatorFilter extends ViewerFilter {

	public NavigatorFilter() {
	}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		if (parentElement instanceof TreePath && element instanceof IFile) {
			final TreePath p = (TreePath) parentElement;
			if (p.getLastSegment() instanceof IFolder) {
				final IResource r = FileMetaDataProvider.shapeFileSupportedBy((IFile) element);
				if (r != null) {
					// System.out.println("Filtering out " + element);
					return false;
				}
			}
		}
		return true;
	}

}
