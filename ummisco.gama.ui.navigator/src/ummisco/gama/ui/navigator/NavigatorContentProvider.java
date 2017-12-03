/*********************************************************************************************
 *
 * 'NavigatorContentProvider.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import static org.eclipse.core.resources.IResourceChangeEvent.POST_CHANGE;
import static org.eclipse.core.resources.IResourceChangeEvent.PRE_DELETE;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.GAML_CT_ID;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.SHAPEFILE_CT_ID;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.SHAPEFILE_SUPPORT_CT_ID;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.getContentTypeId;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.shapeFileSupportedBy;
import static ummisco.gama.ui.navigator.contents.NavigatorRoot.INSTANCE;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
	private CommonViewer viewer;
	ResourceManager mapper;

	@Override
	public Object getParent(final Object element) {
		if (element instanceof VirtualContent) { return ((VirtualContent) element).getParent(); }
		if (element instanceof IFile) {
			final IFile file = (IFile) element;
			if (SHAPEFILE_SUPPORT_CT_ID.equals(getContentTypeId(file))) {
				final IResource shape = shapeFileSupportedBy(file);
				if (shape != null) { return shape; }
			}
			final IContainer parent = file.getParent();
			return mapper.findWrappedInstanceOf(parent);
		}
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
		if (element instanceof IFile) {
			final String ext = getContentTypeId((IFile) element);
			return GAML_CT_ID.equals(ext) || SHAPEFILE_CT_ID.equals(ext);
		}
		return super.hasChildren(element);
	}

	@Override
	public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
		this.viewer = (CommonViewer) v;
		mapper = new ResourceManager(this, viewer);
		INSTANCE.initializeVirtualFolders(mapper);
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
			if (parent != null && parent != INSTANCE)
				segments.add(0, parent);
		} while (parent != null && parent != INSTANCE);
		if (!segments.isEmpty()) { return new TreePath[] { new TreePath(segments.toArray()) }; }
		return new TreePath[0];
	}

}
