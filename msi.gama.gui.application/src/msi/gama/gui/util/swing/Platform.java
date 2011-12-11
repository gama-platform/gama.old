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
