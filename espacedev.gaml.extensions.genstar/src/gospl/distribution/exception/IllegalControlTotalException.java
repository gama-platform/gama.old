/*******************************************************************************************************
 *
 * IllegalControlTotalException.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution.exception;

import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;

/**
 * The Class IllegalControlTotalException.
 */
public class IllegalControlTotalException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new illegal control total exception.
	 *
	 * @param control the control
	 * @param controlAtt the control att
	 */
	public IllegalControlTotalException(final AControl<? extends Number> control,
			final AControl<? extends Number> controlAtt) {
		super("Two " + AControl.class.getSimpleName() + " are incompatible: " + control + " & " + controlAtt);
	}

	/**
	 * Instantiates a new illegal control total exception.
	 *
	 * @param message the message
	 * @param matrix the matrix
	 */
	public IllegalControlTotalException(final String message, final AFullNDimensionalMatrix<? extends Number> matrix) {
		super(message + "\n" + matrix.toString());
	}

}
