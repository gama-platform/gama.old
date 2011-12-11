/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.views;

import msi.gama.gui.application.GUI;
import org.eclipse.swt.*;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;

public class BrowserView extends ViewPart implements IPartListener {

	public static final String ID = "gama.view.browser";

	private Browser browser = null;

	@Override
	public void createPartControl(final Composite parent) {

		try {
			browser = new Browser(parent, SWT.NONE);
		} catch (final SWTError e) {
			GUI.error("Documentation cannot be shown.");
		}
	}

	@Override
	public void setFocus() {

		if ( browser != null ) {
			browser.setFocus();
		}
	}

	public void display(final String html_file) {
		GUI.run(new Runnable() {

			@Override
			public void run() {
				browser.setUrl(html_file);
			}
		});

	}

	@Override
	public void partActivated(final IWorkbenchPart part) {

	}

	@Override
	public void partBroughtToTop(final IWorkbenchPart part) {

	}

	@Override
	public void partClosed(final IWorkbenchPart part) {}

	@Override
	public void partDeactivated(final IWorkbenchPart part) {

	}

	@Override
	public void partOpened(final IWorkbenchPart part) {

	}

	@Override
	public void dispose() {
		if ( browser != null ) {
			browser.dispose();
			browser = null;
		}
		super.dispose();
	}
}
