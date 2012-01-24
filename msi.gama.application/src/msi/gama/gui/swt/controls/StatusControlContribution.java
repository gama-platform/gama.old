/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.simulation.SimulationClock;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class StatusControlContribution extends WorkbenchWindowControlContribution implements
	MouseListener {

	Composite compo;
	Label label;

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
		compo = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 2;
		compo.setLayout(layout);
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = 200;
		label = new Label(compo, SWT.CENTER);
		label.setLayoutData(data);
		label.setBackground(SwtGui.COLOR_OK);
		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		label.setText("No simulation running");
		label.addMouseListener(this);
		SwtGui.setStatusControl(this);
		return compo;
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent e) {}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseDown(final MouseEvent e) {
		SimulationClock.toggleDisplay();
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	@Override
	public void mouseUp(final MouseEvent e) {}

	/**
	 * @return
	 */
	public boolean isDisposed() {
		return label.isDisposed();
	}

	/**
	 * @return
	 */
	public Color getBackground() {
		return label.getBackground();
	}

	/**
	 * @param color
	 */
	public void setBackground(final Color color) {
		label.setBackground(color);
	}

	/**
	 * @param message
	 */
	public void setText(final String message) {
		label.setText(message);
	}

}
