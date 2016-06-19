/*********************************************************************************************
 *
 *
 * 'ItemList.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
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

	public static final Character ERROR_CODE = '\u00F7';
	public static final Character INFO_CODE = '\u00F8';
	public static final Character WARNING_CODE = '\u00FE';
	public static final Character SEPARATION_CODE = '\u00FF';

	boolean addItem(T obj);

	void removeItem(T obj);

	void pauseItem(T obj);

	void resumeItem(T obj);

	void focusItem(T obj);

	public List<T> getItems();

	String getItemDisplayName(T obj, String previousName);

	void updateItemValues();

	void makeItemSelectable(T data, boolean b);

	void makeItemVisible(T obj, boolean b);

	GamaColor getItemDisplayColor(T data);

	Map<String, Runnable> handleMenu(T data, int x, int y);

}
