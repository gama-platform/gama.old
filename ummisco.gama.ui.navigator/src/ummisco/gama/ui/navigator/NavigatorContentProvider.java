/*********************************************************************************************
 *
 * 'NavigatorContentProvider.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import static org.eclipse.core.resources.IResourceChangeEvent.POST_CHANGE;
import static org.eclipse.core.resources.IResourceChangeEvent.PRE_DELETE;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static ummisco.gama.ui.navigator.contents.NavigatorRoot.getInstance;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.navigator.CommonViewer;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.navigator.contents.VirtualContent;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class NavigatorContentProvider extends WorkbenchContentProvider implements ITreePathContentProvider {

	public volatile static boolean FILE_CHILDREN_ENABLED = true;

	@Override
	public Object getParent(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).getParent(); }
		return super.getParent(element);
	}

	@Override
	public Object[] getChildren(final Object p) {
		if (p instanceof VirtualContent) { return ((VirtualContent) p).getNavigatorChildren(); }
		return super.getChildren(p);
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).hasChildren(); }
		return super.hasChildren(element);
	}

	@Override
	public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
		final CommonViewer viewer = (CommonViewer) v;
		final ResourceManager mapper = new ResourceManager(this, viewer);
		getInstance().resetVirtualFolders(mapper);
		getWorkspace().addResourceChangeListener(mapper, POST_CHANGE | PRE_DELETE);
		super.inputChanged(viewer, oldInput, newInput);
	}

	@Override
	public Object[] getChildren(final TreePath parentPath) {
		return getChildren(parentPath.getLastSegment());
	}

	@Override
	public boolean hasChildren(final TreePath path) {
		return hasChildren(path.getLastSegment());
	}

	@Override
	public TreePath[] getParents(final Object element) {
		final ArrayList segments = new ArrayList();
		Object parent = element;
		do {
			parent = getParent(parent);
			if (parent != null && parent != getInstance())
				segments.add(0, parent);
		} while (parent != null && parent != getInstance());
		if (!segments.isEmpty()) { return new TreePath[] { new TreePath(segments.toArray()) }; }
		return new TreePath[0];
	}

}
