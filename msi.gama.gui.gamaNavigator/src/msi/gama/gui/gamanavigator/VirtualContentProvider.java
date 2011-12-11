/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.gamanavigator;

import org.eclipse.jface.viewers.*;

public class VirtualContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[0];
	private VirtualFolder[] virtualFolder;

	@Override
	public Object[] getChildren(final Object parentElement) {
		if ( parentElement instanceof NavigatorRoot ) {
			if ( virtualFolder == null ) {
				initializeVirtualFolders(parentElement);
			}
			return virtualFolder;
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
		this.virtualFolder = null;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {}

	/**
	 * Init code for empty model
	 * @return
	 * @return
	 */
	private void initializeVirtualFolders(final Object parentElement) {
		this.virtualFolder = new VirtualFolder[3];
		this.virtualFolder[0] = new VirtualProjectFolder("User models");
		this.virtualFolder[0].setRootElement(parentElement);
		this.virtualFolder[1] = new VirtualModelsFolder("Models library");
		this.virtualFolder[1].setRootElement(parentElement);
		this.virtualFolder[2] = new VirtualSharedModelsFolder("Shared models");
		this.virtualFolder[2].setRootElement(parentElement);
	}
}