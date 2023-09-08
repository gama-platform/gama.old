/*******************************************************************************************************
 *
 * IVariable.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/**
	 * Checks if is updatable.
	 *
	 * @return true, if is updatable
	 */
	boolean isUpdatable();

	/**
	 * Checks if is parameter.
	 *
	 * @return true, if is parameter
	 */
	boolean isParameter();

	/**
	 * Checks if is function.
	 *
	 * @return true, if is function
	 */
	boolean isFunction();

	/**
	 * Checks if is micro population.
	 *
	 * @return true, if is micro population
	 */
	boolean isMicroPopulation();

	/**
	 * Initialize with.
	 *
	 * @param scope the scope
	 * @param gamaObject the gama object
	 * @param object the object
	 * @throws GamaRuntimeException the gama runtime exception
	 */
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

	/**
	 * Sets the val.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @param v the v
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	void setVal(IScope scope, IAgent agent, Object v) throws GamaRuntimeException;

	/**
	 * Value.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @return the object
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	Object value(IScope scope, IAgent agent) throws GamaRuntimeException;

	/**
	 * Gets the updated value.
	 *
	 * @param scope the scope
	 * @return the updated value
	 */
	Object getUpdatedValue(final IScope scope);

	/**
	 * Checks if is not modifiable.
	 *
	 * @return true, if is not modifiable
	 */
	boolean isNotModifiable();

}