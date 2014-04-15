/*********************************************************************************************
 * 
 *
 * 'BrowserView.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

/**
 * Class BrowserView.
 * 
 * @author drogoul
 * @since 3 avr. 2014
 * 
 */
public class BrowserView extends GamaViewPart {

	public static final String ID = "gama.browser.view";

	Browser browser;

	public BrowserView() {}

	@Override
	public void ownCreatePartControl(final Composite shell) {
		browser = new Browser(shell, SWT.NONE);
		browser.setUrl("https://code.google.com/p/gama-platform/");
	}

	@Override
	public void setFocus() {}

	@Override
	public void activateContext() {}

	public Browser getBrowser() {
		return browser;
	}

	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { BROWSER_BACK, BROWSER_FORWARD, SEP, BROWSER_STOP, SEP, BROWSER_HOME };
	}

}
