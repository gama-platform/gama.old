/*******************************************************************************************************
 *
 * ItemList.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.util.List;
import java.util.Map;

import msi.gama.util.GamaColor;

/**
 * Written by drogoul Modified on 13 mai 2011
 *
 * @todo Description
 *
 */
public interface ItemList<T> {

	/** The Constant ERROR_CODE. */
	Character ERROR_CODE = '\u00F7';

	/** The Constant INFO_CODE. */
	Character INFO_CODE = '\u00F8';

	/** The Constant WARNING_CODE. */
	Character WARNING_CODE = '\u00FE';

	/** The Constant SEPARATION_CODE. */
	Character SEPARATION_CODE = '\u00FF';

	/**
	 * Adds the item.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	boolean addItem(T obj);

	/**
	 * Removes the item.
	 *
	 * @param obj
	 *            the obj
	 */
	default void removeItem(final T obj) {}

	/**
	 * Pause item.
	 *
	 * @param obj
	 *            the obj
	 */
	default void pauseItem(final T obj) {}

	/**
	 * Resume item.
	 *
	 * @param obj
	 *            the obj
	 */
	default void resumeItem(final T obj) {}

	/**
	 * Focus item.
	 *
	 * @param obj
	 *            the obj
	 */
	default void focusItem(final T obj) {}

	/**
	 * Gets the items.
	 *
	 * @return the items
	 */
	List<T> getItems();

	/**
	 * Gets the item display name.
	 *
	 * @param obj
	 *            the obj
	 * @param previousName
	 *            the previous name
	 * @return the item display name
	 */
	String getItemDisplayName(T obj, String previousName);

	/**
	 * Update item values.
	 */
	default void updateItemValues() {}

	/**
	 * Make item selectable.
	 *
	 * @param data
	 *            the data
	 * @param b
	 *            the b
	 */
	default void makeItemSelectable(final T data, final boolean b) {}

	/**
	 * Make item visible.
	 *
	 * @param obj
	 *            the obj
	 * @param b
	 *            the b
	 */
	default void makeItemVisible(final T obj, final boolean b) {}

	/**
	 * Checks if is item visible.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if is item visible
	 */
	default boolean isItemVisible(final T obj) {
		return true;
	}

	/**
	 * Gets the item display color.
	 *
	 * @param data
	 *            the data
	 * @return the item display color
	 */
	default GamaColor getItemDisplayColor(final T data) {
		return null;
	}

	/**
	 * Handle menu.
	 *
	 * @param data
	 *            the data
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the map
	 */
	default Map<String, Runnable> handleMenu(final T data, final int x, final int y) {
		return null;
	}

}
