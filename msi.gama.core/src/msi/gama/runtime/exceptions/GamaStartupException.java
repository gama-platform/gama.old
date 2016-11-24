/*********************************************************************************************
 *
 * 'GamaStartupException.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.runtime.exceptions;

/**
 * The class GamaStartupException.
 * 
 * @author drogoul
 * @since 26 f�vr. 2012
 * 
 */
public class GamaStartupException extends Exception {

	public GamaStartupException(final Throwable ex) {
		super(ex.toString(), ex);
	}

	public GamaStartupException(final String s) {
		super(s);
	}

	public GamaStartupException(final String string, final Throwable e) {
		super(string + ": " + e.toString(), e);
	}

}
