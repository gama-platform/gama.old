/*******************************************************************************************************
 *
 * msi.gama.runtime.IExecutionContext.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import java.util.Map;

import msi.gama.common.interfaces.IDisposable;

public interface IExecutionContext extends IDisposable {

	IScope getScope();

	default int depth() {
		if (getOuterContext() == null) { return 0; }
		return 1 + getOuterContext().depth();
	}

	/**
	 * Temporary variables, defined in execution contexts. Can be accessed in a recursive way
	 */

	void setTempVar(String name, Object value);

	Object getTempVar(String name);

	/**
	 * Local variables, for example arguments, defined in execution contexts. Are only managed locally
	 */

	Map<? extends String, ? extends Object> getLocalVars();

	void clearLocalVars();

	void putLocalVar(String varName, Object val);

	Object getLocalVar(String string);

	boolean hasLocalVar(String name);

	void removeLocalVar(String name);

	/**
	 * Other methods
	 */

	IExecutionContext getOuterContext();

	IExecutionContext createCopy();

	IExecutionContext createChildContext();

}