/**
 * Created by drogoul, 26 févr. 2012
 * 
 */
package msi.gama.runtime.exceptions;

/**
 * The class GamaStartupException.
 * 
 * @author drogoul
 * @since 26 févr. 2012
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
