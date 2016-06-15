/**
 * Created by drogoul, 11 déc. 2014
 *
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.Comparator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

import msi.gama.gui.swt.commands.*;
import ummisco.gama.ui.resources.IGamaColors;

public abstract class EditToolbarMenu extends GamaMenu {

	public class TMenuItem extends GamaMenuItem {

		public TMenuItem(final Menu parent, final int style, final EditToolbarMenu menu) {
			super(parent, style, menu);

		}

		@Override
		protected void showTooltip() {
			Object o = getData(TOOLTIP_KEY);
			if ( o == null ) {
				((EditToolbarMenu) topLevelMenu).getEditor().stopDisplayingTooltips();
			} else {
				((EditToolbarMenu) topLevelMenu).getEditor().displayTooltip(o.toString(), IGamaColors.TOOLTIP);
			}

		}

	}

	protected static Comparator<String> IGNORE_CASE = new Comparator<String>() {

		@Override
		public int compare(final String o1, final String o2) {
			return o1.compareToIgnoreCase(o2);
		}
	};

	private GamlEditor currentEditor;

	MenuListener tooltipListener = new MenuListener() {

		@Override
		public void menuHidden(final MenuEvent e) {
			getEditor().stopDisplayingTooltips();
			// setEditor(null);
		}

		@Override
		public void menuShown(final MenuEvent e) {}
	};

	@Override
	protected TMenuItem createItem(final Menu m, final int style) {
		return new TMenuItem(m, style, this);
	}

	protected void open(final GamlEditor editor, final SelectionEvent trigger) {
		boolean asMenu = trigger.detail == SWT.ARROW;
		boolean init = mainMenu == null;
		setEditor(editor);
		if ( !asMenu ) {
			openView();
		} else {
			final ToolItem target = (ToolItem) trigger.widget;
			final ToolBar toolBar = target.getParent();

			if ( init ) {
				mainMenu = new Menu(editor.getShell(), SWT.POP_UP);
				// AD: again. In the first call, the mainMenu was perhaps not yet initialized
				setEditor(editor);
				fillMenu();
				mainMenu.addMenuListener(tooltipListener);
			}

			Point point = toolBar.toDisplay(new Point(trigger.x, trigger.y));
			mainMenu.setLocation(point.x, point.y);
			mainMenu.setVisible(true);
		}
	}

	protected abstract void openView();

	@Override
	protected abstract void fillMenu();

	// Helper methods for working with editors and menus

	protected GamlEditor getEditor() {
		return currentEditor;
	}

	@Override
	protected void reset() {
		if ( mainMenu != null && !mainMenu.isDisposed() ) {
			mainMenu.removeMenuListener(tooltipListener);
			super.reset();
		}
	}

	protected final void applyText(final String t) {
		GamlEditor editor = getEditor();
		if ( editor == null ) { return; }
		editor.insertText(t);
	}

	public void applyTemplate(final Template t) {
		GamlEditor editor = getEditor();
		if ( editor == null ) { return; }
		editor.applyTemplate(t);

	}

	protected void setEditor(final GamlEditor currentEditor) {
		this.currentEditor = currentEditor;
		if ( mainMenu != null ) {
			mainMenu.setData(EditToolbarMenuFactory.EDITOR_KEY, currentEditor);
		}
	}

}