/*********************************************************************************************
 *
 * 'NavigatorFilter.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.WrappedFolder;

public class NavigatorFilter extends ViewerFilter {

	public NavigatorFilter() {}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		if (parentElement instanceof TreePath && ResourceManager.isFile(element)) {
			final TreePath p = (TreePath) parentElement;
			if (p.getLastSegment() instanceof WrappedFolder) {
				final IResource r = FileMetaDataProvider.shapeFileSupportedBy(ResourceManager.getFile(element));
				if (r != null) { return false; }
			}
		}
		return true;
	}

}
