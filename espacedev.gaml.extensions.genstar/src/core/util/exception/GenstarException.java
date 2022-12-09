/*******************************************************************************************************
 *
 * GenstarException.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util.exception;

/**
 * The Class GenstarException.
 */
public class GenstarException extends RuntimeException {

	/**
	 * Instantiates a new genstar exception.
	 *
	 * @param string
	 *            the string
	 */
	public GenstarException(final String string) {
		super(string);
	}

	/**
	 * Instantiates a new genstar exception.
	 *
	 * @param string
	 *            the string
	 * @param e1
	 *            the e 1
	 */
	public GenstarException(final String string, final Exception e1) {
		super(string, e1);
	}

	/**
	 * Instantiates a new genstar exception.
	 */
	public GenstarException() {
		this("Exception in Genstar");
	}

	/**
	 * Instantiates a new genstar exception.
	 *
	 * @param e
	 *            the e
	 */
	public GenstarException(final Exception e) {
		this("Exception in Genstar", e);
	}

}
