/*******************************************************************************************************
 *
 * ParseException.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

/**
 * An unchecked exception to indicate that an input does not qualify as valid JSON.
 */
@SuppressWarnings ("serial") // use default serial UID
public class ParseException extends RuntimeException {

	/** The location. */
	private final Location location;

	/**
	 * Instantiates a new parses the exception.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param message
	 *            the message
	 * @param location
	 *            the location
	 * @date 29 oct. 2023
	 */
	ParseException(final String message, final Location location) {
		super(message + " at " + location);
		this.location = location;
	}

	/**
	 * Returns the location at which the error occurred.
	 *
	 * @return the error location
	 */
	public Location getLocation() { return location; }

}
