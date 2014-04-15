/*********************************************************************************************
 * 
 *
 * 'WebDisplayView.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.perspectives.ModelingPerspective;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;

public class WebDisplayView extends LayeredDisplayView {

	public static final String ID = GuiUtils.WEB_VIEW_ID;

	@Override
	protected Composite createSurfaceComposite() {
		surfaceComposite = new Browser(parent, SWT.NONE);

		perspectiveListener = new IPerspectiveListener() {

			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if ( perspective.getId().equals(ModelingPerspective.ID) ) {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						previousState = getOutput().getSurface().isPaused();
						getOutput().getSurface().setPaused(true);
					}
					if ( overlay != null && layersOverlay != null ) {
						overlay.hide();
						layersOverlay.hide();
					}
				} else {
					if ( getOutput() != null && getOutput().getSurface() != null ) {
						getOutput().getSurface().setPaused(previousState);
					}
					if ( overlay != null && layersOverlay != null ) {
						overlay.update();
						layersOverlay.update();
					}
				}
			}
		};
		SwtGui.getWindow().addPerspectiveListener(perspectiveListener);
		return surfaceComposite;
	}

}