/*******************************************************************************************************
 *
 * ColorReferenceMenu.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The class EditToolbarColorMenu.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
public class ColorReferenceMenu extends GamlReferenceMenu {

	/** The color menu. */
	GamaColorMenu colorMenu;

	/** The runnable. */
	IColorRunnable runnable = (r, g, b) -> {
		final GamaColor c = new GamaColor(r, g, b, 255);
		applyText(c.serialize(true));
	};

	/** The color inserter. */
	final SelectionListener colorInserter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem i = (MenuItem) e.widget;
			applyText(i.getText());
		}
	};

	@Override
	protected void open(final Decorations parent, final SelectionEvent trigger) {
		if (colorMenu == null) { colorMenu = new GamaColorMenu(mainMenu); }
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
			colorMenu.setSelectionListener(colorInserter);
			colorMenu.setCurrentRunnable(runnable);
		}
		colorMenu.fillMenu();

	}

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getImage()
	 */
	@Override
	protected Image getImage() { return GamaIcons.create(IGamaIcons.REFERENCE_COLORS).image(); }

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getTitle()
	 */
	@Override
	protected String getTitle() { return "Colors"; }

}
