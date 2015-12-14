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
package msi.gama.display;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.LayeredDisplayView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class WebDisplayView extends LayeredDisplayView {

	public static final String ID = GuiUtils.WEB_VIEW_ID;

	@Override
	protected Composite createSurfaceComposite() {
		surfaceComposite = new Browser(parent, SWT.NONE);

		return surfaceComposite;
	}

	/**
	 * Method zoomWhenScrolling()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Zoomable#zoomWhenScrolling()
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return false;
	}

}