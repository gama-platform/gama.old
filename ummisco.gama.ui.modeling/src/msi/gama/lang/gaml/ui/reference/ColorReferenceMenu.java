/*********************************************************************************************
 *
 * 'ColorReferenceMenu.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.reference;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.util.GamaColor;
import ummisco.gama.ui.menus.GamaColorMenu;
import ummisco.gama.ui.menus.GamaColorMenu.IColorRunnable;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * The class EditToolbarColorMenu.
 * 
 * @author drogoul
 * @since 5 déc. 2014
 * 
 */
public class ColorReferenceMenu extends GamlReferenceMenu {

	GamaColorMenu colorMenu;

	IColorRunnable runnable = (r, g, b) -> {
		final GamaColor c = new GamaColor(r, g, b, 255);
		applyText(c.serialize(true));
	};

	SelectionListener colorInserter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem i = (MenuItem) e.widget;
			applyText(i.getText());
		}
	};

	@Override
	protected void open(final Decorations parent, final SelectionEvent trigger) {
		if (colorMenu == null) {
			colorMenu = new GamaColorMenu(mainMenu);
		}
		final ToolItem target = (ToolItem) trigger.widget;
		final ToolBar toolBar = target.getParent();
		colorMenu.open(toolBar, trigger, colorInserter, runnable);
	}

	@Override
	protected void openView() {
		GamaColorMenu.openView(runnable, null);
	}

	@Override
	protected void fillMenu() {
		if (colorMenu == null) {
			colorMenu = new GamaColorMenu(mainMenu);
		}
		colorMenu.fillMenu();

	}

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getImage()
	 */
	@Override
	protected Image getImage() {
		return GamaIcons.create("reference.colors").image();
	}

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getTitle()
	 */
	@Override
	protected String getTitle() {
		return "Colors";
	}

}
