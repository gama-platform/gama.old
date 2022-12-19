/*******************************************************************************************************
 *
 * GamaMenu.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.menus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The class GamaMenu.
 *
 * @author drogoul
 * @since 11 déc. 2014
 *
 */
public abstract class GamaMenu {

	/**
	 * Separate.
	 *
	 * @param parent
	 *            the parent
	 * @param s
	 *            the s
	 * @return the menu item
	 */
	public static MenuItem separate(final Menu parent, final String s) {
		final MenuItem string = new MenuItem(parent, SWT.PUSH);
		string.setEnabled(false);
		string.setText(s);
		return string;
	}

	/**
	 * Separate.
	 *
	 * @param parent
	 *            the parent
	 * @return the menu item
	 */
	public static MenuItem separate(final Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
	}

	/** The main menu. */
	protected Menu mainMenu;

	/**
	 * Creates the item.
	 *
	 * @param m
	 *            the m
	 * @param style
	 *            the style
	 * @return the menu item
	 */
	public static MenuItem createItem(final Menu m, final int style) {
		return new MenuItem(m, style);
	}

	/**
	 * Sep.
	 */
	protected final void sep() {
		sep(mainMenu);
	}

	/**
	 * Sep.
	 *
	 * @param m
	 *            the m
	 */
	public final void sep(final Menu m) {
		createItem(m, SWT.SEPARATOR);
	}

	/**
	 * Sep.
	 *
	 * @param m
	 *            the m
	 * @param s
	 *            the s
	 */
	protected final void sep(final Menu m, final String s) {
		final MenuItem me = createItem(m, SWT.NONE);
		me.setText(s);
		me.setEnabled(false);
	}

	/**
	 * Title.
	 *
	 * @param m
	 *            the m
	 * @param s
	 *            the s
	 */
	protected final void title(final Menu m, final String s) {
		sep(m);
		sep(m, s);
		sep(m);
	}

	/**
	 * Action.
	 *
	 * @param s
	 *            the s
	 * @param listener
	 *            the listener
	 * @return the menu item
	 */
	protected final MenuItem action(final String s, final SelectionListener listener) {
		return action(mainMenu, s, listener);
	}

	/**
	 * Action.
	 *
	 * @param s
	 *            the s
	 * @param listener
	 *            the listener
	 * @param image
	 *            the image
	 * @return the menu item
	 */
	protected final MenuItem action(final String s, final SelectionListener listener, final Image image) {
		return action(mainMenu, s, listener, image);
	}

	/**
	 * Action.
	 *
	 * @param m
	 *            the m
	 * @param s
	 *            the s
	 * @param listener
	 *            the listener
	 * @return the menu item
	 */
	public static final MenuItem action(final Menu m, final String s, final SelectionListener listener) {
		return action(m, s, listener, null);
	}

	/**
	 * Action.
	 *
	 * @param m the m
	 * @param s the s
	 * @param listener the listener
	 * @return the menu item
	 */
	public static final MenuItem action(final Menu m, final String s, final Selector listener) {
		return action(m, s, listener, null);
	}

	/**
	 * Action.
	 *
	 * @param m
	 *            the m
	 * @param s
	 *            the s
	 * @param listener
	 *            the listener
	 * @param image
	 *            the image
	 * @return the menu item
	 */
	public static MenuItem action(final Menu m, final String s, final SelectionListener listener, final Image image) {
		final MenuItem action = createItem(m, SWT.PUSH);
		action.setText(s);
		action.addSelectionListener(listener);
		if (image != null) { action.setImage(image); }
		return action;
	}

	/**
	 * Action.
	 *
	 * @param m the m
	 * @param s the s
	 * @param listener the listener
	 * @param image the image
	 * @return the menu item
	 */
	public static MenuItem action(final Menu m, final String s, final Selector listener, final Image image) {
		final MenuItem action = createItem(m, SWT.PUSH);
		action.setText(s);
		action.addSelectionListener(listener);
		if (image != null) { action.setImage(image); }
		return action;
	}

	/**
	 * Check.
	 *
	 * @param m
	 *            the m
	 * @param s
	 *            the s
	 * @param select
	 *            the select
	 * @param listener
	 *            the listener
	 * @return the menu item
	 */
	protected final MenuItem check(final Menu m, final String s, final boolean select,
			final SelectionListener listener) {
		return check(m, s, select, listener, null);
	}

