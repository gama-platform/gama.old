/*********************************************************************************************
 *
 * 'GamaMenu.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.menus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * The class GamaMenu.
 * 
 * @author drogoul
 * @since 11 déc. 2014
 * 
 */
public abstract class GamaMenu {

	public static MenuItem separate(final Menu parent, final String s) {
		final MenuItem string = new MenuItem(parent, SWT.PUSH);
		string.setEnabled(false);
		string.setText(s);
		return string;
	}

	public static MenuItem separate(final Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
	}

	protected Menu mainMenu;

	protected MenuItem createItem(final Menu m, final int style) {
		return new MenuItem(m, style);
	}

	protected final void sep() {
		sep(mainMenu);
	}

	protected final void sep(final Menu m) {
		createItem(m, SWT.SEPARATOR);
	}

	protected final void sep(final String s) {
		sep(mainMenu, s);
	}

	protected final void sep(final Menu m, final String s) {
		final MenuItem me = createItem(m, SWT.NONE);
		me.setText(s);
		me.setEnabled(false);
	}

	protected final void title(final String s) {
		title(mainMenu, s);
	}

	protected final void title(final Menu m, final String s) {
		sep(m);
		sep(m, s);
		sep(m);
	}

	protected final MenuItem action(final String s, final SelectionListener listener) {
		return action(mainMenu, s, listener);
	}

	protected final MenuItem action(final String s, final SelectionListener listener, final Image image) {
		return action(mainMenu, s, listener, image);
	}

	protected final MenuItem action(final Menu m, final String s, final SelectionListener listener) {
		return action(m, s, listener, null);
	}

	protected final MenuItem action(final Menu m, final String s, final SelectionListener listener, final Image image) {
		final MenuItem action = createItem(m, SWT.PUSH);
		action.setText(s);
		action.addSelectionListener(listener);
		if (image != null) {
			action.setImage(image);
		}
		return action;
	}

	protected final MenuItem check(final String s, final boolean selected, final SelectionListener listener) {
		return check(mainMenu, s, selected, listener);
	}

	protected final MenuItem check(final String s, final boolean selected, final SelectionListener listener,
			final Image image) {
		return check(mainMenu, s, selected, listener, image);
	}

	protected final MenuItem check(final Menu m, final String s, final boolean select,
			final SelectionListener listener) {
		return check(m, s, select, listener, null);
	}

	protected final MenuItem check(final Menu m, final String s, final boolean select, final SelectionListener listener,
			final Image image) {
		final MenuItem action = createItem(m, SWT.CHECK);
		action.setText(s);
		action.setSelection(select);
		action.addSelectionListener(listener);
		if (image != null) {
			action.setImage(image);
		}
		return action;
	}

	protected final Menu sub(final String s) {
		return sub(mainMenu, s);
	}

	protected final Menu sub(final Menu parent, final String s) {
		return sub(parent, s, null);
	}

	protected final Menu sub(final Menu parent, final String s, final String t) {
		final MenuItem item = createItem(parent, SWT.CASCADE);
		item.setText(s);
		if (t != null) {
			item.setToolTipText(t);
		}
		final Menu m = new Menu(item);
		item.setMenu(m);
		return m;
	}

	public void reset() {
		if (mainMenu != null && !mainMenu.isDisposed()) {
			for (final MenuItem item : mainMenu.getItems())
				item.dispose();
		}
	}

	public void open(final Control c, final SelectionEvent trigger) {
		open(c, trigger, 0);
	}

	public void open(final Control c, final SelectionEvent trigger, final int verticalOffset) {

		if (mainMenu == null || mainMenu.isDisposed() || mainMenu.getItemCount() == 0) {
			mainMenu = new Menu(c.getShell(), SWT.POP_UP);
			fillMenu();
		}

		final Point point = c.toDisplay(new Point(trigger.x, trigger.y));
		mainMenu.setLocation(point.x, point.y + verticalOffset);
		mainMenu.setVisible(true);
	}

	protected abstract void fillMenu();

}