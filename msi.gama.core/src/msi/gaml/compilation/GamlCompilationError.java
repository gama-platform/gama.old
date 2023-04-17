/*******************************************************************************************************
 *
 * GamlCompilationError.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * The Class GamlCompilationError. Represents the errors produced by the
 * validation/compilation of IDescription's.
 */
public class GamlCompilationError {

	/** The is warning. */
	protected boolean isWarning = false;
	
	/** The is info. */
	protected boolean isInfo = false;
	
	/** The message. */
	protected final String message;
	
	/** The code. */
	protected String code;
	
	/** The data. */
	protected String[] data;
	
	/** The source. */
	protected EObject source;
	
	/** The uri. */
	protected final URI uri;

	/**
	 * Instantiates a new gaml compilation error.
	 *
	 * @param string the string
	 * @param code the code
	 * @param object the object
	 * @param warning the warning
	 * @param info the info
	 * @param data the data
	 */
	public GamlCompilationError(final String string, final String code, final EObject object, final boolean warning,
			final boolean info, final String... data) {

		message = string;
		isWarning = warning;
		isInfo = info;
		this.code = code;
		this.data = data;
		source = object;
		uri = object.eResource().getURI();
	}

	/**
	 * Instantiates a new gaml compilation error.
	 *
	 * @param string the string
	 * @param code the code
	 * @param uri the uri
	 * @param warning the warning
	 * @param info the info
	 * @param data the data
	 */
	public GamlCompilationError(final String string, final String code, final URI uri, final boolean warning,
			final boolean info, final String... data) {

		message = string;
		isWarning = warning;
		isInfo = info;
		this.code = code;
		this.data = data;
		source = null;
		this.uri = uri;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public String[] getData() {
		return data;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return message;
	}

	/**
	 * Checks if is warning.
	 *
	 * @return true, if is warning
	 */
	public boolean isWarning() {
		return isWarning && !isInfo;
	}

	/**
	 * Checks if is info.
	 *
	 * @return true, if is info
	 */
	public boolean isInfo() {
		return isInfo;
	}

	/**
	 * Gets the statement.
	 *
	 * @return the statement
	 */
	public EObject getStatement() {
		return source;
	}

	/**
	 * Checks if is error.
	 *
	 * @return true, if is error
	 */
	public boolean isError() {
		return !isInfo && !isWarning;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GamlCompilationError)) {
			return false;
		}
		final GamlCompilationError error = (GamlCompilationError) other;
		return message.equals(error.message) && source == error.source;
	}

	@Override
	public int hashCode() {
		return message.hashCode() + (source == null ? 0 : source.hashCode());
	}
}
