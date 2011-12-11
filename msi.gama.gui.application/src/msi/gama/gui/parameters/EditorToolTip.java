/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gama.gui.parameters;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

/**
 * Written by drogoul Modified on 2 juin 2011
 * 
 * @todo Description
 * 
 */
public class EditorToolTip extends DefaultToolTip {

	public EditorToolTip(final Control control, final int recreate, final boolean b) {
		super(control, recreate, b);
		setShift(new Point(0, 0));
		setPopupDelay(0);
		setHideDelay(0);
		setHideOnMouseDown(false);
		setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
	}

	@Override
	protected boolean shouldCreateToolTip(final Event event) {
		return true;
	}

	public void show() {

	}

}
