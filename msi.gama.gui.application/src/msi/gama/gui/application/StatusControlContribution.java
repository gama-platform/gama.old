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
package msi.gama.gui.application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class StatusControlContribution extends WorkbenchWindowControlContribution {

	public StatusControlContribution() {}

	public StatusControlContribution(final String id) {
		super(id);
	}

	//
	@Override
	protected int computeWidth(final Control control) {
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	@Override
	protected Control createControl(final Composite parent) {
		final Composite c = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 4;
		c.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		Label l = new Label(c, SWT.CENTER);
		l.setLayoutData(data);
		l.setBackground(GUI.COLOR_OK);
		l.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		// l.setSize(200, 20);
		l.setText("No simulation running");
		// l.pack();
		GUI.setStatusControl(l);
		return c;
	}

}
