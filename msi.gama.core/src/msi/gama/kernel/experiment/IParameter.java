/*******************************************************************************************************
 *
 * IParameter.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 4 juin 2010
 *
 * @todo Description
 *
 */
public interface IParameter extends IExperimentDisplayable {

	/**
	 * The listener interface for receiving parameterChange events. The class that is interested in processing a
	 * parameterChange event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addParameterChangeListener<code> method. When the parameterChange event
	 * occurs, that object's appropriate method is invoked.
	 *
	 * @see ParameterChangeEvent
	 */
	public interface ParameterChangeListener {

		/**
		 * Changed.
		 *
		 * @param scope
		 *            the scope
		 * @param newValue
		 *            the new value
		 */
		void changed(IScope scope, Object newValue);
	}

	/** The empty strings. */
	String[] EMPTY_STRINGS = {};

	/** The empty strings. */
	String[] SWITCH_STRINGS = { "True", "False" };

	/**
	 * Sets the value.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	void setValue(IScope scope, Object value);

	/**
	 * Sets the value no check no notification.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param value
	 *            the new value no check no notification
	 * @date 13 août 2023
	 */
	void setValueNoCheckNoNotification(Object value);

	/**
	 * Value.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	Object value(IScope scope) throws GamaRuntimeException;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@SuppressWarnings ("rawtypes")
	IType getType();

	/**
	 * Serialize.
	 *
	 * @param includingBuiltIn
	 *            the including built in
	 * @return the string
	 */
	@Override
	String serialize(boolean includingBuiltIn);

	/**
	 * Gets the initial value.
	 *
	 * @param scope
	 *            the scope
	 * @return the initial value
	 */
	Object getInitialValue(IScope scope);

	/**
	 * Gets the min value.
	 *
	 * @param scope
	 *            the scope
	 * @return the min value
	 */
	Object getMinValue(IScope scope);

	/**
	 * Gets the max value.
	 *
	 * @param scope
	 *            the scope
	 * @return the max value
	 */
	Object getMaxValue(IScope scope);

	/**
	 * Gets the among value.
	 *
	 * @param scope
	 *            the scope
	 * @return the among value
	 */
	@SuppressWarnings ("rawtypes")
	List getAmongValue(IScope scope);

	/**
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	boolean isEditable();

	/**
	 * Accepts slider.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	boolean acceptsSlider(IScope scope);

	/**
	 * Gets the step value.
	 *
	 * @param scope
	 *            the scope
	 * @return the step value
	 */
	Comparable getStepValue(IScope scope);

	/**
	 * Checks if is defined.
	 *
	 * @return true, if is defined
	 */
	boolean isDefined();

	/**
	 * Gets the disablement. Only valid for boolean parameters. The list of parameter names that setting this parameter
	 * to true will enable
	 *
	 * @return the enablement
	 */
	@Nonnull
	default String[] getEnablement() { return EMPTY_STRINGS; }

	/**
	 * Gets the disablement. Only valid for boolean parameters. The list of parameter names that setting this parameter
	 * to true will disable
	 *
	 * @return the disablement
	 */
	@Nonnull
	default String[] getDisablement() { return EMPTY_STRINGS; }

	/**
	 * Gets the refreshment. The list of parameter names that a change of value of this parameter will refresh
	 * (recomputing the value and the lists of possible values)
	 *
	 * @return the refreshment
	 */
	@Nonnull
	default String[] getRefreshment() { return EMPTY_STRINGS; }

	/**
	 * Gets the file extensions.
	 *
	 * @return the file extensions
	 */
	@Nonnull
	default String[] getFileExtensions() { return EMPTY_STRINGS; }

	/**
	 * Adds the changed listener.
	 *
	 * @param listener
	 *            the listener
	 */
	default void addChangedListener(final ParameterChangeListener listener) {
		// Nothing to do by default
	}

	/**
	 * The Interface Batch.
	 */
	public interface Batch extends IParameter {

		/**
		 * Value.
		 *
		 * @return the object
		 */
		Object value();

		/**
		 * Sets the category.
		 *
		 * @param cat
		 *            the new category
		 */
		void setCategory(String cat);

		/**
		 * Reinit randomly.
		 *
		 * @param scope
		 *            the scope
		 */
		void reinitRandomly(IScope scope);

		/**
		 * Neighbor values.
		 *
		 * @param scope
		 *            the scope
		 * @return the sets the
		 * @throws GamaRuntimeException
		 *             the gama runtime exception
		 */
		Set<Object> neighborValues(IScope scope) throws GamaRuntimeException;

		/**
		 * Sets the editable.
		 *
		 * @param b
		 *            the new editable
		 */
		void setEditable(boolean b);

		/**
		 * Can be explored.
		 *
		 * @return true, if successful
		 */
		boolean canBeExplored();

	}

	/**
	 * Only valid for file parameters. Tells whether it is to be restricted to the workspace or nos
	 *
	 * @return true, if is workspace
	 */
	default boolean isWorkspace() { return false; }

	/**
	 * @param b
	 */
	void setDefined(boolean b);

	/**
	 * Gets the labels.
	 *
	 * @param scope
	 *            the scope
	 * @return the labels
	 */
	default String[] getLabels(final IScope scope) {
		return SWITCH_STRINGS;
	}

	/**
	 * Allows to know if the value of the parameter should be interpreted or kept as an expression
	 *
	 * @return
	 */
	default boolean isExpression() { return false; }

}
