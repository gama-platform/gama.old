/*******************************************************************************************************
 *
 * msi.gama.runtime.IExecutionContext.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime;

import java.util.Map;

public interface IExecutionContext {

	public IScope getScope();

	public default int depth() {
		if (getOuterContext() == null)
			return 0;
		return 1 + getOuterContext().depth();
	}

	/**
	 * Temporary variables, defined in execution contexts. Can be accessed in a recursive way
	 */

	public abstract void setTempVar(String name, Object value);

	public abstract Object getTempVar(String name);

	/**
	 * Local variables, for example arguments, defined in execution contexts. Are only managed locally
	 */

	public abstract Map<? extends String, ? extends Object> getLocalVars();

	public abstract void clearLocalVars();

	public abstract void putLocalVar(String varName, Object val);

	public abstract Object getLocalVar(String string);

	public abstract boolean hasLocalVar(String name);

	public abstract void removeLocalVar(String name);

	/**
	 * Other methods
	 */

	public abstract IExecutionContext getOuterContext();

	public abstract IExecutionContext createCopyContext();

	public abstract IExecutionContext createChildContext();

}