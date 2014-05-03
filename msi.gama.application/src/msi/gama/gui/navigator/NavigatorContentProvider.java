/*********************************************************************************************
 * 
 * 
 * 'NavigatorContentProvider.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Display;

public class NavigatorContentProvider implements ITreeContentProvider, IResourceChangeListener {

	Viewer viewer;

	private static final Object[] EMPTY_ARRAY = new Object[0];

	public NavigatorContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		if ( parentElement instanceof ModelsLibraryFolder ) {
			ModelsLibraryFolder parent = (ModelsLibraryFolder) parentElement;
			return parent.getChildren();
		} else if ( parentElement instanceof VirtualSharedModelsFolder ) {
			VirtualSharedModelsFolder parent = (VirtualSharedModelsFolder) parentElement;
			return parent.getChildren();
		} else if ( parentElement instanceof UserProjectsFolder ) {
			UserProjectsFolder parent = (UserProjectsFolder) parentElement;
			return parent.getChildren();
		} else if ( parentElement instanceof FileBean ) {
			FileBean parent = (FileBean) parentElement;
			return parent.getChildren();
		} else {
			return EMPTY_ARRAY;
		}
	}

	@Override
	public Object getParent(final Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		if ( element instanceof FileBean ) {
			FileBean file = (FileBean) element;
			return file.hasChildren();
		}
		return false;
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				TreePath[] treePaths = ((TreeViewer) viewer).getExpandedTreePaths();
				viewer.refresh();
				((TreeViewer) viewer).setExpandedTreePaths(treePaths);
			}
		});
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		this.viewer = viewer;
	}
}
