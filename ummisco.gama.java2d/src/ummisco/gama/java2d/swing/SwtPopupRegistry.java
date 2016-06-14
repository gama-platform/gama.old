/*******************************************************************************
 * Copyright (c) 2005-2008 SAS Institute Inc., ILOG S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * SAS Institute Inc. - initial API and implementation
 * ILOG S.A. - initial API and implementation
 *******************************************************************************/
package ummisco.gama.java2d.swing;

import java.util.WeakHashMap;
import org.eclipse.swt.widgets.*;

/**
 * This class allows you to register SWT popup menus on AWT/Swing components.
 * <p>
 * It is customizable through the "replaceable singleton" design pattern.
 * @see #setMenu(java.awt.Component, boolean, Menu)
 * @see SwingControl#getMenu(java.awt.Component, int, int, int, int)
 */
public class SwtPopupRegistry {

	// -------------------- Static registry of popup menus --------------------

	/**
	 * A popup menu specification.
	 */
	private static class MenuInfo {

		// The menu that shall appear.
		Menu menu;
		// Whether it also applies to subcomponents (if not overridden).
		boolean recursive;

		// Constructor.
		MenuInfo(final Menu menu, final boolean recursive) {
			this.menu = menu;
			this.recursive = recursive;
		}
	}

	private final WeakHashMap /* java.awt.Component -> MenuInfo */ menuTable = new WeakHashMap();

	/**
	 * Returns the registered menu for a given component.
	 * @param component An AWT/Swing component.
	 * @param recursive Whether to look for a recursive or for a non-recursive
	 *            specification of a popup menu.
	 * @return A popup menu, or <code>null</code>.
	 */
	public Menu getMenu(final java.awt.Component component, final boolean recursive) {
		synchronized (menuTable) {
			MenuInfo mi = (MenuInfo) menuTable.get(component);
			if ( mi != null && mi.recursive == recursive ) {
				return mi.menu;
			} else {
				return null;
			}
		}
	}

	/**
	 * Registers a popup menu for a given component and, optionally, its
	 * subcomponents.
	 * <p>
	 * Note: You can only specify one popup menu on a given component. You
	 * cannot specify a recursive and a non-recursive popup menu simultaneously
	 * on the same component.
	 * @param component An AWT/Swing component.
	 * @param recursive Whether the menu also applies to subcomponents (unless
	 *            another popup menu is specified on the subcomponent or
	 *            a component in between in the hierarchy).
	 * @param menu A popup menu, or <code>null</code> to clear the previously
	 *            specified popup menu.
	 */
	public void setMenu(final java.awt.Component component, final boolean recursive, final Menu menu) {
		synchronized (menuTable) {
			MenuInfo mi = (MenuInfo) menuTable.get(component);
			if ( menu != null ) {
				if ( mi != null ) {
					mi.menu = menu;
					mi.recursive = recursive;
				} else {
					mi = new MenuInfo(menu, recursive);
					menuTable.put(component, mi);
				}
			} else {
				if ( mi != null ) {
					menuTable.remove(component);
				}
			}
		}

	}

	/**
	 * Searches for a popup menu to be used on a given component.
	 * <p>
	 * The default implementation walks up the component hierarchy, looking
	 * for popup menus registered with {@link #setMenu}.
	 * <p>
	 * This method can be overridden, to achieve dynamic popup menus.
	 * @param component The component on which a popup event was received.
	 * @param x The x coordinate, relative to the component's top left corner,
	 *            of the mouse cursor when the event occurred.
	 * @param y The y coordinate, relative to the component's top left corner,
	 *            of the mouse cursor when the event occurred.
	 * @param xAbsolute The x coordinate, relative to this control's top left
	 *            corner, of the mouse cursor when the event occurred.
	 * @param yAbsolute The y coordinate, relative to this control's top left
	 *            corner, of the mouse cursor when the event occurred.
	 */
	protected Menu findMenu(final java.awt.Component component, final int x, final int y, final int xAbsolute,
		final int yAbsolute) {
		assert Display.getCurrent() != null;

		synchronized (menuTable) {
			MenuInfo mi = (MenuInfo) menuTable.get(component);
			if ( mi != null ) {
				// On the component itself, ignore whether recursive or not.
				return mi.menu;
			}
			for ( java.awt.Component parent = component.getParent(); parent != null; parent = parent.getParent() ) {
				mi = (MenuInfo) menuTable.get(parent);
				if ( mi != null ) {
					if ( mi.recursive ) {
						return mi.menu;
					} else {
						return null;
					}
				}
			}
		}
		// not found
		return null;
	}

	// ========================================================================
	// Singleton design pattern

	private static SwtPopupRegistry theRegistry = new SwtPopupRegistry();

	/**
	 * Returns the currently active singleton of this class.
	 */
	public static SwtPopupRegistry getInstance() {
		return theRegistry;
	}

	/**
	 * Replaces the singleton of this class.
	 * @param instance An instance of this class or of a customized subclass.
	 */
	public static void setInstance(final SwtPopupRegistry instance) {
		theRegistry = instance;
	}

}
