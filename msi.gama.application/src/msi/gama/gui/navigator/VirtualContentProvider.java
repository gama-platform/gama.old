/*********************************************************************************************
 * 
 *
 * 'VirtualContentProvider.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import org.eclipse.jface.viewers.*;

public class VirtualContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];
	private VirtualFolder[] virtualFolders;

	@Override
	public Object[] getChildren(final Object parentElement) {
		if ( parentElement instanceof NavigatorRoot ) {
			if ( virtualFolders == null ) {
				initializeVirtualFolders(parentElement);
			}
			return virtualFolders;
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(final Object element) {
		if ( element instanceof VirtualFolder ) { return ((VirtualFolder) element).getRootElement(); }
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		return element instanceof NavigatorRoot || element instanceof VirtualFolder;
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		this.virtualFolders = null;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {}

	/**
	 * Init code for empty model
	 * @return
	 * @return
	 */
	private void initializeVirtualFolders(final Object parentElement) {
		this.virtualFolders = new VirtualFolder[3];
		this.virtualFolders[0] = new UserProjectsFolder("User models");
		this.virtualFolders[0].setRootElement(parentElement);
		this.virtualFolders[1] = new ModelsLibraryFolder("Models library");
		this.virtualFolders[1].setRootElement(parentElement);
		this.virtualFolders[2] = new VirtualSharedModelsFolder("Shared models");
		this.virtualFolders[2].setRootElement(parentElement);
	}
}