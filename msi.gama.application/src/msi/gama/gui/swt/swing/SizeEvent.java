/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 *******************************************************************************/
package msi.gama.gui.swt.swing;

import java.util.EventObject;
import org.eclipse.swt.graphics.Point;

/**
 * Instances of this class are sent as a result of
 * size changes for an embedded Swing control
 *
 * @see SizeListener
 */
public class SizeEvent extends EventObject {

	/** the minimum size for this control, as reported by AWT. */
	public final Point minimum;
	/** the preferred size for this control, as reported by AWT. */
	public final Point preferred;
	/** the maximum size for this control, as reported by AWT. */
	public final Point maximum;

	public SizeEvent(final SwingControl source, final Point min, final Point pref, final Point max) {
		super(source);
		minimum = min;
		preferred = pref;
		maximum = max;
	}

	/**
	 * Returns the SwingControl that is the source of this event
	 * @return SwingControl
	 */
	public SwingControl getSwingControl() {
		return (SwingControl) source;
	}

	/**
	 * Returns the name of the event. This is the name of
	 * the class without the package name.
	 *
	 * @return the name of the event
	 */
	protected String getName() {
		String string = getClass().getName();
		int index = string.lastIndexOf('.');
		if ( index == -1 ) { return string; }
		return string.substring(index + 1, string.length());
	}

	/**
	 * Returns a string containing a concise, human-readable
	 * description of the receiver.
	 *
	 * @return a string representation of the event
	 */
	@Override
	public String toString() {
		return getName() + "{" + source //$NON-NLS-1$
			+ " min=" + minimum //$NON-NLS-1$
			+ " pref=" + preferred //$NON-NLS-1$
			+ " max=" + maximum //$NON-NLS-1$
			+ "}"; //$NON-NLS-1$
	}
}
