/**
 * Created by drogoul, 11 déc. 2014
 * 
 */
package msi.gama.gui.swt.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;

/**
 * The class GamaMenu.
 * 
 * @author drogoul
 * @since 11 déc. 2014
 * 
 */
public abstract class GamaMenu {

	protected Menu mainMenu;

	protected GamaMenuItem createItem(final Menu m, final int style) {
		return new GamaMenuItem(m, style, this);
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
		GamaMenuItem me = createItem(m, SWT.NONE);
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

	protected final GamaMenuItem action(final String s, final SelectionListener listener) {
		return action(mainMenu, s, listener);
	}

	protected final GamaMenuItem action(final Menu m, final String s, final SelectionListener listener) {
		GamaMenuItem action = createItem(m, SWT.PUSH);
		action.setText(s);
		action.addSelectionListener(listener);
		return action;
	}

	protected final GamaMenuItem check(final String s, final boolean selected, final SelectionListener listener) {
		return check(mainMenu, s, selected, listener);
	}

	protected final GamaMenuItem check(final Menu m, final String s, final boolean select,
		final SelectionListener listener) {
		GamaMenuItem action = createItem(m, SWT.CHECK);
		action.setText(s);
		action.setSelection(select);
		action.addSelectionListener(listener);
		return action;
	}

	protected final Menu sub(final String s) {
		return sub(mainMenu, s);
	}

	protected final Menu sub(final Menu parent, final String s) {
		return sub(parent, s, null);
	}

	protected final Menu sub(final Menu parent, final String s, final String t) {
		GamaMenuItem item = createItem(parent, SWT.CASCADE);
		item.setText(s);
		if ( t != null ) {
			item.setTooltipText(t);
		}
		Menu m = new Menu(item);
		item.setMenu(m);
		return m;
	}

	protected void reset() {
		if ( mainMenu != null && !mainMenu.isDisposed() ) {
			mainMenu.dispose();
			mainMenu = null;
		}
	}

}