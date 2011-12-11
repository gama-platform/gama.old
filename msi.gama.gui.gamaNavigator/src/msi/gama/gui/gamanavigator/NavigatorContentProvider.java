/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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

import msi.gama.gui.application.GUI;
import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.*;

public class NavigatorContentProvider implements ITreeContentProvider, IResourceChangeListener {

	Viewer viewer;

	private static final Object[] EMPTY_ARRAY = new Object[0];

	public NavigatorContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
			IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		if ( parentElement instanceof VirtualModelsFolder ) {
			VirtualModelsFolder parent = (VirtualModelsFolder) parentElement;
			return parent.getChildren();
		} else if ( parentElement instanceof VirtualSharedModelsFolder ) {
			VirtualSharedModelsFolder parent = (VirtualSharedModelsFolder) parentElement;
			return parent.getChildren();
		} else if ( parentElement instanceof VirtualProjectFolder ) {
			VirtualProjectFolder parent = (VirtualProjectFolder) parentElement;
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
		GUI.run(new Runnable() {

			@Override
			public void run() {
				viewer.refresh();
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
