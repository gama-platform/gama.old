/**
 * Created by drogoul, 5 avr. 2012
 * 
 */
package msi.gama.common.util;

import msi.gaml.compilation.GamlCompilationError;

/**
 * The class IErrorCollector.
 * 
 * @author drogoul
 * @since 5 avr. 2012
 * 
 */
public interface IErrorCollector {

	public abstract void add(final GamlCompilationError error);

	/**
	 * @return
	 */
	// public abstract boolean hasErrors();

}