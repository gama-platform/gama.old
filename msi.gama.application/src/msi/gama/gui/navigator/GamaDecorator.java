/*********************************************************************************************
 * 
 *
 * 'GamaDecorator.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.navigator;

import msi.gama.gui.swt.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.team.svn.ui.decorator.SVNLightweightDecorator;

public class GamaDecorator extends SVNLightweightDecorator {

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		IResource r = super.getResource(element);
		if ( r == null || r.getType() == IResource.ROOT ) { return; }
		IProject p = r.getProject();
		if ( p == null ) { return; }
		if ( !p.isAccessible() || !p.isOpen() ) { return; }
		// We dont decorate built-in projects.
		try {
			if ( p.getNature(WorkspaceModelsManager.builtInNature) != null ) {
				if ( r.getType() == IResource.FILE ) {
					decoration.addOverlay(IGamaIcons.SMALL_PIN.descriptor(), IDecoration.TOP_LEFT);
				}
				return;
			}
		} catch (CoreException e) {}
		super.decorate(element, decoration);
	}

}
