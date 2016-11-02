/*********************************************************************************************
 *
 * 'IVariable.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.variables;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * @author drogoul
 */
public interface IVariable extends ISymbol, IParameter {

	public abstract boolean isUpdatable();

	public abstract boolean isParameter();

	public abstract boolean isConst();

	public abstract void initializeWith(IScope scope, IAgent gamaObject, Object object) throws GamaRuntimeException;

	// public abstract void updateFor(IScope scope, IAgent agent) throws GamaRuntimeException;

	public abstract void setVal(IScope scope, IAgent agent, Object v) throws GamaRuntimeException;

	public abstract Object value(IScope scope, IAgent agent) throws GamaRuntimeException;

	public abstract Object getUpdatedValue(final IScope scope);
}