/*******************************************************************************************************
 *
 * WebbException.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.webb;

/**
 * Runtime exception wrapping the real exception thrown by HttpUrlConnection et al.
 *
 * @author hgoebl
 */
public class WebbException extends RuntimeException {

	/** The response. */
	private Response<?> response;

	/**
	 * Instantiates a new webb exception.
	 *
	 * @param message
	 *            the message
	 */
	public WebbException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new webb exception.
	 *
	 * @param message
	 *            the message
	 * @param response
	 *            the response
	 */
	public WebbException(final String message, final Response<?> response) {
		super(message);
		this.response = response;
	}

	/**
	 * Instantiates a new webb exception.
	 *
	 * @param cause
	 *            the cause
	 */
	public WebbException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Get the Response object (only available if exception has been raised by
	 * {@link com.goebl.david.Request#ensureSuccess()}.
	 *
	 * @return the <code>Response</code> object filled with error information like statusCode and errorBody.
	 */
	public Response<?> getResponse() { return response; }
}
