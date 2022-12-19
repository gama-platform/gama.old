/*******************************************************************************************************
 *
 * InvalidSurveyFormatException.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.io.exception;

import java.util.List;

/**
 * The Class InvalidSurveyFormatException.
 */
public class InvalidSurveyFormatException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid survey format exception.
	 *
	 * @param fileName the file name
	 * @param supportedFileFormat the supported file format
	 */
	public InvalidSurveyFormatException(final String fileName, final List<String> supportedFileFormat) {
		super("file " + fileName + " is not a valide file type which are " + supportedFileFormat.toString());
	}

}
