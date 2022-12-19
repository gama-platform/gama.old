/*******************************************************************************************************
 *
 * DeserializationException.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.file.json;

/**
 * DeserializationException explains how and where the problem occurs in the source JSON text during deserialization.
 * 
 * @since 2.0.0
 */
public class DeserializationException extends Exception {
	/** The kinds of exceptions that can trigger a DeserializationException. */
	public enum Problems {
		
		/** The disallowed token. */
		@SuppressWarnings ("javadoc")
		DISALLOWED_TOKEN,
		/** @since 2.3.0 to consolidate exceptions that occur during deserialization. */
		IOEXCEPTION,
		
		/** The unexpected character. */
		@SuppressWarnings ("javadoc")
		UNEXPECTED_CHARACTER,
		
		/** The unexpected exception. */
		@SuppressWarnings ("javadoc")
		UNEXPECTED_EXCEPTION,
		
		/** The unexpected token. */
		@SuppressWarnings ("javadoc")
		UNEXPECTED_TOKEN;
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The position. */
	private final int position;
	
	/** The problem type. */
	private final Problems problemType;
	
	/** The unexpected object. */
	private final Object unexpectedObject;

	/**
	 * Instantiates a DeserializationException without assumptions.
	 * 
	 * @param position
	 *            where the exception occurred.
	 * @param problemType
	 *            how the exception occurred.
	 * @param unexpectedObject
	 *            what caused the exception.
	 */
	public DeserializationException(final int position, final Problems problemType, final Object unexpectedObject) {
		this.position = position;
		this.problemType = problemType;
		this.unexpectedObject = unexpectedObject;
		if (Problems.IOEXCEPTION.equals(problemType) || Problems.UNEXPECTED_EXCEPTION.equals(problemType)) {
			if (unexpectedObject instanceof Throwable) {
				this.initCause((Throwable) unexpectedObject);
			}
		}
	}

	@Override
	public String getMessage() {
		final StringBuilder sb = new StringBuilder();
		switch (this.problemType) {
			case DISALLOWED_TOKEN:
				sb.append("The disallowed token (").append(this.unexpectedObject).append(") was found at position ")
						.append(this.position)
						.append(". If this is in error, try again with a parse that allows the token instead. Otherwise, fix the parsable string and try again.");
				break;
			case IOEXCEPTION:
				sb.append(
						"An IOException was encountered, ensure the reader is properly instantiated, isn't closed, or that it is ready before trying again.\n")
						.append(this.unexpectedObject);
				break;
			case UNEXPECTED_CHARACTER:
				sb.append("The unexpected character (").append(this.unexpectedObject).append(") was found at position ")
						.append(this.position).append(". Fix the parsable string and try again.");
				break;
			case UNEXPECTED_TOKEN:
				sb.append("The unexpected token ").append(this.unexpectedObject).append(" was found at position ")
						.append(this.position).append(". Fix the parsable string and try again.");
				break;
			case UNEXPECTED_EXCEPTION:
				sb.append(
						"Please report this to the library's maintainer. The unexpected exception that should be addressed before trying again occurred at position ")
						.append(this.position).append(":\n").append(this.unexpectedObject);
				break;
			default:
				sb.append("Please report this to the library's maintainer. An error at position ").append(this.position)
						.append(" occurred. There are no recovery recommendations available.");
				break;
		}
		return sb.toString();
	}

	/**
	 * Helps debug the location of a problem.
	 * 
	 * @return an index of the string character the error type occurred at.
	 */
	public int getPosition() {
		return this.position;
	}

	/**
	 * Helps find an appropriate solution for a problem.
	 * 
	 * @return the enumeration for how the exception occurred.
	 */
	public Problems getProblemType() {
		return this.problemType;
	}

	/**
	 * Helps identify the problem.
	 * 
	 * @return a representation of what caused the exception.
	 */
	public Object getUnexpectedObject() {
		return this.unexpectedObject;
	}
}
