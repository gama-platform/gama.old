/*******************************************************************************************************
 *
 * NavigatorFilter.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.WrappedFolder;
import ummisco.gama.ui.utils.PreferencesHelper;

/**
 * The Class NavigatorFilter.
 */
public class NavigatorFilter extends ViewerFilter {

	/**
	 * Instantiates a new navigator filter.
	 */
	public NavigatorFilter() {}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		IResource file = ResourceManager.getResource(element);
		if (file == null) return true;
		if (file.getName().charAt(0) == '.' && !PreferencesHelper.NAVIGATOR_HIDDEN.getValue()) return false;
		if (parentElement instanceof final TreePath p && p.getLastSegment() instanceof WrappedFolder
				&& file instanceof IFile f) {
			final IResource r = FileMetaDataProvider.shapeFileSupportedBy(f);
			if (r != null) return false;
		}
		return true;
	}

}
