/*******************************************************************************************************
 *
 * msi.gama.runtime.exceptions.GamaStartupException.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
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
