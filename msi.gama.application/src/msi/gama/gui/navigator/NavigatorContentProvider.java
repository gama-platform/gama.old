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

import java.util.*;
import msi.gama.util.file.GAMLFile;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

public class NavigatorContentProvider extends BaseWorkbenchContentProvider {

	private VirtualContent[] virtualFolders;

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object getParent(final Object element) {
		if ( element instanceof VirtualContent ) { return ((VirtualContent) element).getParent(); }
		if ( element instanceof IProject ) {
			for ( VirtualContent folder : virtualFolders ) {
				if ( folder.isParentOf(element) ) { return folder; }
			}
		}
		if ( element instanceof IFile &&
			FileMetaDataProvider.SHAPEFILE_SUPPORT_CT_ID.equals(FileMetaDataProvider.getContentTypeId((IFile) element)) ) {
			IResource r = FileMetaDataProvider.shapeFileSupportedBy((IFile) element);
			if ( r != null ) { return r; }
		}
		return super.getParent(element);
	}

	@Override
	public Object[] getChildren(final Object p) {
		if ( p instanceof NavigatorRoot ) {
			if ( virtualFolders == null ) {
				initializeVirtualFolders(p);
			}
			return virtualFolders;
		}
		if ( p instanceof VirtualContent ) { return ((VirtualContent) p).getNavigatorChildren(); }
		if ( p instanceof IFile ) {
			String ctid = FileMetaDataProvider.getContentTypeId((IFile) p);
			if ( ctid.equals(FileMetaDataProvider.GAML_CT_ID) ) {
				GAMLFile.GamlInfo info = (GAMLFile.GamlInfo) FileMetaDataProvider.getInstance().getMetaData(p, false);
				if ( info == null ) { return VirtualContent.EMPTY; }
				List l = new ArrayList();
				for ( String s : info.experiments ) {
					l.add(new WrappedExperiment((IFile) p, s));
				}
				if ( !info.imports.isEmpty() ) {
					l.add(new WrappedFolder((IFile) p, info.imports, "Imports"));
				}
				if ( !info.uses.isEmpty() ) {
					l.add(new WrappedFolder((IFile) p, info.uses, "Uses"));
				}
				return l.toArray();

			} else if ( ctid.equals(FileMetaDataProvider.SHAPEFILE_CT_ID) ) {
				try {
					IContainer folder = ((IFile) p).getParent();
					List<IResource> sub = new ArrayList();
					for ( IResource r : folder.members() ) {
						if ( r instanceof IFile && FileMetaDataProvider.isSupport((IFile) p, (IFile) r) ) {
							sub.add(r);
						}
					}
					return sub.toArray();
				} catch (CoreException e) {
					e.printStackTrace();
					return super.getChildren(p);
				}
			}

		}
		return super.getChildren(p);
	}

	@Override
	public boolean hasChildren(final Object element) {
		if ( element instanceof VirtualContent ) { return ((VirtualContent) element).hasChildren(); }
		if ( element instanceof NavigatorRoot ) { return true; }
		if ( element instanceof IFile ) {
			String ext = FileMetaDataProvider.getContentTypeId((IFile) element);
			return FileMetaDataProvider.GAML_CT_ID.equals(ext) || FileMetaDataProvider.SHAPEFILE_CT_ID.equals(ext);
		}
		return super.hasChildren(element);
	}

	@Override
	public void dispose() {
		super.dispose();
		this.virtualFolders = null;
	}

	private void initializeVirtualFolders(final Object parentElement) {
		virtualFolders =
			new VirtualContent[] { new UserProjectsFolder(parentElement, "User models"),
			new ModelsLibraryFolder(parentElement, "Models library") };
	}
}
