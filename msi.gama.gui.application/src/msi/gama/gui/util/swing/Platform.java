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
package msi.gama.gui.util.swing;

import org.eclipse.swt.SWT;

/**
 * The Class Platform.
 */
class Platform {

	/** The platform string. */
	private static String platformString = SWT.getPlatform();

	// prevent instantiation
	/**
	 * Instantiates a new platform.
	 */
	private Platform() {
	}

	/**
	 * Checks if is win32.
	 * 
	 * @return true, if is win32
	 */
	public static boolean isWin32() {
		return "win32".equals(platformString); //$NON-NLS-1$
	}

	/**
	 * Checks if is gtk.
	 * 
	 * @return true, if is gtk
	 */
	public static boolean isGtk() {
		return "gtk".equals(platformString); //$NON-NLS-1$
	}

}
