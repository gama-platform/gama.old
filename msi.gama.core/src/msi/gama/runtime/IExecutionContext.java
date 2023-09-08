/*******************************************************************************************************
 *
 * IExecutionContext.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import java.util.Map;

import msi.gama.common.interfaces.IDisposable;
import msi.gaml.compilation.ISymbol;

/**
 * The Interface IExecutionContext.
 */
public interface IExecutionContext extends IDisposable {

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	IScope getScope();

	/**
	 * Depth.
	 *
	 * @return the int
	 */
	default int depth() {
		if (getOuterContext() == null) return 0;
		return 1 + getOuterContext().depth();
	}

	/**
	 * Temporary variables, defined in execution contexts. Can be accessed in a recursive way
	 */

	void setTempVar(String name, Object value);

	/**
	 * Gets the temp var.
	 *
	 * @param name
	 *            the name
	 * @return the temp var
	 */
	Object getTempVar(String name);

	/**
	 * Local variables, for example arguments, defined in execution contexts. Are only managed locally
	 */

	Map<? extends String, ? extends Object> getLocalVars();

	/**
	 * Clear local vars.
	 */
	void clearLocalVars();

	/**
	 * Put local var.
	 *
	 * @param varName
	 *            the var name
	 * @param val
	 *            the val
	 */
	void putLocalVar(String varName, Object val);

	/**
	 * Gets the local var.
	 *
	 * @param string
	 *            the string
	 * @return the local var
	 */
	Object getLocalVar(String string);

	/**
	 * Checks for local var.
	 *
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	boolean hasLocalVar(String name);

	/**
	 * Removes the local var.
	 *
	 * @param name
	 *            the name
	 */
	void removeLocalVar(String name);

	/**
	 * Other methods
	 */

	IExecutionContext getOuterContext();

	/**
	 * Creates the copy.
	 *
	 * @return the i execution context
	 */
	IExecutionContext createCopy(ISymbol command);

	/**
	 * Creates the child context.
	 *
	 * @return the i execution context
	 */
	IExecutionContext createChildContext(ISymbol command);

	/**
	 * Gets the current symbol.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the current symbol
	 * @date 3 août 2023
	 */
	ISymbol getCurrentSymbol();

	/**
	 * Sets the symbol.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param statement
	 *            the new symbol
	 * @date 3 août 2023
	 */
	void setCurrentSymbol(ISymbol statement);

}