	/**
	 * Check.
	 *
	 * @param m
	 *            the m
	 * @param s
	 *            the s
	 * @param select
	 *            the select
	 * @param listener
	 *            the listener
	 * @param image
	 *            the image
	 * @return the menu item
	 */
	public static final MenuItem check(final Menu m, final String s, final boolean select,
			final SelectionListener listener, final Image image) {
		final MenuItem action = createItem(m, SWT.CHECK);
		action.setText(s);
		action.setSelection(select);
		action.addSelectionListener(listener);
		if (image != null) { action.setImage(image); }
		return action;
	}

	/**
	 * Check.
	 *
	 * @param m the m
	 * @param s the s
	 * @param select the select
	 * @param listener the listener
	 * @param image the image
	 * @return the menu item
	 */
	public static final MenuItem check(final Menu m, final String s, final boolean select, final Selector listener,
			final Image image) {
		final MenuItem action = createItem(m, SWT.CHECK);
		action.setText(s);
		action.setSelection(select);
		action.addSelectionListener(listener);
		if (image != null) { action.setImage(image); }
		return action;
	}

	/**
	 * Sub.
	 *
	 * @param s
	 *            the s
	 * @return the menu
	 */
	protected final Menu sub(final String s) {
		return sub(mainMenu, s);
	}

	/**
	 * Sub.
	 *
	 * @param parent
	 *            the parent
	 * @param s
	 *            the s
	 * @return the menu
	 */
	public final Menu sub(final Menu parent, final String s) {
		return sub(parent, s, null);
	}

	/**
	 * Sub.
	 *
	 * @param parent
	 *            the parent
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return the menu
	 */
	public static Menu sub(final Menu parent, final String s, final String t) {
		final MenuItem item = createItem(parent, SWT.CASCADE);
		item.setText(s);
		if (t != null) { item.setToolTipText(t); }
		final Menu m = new Menu(item);
		item.setMenu(m);
		return m;
	}

	/**
	 * Sub.
	 *
	 * @param parent the parent
	 * @param s the s
	 * @param t the t
	 * @param image the image
	 * @return the menu
	 */
	public static Menu sub(final Menu parent, final String s, final String t, final Image image) {
		final MenuItem item = createItem(parent, SWT.CASCADE);
		item.setText(s);
		item.setImage(image);
		if (t != null) { item.setToolTipText(t); }
		final Menu m = new Menu(item);
		item.setMenu(m);
		return m;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		if (mainMenu != null && !mainMenu.isDisposed()) {
			for (final MenuItem item : mainMenu.getItems()) { item.dispose(); }
		}
	}

	/**
	 * Open.
	 *
	 * @param trigger
	 *            the trigger
	 */
	public void open(final SelectionEvent trigger) {
		if (trigger.widget instanceof Control c) { this.open(c, trigger); }
	}

	/**
	 * Open.
	 *
	 * @param c
	 *            the c
	 * @param trigger
	 *            the trigger
	 */
	public void open(final Control c, final SelectionEvent trigger) {
		open(c, trigger, 0);
	}

	/**
	 * Open.
	 *
	 * @param c
	 *            the c
	 * @param trigger
	 *            the trigger
	 * @param verticalOffset
	 *            the vertical offset
	 */
	public void open(final Control c, final SelectionEvent trigger, final int verticalOffset) {

		open(c, trigger, verticalOffset, 0);
	}

	/**
	 * Open.
	 *
	 * @param c
	 *            the c
	 * @param trigger
	 *            the trigger
	 * @param verticalOffset
	 *            the vertical offset
	 * @param horizontalOffset
	 *            the horizontal offset
	 */
	public void open(final Control c, final SelectionEvent trigger, final int verticalOffset,
			final int horizontalOffset) {

		if (mainMenu == null || mainMenu.isDisposed() || mainMenu.getItemCount() == 0) {
			mainMenu = new Menu(c.getShell(), SWT.POP_UP);
			fillMenu();
		}

		final Point point = c.toDisplay(new Point(trigger.x, trigger.y));
		mainMenu.setLocation(point.x + horizontalOffset, point.y + verticalOffset);
		mainMenu.setVisible(true);
	}

	/**
	 * Fill menu.
	 */
	protected abstract void fillMenu();

}