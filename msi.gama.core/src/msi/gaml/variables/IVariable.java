/*******************************************************************************************************
 *
 * msi.gaml.variables.IVariable.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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

	boolean isUpdatable();

	boolean isParameter();

	boolean isFunction();

	boolean isMicroPopulation();

	boolean isConst();

	void initializeWith(IScope scope, IAgent gamaObject, Object object) throws GamaRuntimeException;

	void setVal(IScope scope, IAgent agent, Object v) throws GamaRuntimeException;

	Object value(IScope scope, IAgent agent) throws GamaRuntimeException;

	Object getUpdatedValue(final IScope scope);

	boolean isNotModifiable();

}