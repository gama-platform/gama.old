/**
 * 
 */
package msi.gama.kernel.exceptions;

/**
 * Written by drogoul
 * Modified on 8 nov. 2011
 * 
 * @todo Description
 * 
 */
public class GamaRuntimeWarning extends GamaRuntimeException {

	/**
	 * Instantiates a new warning.
	 * 
	 * @param s the message of the warning
	 */
	public GamaRuntimeWarning(final String s) {
		super(s, true);
	}

}
