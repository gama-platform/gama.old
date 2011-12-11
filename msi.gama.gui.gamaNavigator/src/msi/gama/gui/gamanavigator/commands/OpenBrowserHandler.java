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
package msi.gama.gui.gamanavigator.commands;

import java.io.File;
import msi.gama.gui.gamanavigator.FileBean;
import org.eclipse.core.commands.*;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

public class OpenBrowserHandler extends AbstractHandler {

	ISelection activeSelection;
	StructuredSelection currentSelection = null;

	/**
	 * Handler is enabled if the current selection in the navigator is an html file.
	 */
	@Override
	public boolean isEnabled() {
		/* Get the selection */
		try {
			IWorkbench w = PlatformUI.getWorkbench();
			if ( w == null ) { return false; }
			IWorkbenchWindow ww = w.getActiveWorkbenchWindow();
			if ( ww == null ) { return false; }
			ISelectionService s = ww.getSelectionService();
			if ( s == null ) { return false; }
			activeSelection = s.getSelection();
			if ( activeSelection == null ) { return false; }
			currentSelection = new StructuredSelection(((TreeSelection) activeSelection).toArray());
			if ( !currentSelection.isEmpty() &&
				currentSelection.getFirstElement().toString().endsWith(".html") ) { return true; }
			return false;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		Display display = PlatformUI.getWorkbench().getDisplay();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM);
		shell.setLayout(new FillLayout());
		Browser browser = new Browser(shell, SWT.NONE);
		browser.addTitleListener(new TitleListener() {

			@Override
			public void changed(final TitleEvent event) {
				shell.setText(event.title);
			}
		});
		browser.setBounds(0, 0, 800, 600);
		shell.pack();
		shell.open();

		Object selection = ((IStructuredSelection) currentSelection).getFirstElement();
		if ( selection instanceof FileBean ) {
			FileBean file = (FileBean) ((IStructuredSelection) currentSelection).getFirstElement();
			browser.setUrl(file.getPath());
		} else if ( selection instanceof File ) {
			File file = (File) ((IStructuredSelection) currentSelection).getFirstElement();
			browser.setUrl(file.getPath());
		} else {
			IResource resource =
				(IResource) ((IStructuredSelection) currentSelection).getFirstElement();
			browser.setUrl(resource.getLocation().toString());
		}

		while (!shell.isDisposed()) {
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}

		return null;
	}

}
