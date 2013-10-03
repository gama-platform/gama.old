package msi.gama.gui.navigator;

import msi.gama.gui.swt.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.team.svn.ui.decorator.SVNLightweightDecorator;

public class GamaDecorator extends SVNLightweightDecorator {

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		IResource r = super.getResource(element);
		if ( r == null || r.getType() == IResource.ROOT ) { return; }
		IProject p = r.getProject();
		if ( p == null ) { return; }
		if ( !p.isAccessible() || !p.isOpen() ) { return; }
		// We dont decorate built-in projects.
		try {
			if ( p.getNature(ApplicationWorkbenchAdvisor.builtInNature) != null ) {
				if ( r.getType() == IResource.FILE ) {
					decoration.addOverlay(IGamaIcons.SMALL_PIN.descriptor(), IDecoration.TOP_LEFT);
				}
				return;
			}
		} catch (CoreException e) {}
		super.decorate(element, decoration);
	}

}
