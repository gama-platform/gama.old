/*******************************************************************************************************
 *
 * msi.gaml.variables.IVariable.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	void initializeWith(IScope scope, IAgent gamaObject, Object object) throws GamaRuntimeException;

	/**
	 * Can be called on this variable to indicate that the value it represents has been changed outside. For instance,
	 * if agent.setLocation(...) has been invoked, the value of location will change, but no listeners (see
	 * GamaAnnotations.listener.class) will be notified and the action attached to the on_change: facet will not be run
	 * as well. In the core, this represents a small set of variables (location, shape, name...) that can be modified
	 * outside of the models. Plugins may have more variables, although they are expected to produce listeners instead
	 * (e.g. listening to the changes of the location of an agent can be important for some). As soon as a variable is
	 * asked to produce notifications this way, it automatically blocks internal notifications (so as to avoid double
	 * notifications, one from the agent whose location is manipulated and one from the variable itself if it is
	 * modified in a model).
	 *
	 * @param scope
	 *            the current scope
	 * @param agent
	 *            the agent concerned by this change
	 * @param oldValue
	 *            previous value of the variable. Not used for the moment
	 * @param newValue
	 *            new value, once it has been set
	 */
	void notifyOfValueChange(final IScope scope, final IAgent agent, final Object oldValue, final Object newValue);

	void setVal(IScope scope, IAgent agent, Object v) throws GamaRuntimeException;

	Object value(IScope scope, IAgent agent) throws GamaRuntimeException;

	Object getUpdatedValue(final IScope scope);

	boolean isNotModifiable();

}