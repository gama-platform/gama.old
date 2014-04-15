/*********************************************************************************************
 * 
 *
 * 'IPopupProvider.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Shell;

/**
 * The class IPopupProvider.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public interface IPopupProvider {

	public String getPopupText();

	public Shell getControllingShell();

	public Color getPopupBackground();

	public Point getAbsoluteOrigin();

}
