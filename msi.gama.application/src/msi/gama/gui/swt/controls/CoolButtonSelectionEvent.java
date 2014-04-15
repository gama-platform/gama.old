/*********************************************************************************************
 * 
 *
 * 'CoolButtonSelectionEvent.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import org.eclipse.swt.events.TypedEvent;

/**
 * @author Code Crofter
 *         On behalf Polymorph Systems
 * 
 * @since RCP Toolbox v0.1 <br>
 */
public class CoolButtonSelectionEvent extends TypedEvent {

	private static final long serialVersionUID = 1L;

	// private final int x;
	// private final int y;

	public CoolButtonSelectionEvent(final Object source, final int x, final int y) {
		super(source);
		// this.x = x;
		// this.y = y;
	}
}
