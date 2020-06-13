/*******************************************************************************************************
 *
 * msi.gama.common.preferences.IPreferenceChangeListener.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.preferences;

public interface IPreferenceChangeListener<T> {

	public interface IPreferenceAfterChangeListener<T> extends IPreferenceChangeListener<T> {
		/**
		 * Only listens to values after they changed.
		 */
		@Override
		default boolean beforeValueChange(final T newValue) {
			return true;
		}

	}

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