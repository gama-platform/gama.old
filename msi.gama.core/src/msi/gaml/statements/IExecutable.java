/**
 * Created by drogoul, 20 août 2013
 * 
 */
package msi.gaml.statements;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Class IExecutable.
 * 
 * @author drogoul
 * @since 20 août 2013
 * 
 */
public interface IExecutable {

	public abstract Object executeOn(final IScope scope) throws GamaRuntimeException;
}
