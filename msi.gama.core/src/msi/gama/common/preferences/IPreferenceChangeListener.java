/*******************************************************************************************************
 *
 * IPreferenceChangeListener.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.preferences;

/**
 * The listener interface for receiving IPreferenceChange events.
 * The class that is interested in processing a IPreferenceChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addIPreferenceChangeListener<code> method. When
 * the IPreferenceChange event occurs, that object's appropriate
 * method is invoked.
 *
 * @param <T> the generic type
 * @see IPreferenceChangeEvent
 */
public interface IPreferenceChangeListener<T> {

	/**
	 * The listener interface for receiving IPreferenceAfterChange events.
	 * The class that is interested in processing a IPreferenceAfterChange
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addIPreferenceAfterChangeListener<code> method. When
	 * the IPreferenceAfterChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @param <T> the generic type
	 * @see IPreferenceAfterChangeEvent
	 */
	public interface IPreferenceAfterChangeListener<T> extends IPreferenceChangeListener<T> {
		/**
		 * Only listens to values after they changed.
		 */
		@Override
		default boolean beforeValueChange(final T newValue) {
			return true;
		}

	}

	/**
	 * The listener interface for receiving IPreferenceBeforeChange events.
	 * The class that is interested in processing a IPreferenceBeforeChange
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addIPreferenceBeforeChangeListener<code> method. When
	 * the IPreferenceBeforeChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @param <T> the generic type
	 * @see IPreferenceBeforeChangeEvent
	 */
	public interface IPreferenceBeforeChangeListener<T> extends IPreferenceChangeListener<T> {
		/**
		 * Only listens to values before they change to be able to veto the change
		 */

		@Override
		default void afterValueChange(final T newValue) {}

	}

	/**
	 * A change listener, that receives the beforeValueChange() message before the preference is assigned a new value,
	 * with this value in parameter. Returning true will enable the change, returning false will veto it.
	 * 
	 * @param newValue,
	 *            the new value set to this preference
	 * @return true or false, whether or not the change is accepted by the listener.
	 */
	public boolean beforeValueChange(T newValue);

	/**
	 * A change listener, that receives the afterValueChange() message after the preference is assigned a new value,
	 * with this value in parameter, in order to perform anything needed for this change.
	 * 
	 * @param newValue,
	 *            the new value set to this preference
	 * @return true or false, whether or not the change is accepted by the listener.
	 */
	public void afterValueChange(T newValue);
}