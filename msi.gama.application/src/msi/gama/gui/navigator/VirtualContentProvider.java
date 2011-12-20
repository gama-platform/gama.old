/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.navigator;

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