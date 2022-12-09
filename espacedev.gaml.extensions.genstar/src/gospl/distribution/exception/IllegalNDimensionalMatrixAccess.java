/*******************************************************************************************************
 *
 * IllegalNDimensionalMatrixAccess.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution.exception;

/**
 * The Class IllegalNDimensionalMatrixAccess.
 */
public class IllegalNDimensionalMatrixAccess extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new illegal N dimensional matrix access.
	 *
	 * @param message the message
	 */
	public IllegalNDimensionalMatrixAccess(final String message) {
		super(message);
	}

}
