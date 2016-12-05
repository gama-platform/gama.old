/*********************************************************************************************
 *
 * 'GamlReferenceMenu.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.reference;

import java.util.Comparator;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.utils.WorkbenchHelper;

public abstract class GamlReferenceMenu extends GamaMenu {

	// public class TMenuItem extends GamaMenuItem {
	//
	// public TMenuItem(final Menu parent, final int style, final GamlReferenceMenu menu) {
	// super(parent, style, menu);
	//
	// }
	//
	// @Override
	// protected void showTooltip() {
	// final Object o = getData(TOOLTIP_KEY);
	// if (o == null) {
	// ((GamlReferenceMenu) getTopLevelMenu()).getEditor().stopDisplayingTooltips();
	// } else {
	// ((GamlReferenceMenu) getTopLevelMenu()).getEditor().displayTooltip(o.toString(), IGamaColors.TOOLTIP);
	// }
	//
	// }
	//
	// }

	protected static Comparator<String> IGNORE_CASE = (o1, o2) -> o1.compareToIgnoreCase(o2);

	// private GamlEditor currentEditor;

	// MenuListener tooltipListener = new MenuListener() {
	//
	// @Override
	// public void menuHidden(final MenuEvent e) {
	// // getEditor().stopDisplayingTooltips();
	// // setEditor(null);
	// }
	//
	// @Override
	// public void menuShown(final MenuEvent e) {}
	// };
	//
	// @Override
	// protected TMenuItem createItem(final Menu m, final int style) {
	// return new TMenuItem(m, style, this);
	// }

	protected void open(final Decorations parent, final SelectionEvent trigger) {
		// final boolean asMenu = trigger.detail == SWT.ARROW;
		final boolean init = mainMenu == null;
		// if (!asMenu) {
		// openView();
		// } else {
		final ToolItem target = (ToolItem) trigger.widget;
		final ToolBar toolBar = target.getParent();

		if (init) {
			mainMenu = new Menu(parent, SWT.POP_UP);
			// AD: again. In the first call, the mainMenu was perhaps not
			// yet initialized
			fillMenu();
			// mainMenu.addMenuListener(tooltipListener);
		}

		final Point point = toolBar.toDisplay(new Point(trigger.x, trigger.y));
		mainMenu.setLocation(point.x, point.y);
		mainMenu.setVisible(true);
		// }
	}

	protected abstract void openView();

	@Override
	protected abstract void fillMenu();

	// Helper methods for working with editors and menus

	protected GamlEditor getEditor() {
		return (GamlEditor) WorkbenchHelper.getActiveEditor();
	}

	@Override
	public void reset() {
		if (mainMenu != null && !mainMenu.isDisposed()) {
			// mainMenu.removeMenuListener(tooltipListener);
			super.reset();
		}
	}

	protected final void applyText(final String t) {
		final GamlEditor editor = getEditor();
		if (editor == null) { return; }
		editor.insertText(t);
	}

	public void applyTemplate(final Template t) {
		final GamlEditor editor = getEditor();
		if (editor == null) { return; }
		editor.applyTemplate(t);

	}

	public void installSubMenuIn(final Menu menu) {
		final MenuItem builtInItem = new MenuItem(menu, SWT.CASCADE);
		builtInItem.setText(getTitle());
		builtInItem.setImage(getImage());
		mainMenu = new Menu(builtInItem);
		builtInItem.setMenu(mainMenu);
		mainMenu.addListener(SWT.Show, e -> {
			if (mainMenu.getItemCount() > 0) {
				for (final MenuItem item : mainMenu.getItems()) {
					item.dispose();
				}
			}
			fillMenu();
		});

	}

	/**
	 * @return
	 */
	protected abstract Image getImage();

	/**
	 * @return
	 */
	protected abstract String getTitle();

}