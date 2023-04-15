/*******************************************************************************************************
 *
 * GamaColorMenu.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.menus;

import static msi.gama.util.GamaColor.colors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import msi.gama.util.GamaColor;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.PreferencesHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The class EditToolbarColorMenu.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaColorMenu extends GamaMenu {

	/** The Constant SORT_NAMES. */
	public static final String[] SORT_NAMES = { "RGB value", "Name", "Brightness", "Luminescence" };

	/**
	 * The Interface IColorRunnable.
	 */
	public interface IColorRunnable {

		/**
		 * Run.
		 *
		 * @param r
		 *            the r
		 * @param g
		 *            the g
		 * @param b
		 *            the b
		 */
		void run(int r, int g, int b);
	}

	/** The current runnable. */
	IColorRunnable currentRunnable;

	// IColorRunnable defaultRunnable = (r, g, b) -> currentRunnable.run(r, g, b);

	/** The default listener. */
	SelectionListener defaultListener = new SelectionAdapter() {

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			currentListener.widgetSelected(e);
		}

	};

	/**
	 * Instantiates a new gama color menu.
	 *
	 * @param main
	 *            the main
	 */
	public GamaColorMenu(final Menu main) {
		mainMenu = main;
	}

	/** The current listener. */
	SelectionListener currentListener;

	/** The reverse. */
	private static Integer reverse = null;

	/** The by RGB. */
	public static Comparator<String> byRGB = (a, b) -> getReverse() * colors.get(a).compareTo(colors.get(b));

	/** The by brightness. */
	public static Comparator<String> byBrightness =
			(a, b) -> getReverse() * colors.get(a).compareBrightnessTo(colors.get(b));

	/** The by name. */
	public static Comparator<String> byName = (a, b) -> getReverse() * a.compareTo(b);

	/** The by luminescence. */
	public static Comparator<String> byLuminescence =
			(a, b) -> getReverse() * GamaColor.colors.get(a).compareTo(GamaColor.colors.get(b));

	/** The color comp. */
	public static Comparator colorComp = null;

	/** The choose sort. */
	public SelectionListener chooseSort = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem item = (MenuItem) e.widget;
			colorComp = (Comparator) item.getData();
			reset();
		}

	};

	/** The breakdown. */
	public static Boolean breakdown = null;

	/** The choose break. */
	Selector chooseBreak = e -> {
		breakdown = !breakdown;
		reset();
	};

	/** The choose reverse. */
	Selector chooseReverse = e -> {
		setReverse(-1 * getReverse());
		reset();
	};

	/**
	 * Open view.
	 *
	 * @param runnable
	 *            the runnable
	 * @param initial
	 *            the initial
	 */
	public static void openView(final IColorRunnable runnable, final RGB initial) {
		final Shell shell = new Shell(WorkbenchHelper.getDisplay(), SWT.MODELESS);
		final ColorDialog dlg = new ColorDialog(shell, SWT.MODELESS);
		dlg.setText("Choose a custom color");
		dlg.setRGB(initial);
		final RGB rgb = dlg.open();
		// final int a = StringUtils.INDEX_NOT_FOUND;
		if (rgb != null && runnable != null) { runnable.run(rgb.red, rgb.green, rgb.blue); }
	}

	@Override
	public void fillMenu() {
		if (colorComp == null) {
			final String pref = PreferencesHelper.COLOR_MENU_SORT.getValue();
			if (pref.equals(SORT_NAMES[0])) {
				colorComp = byRGB;
			} else if (pref.equals(SORT_NAMES[1])) {
				colorComp = byName;
			} else if (pref.equals(SORT_NAMES[2])) {
				colorComp = byBrightness;
			} else {
				colorComp = byLuminescence;
			}
		}
		if (getReverse() == null) { setReverse(PreferencesHelper.COLOR_MENU_REVERSE.getValue() ? -1 : 1); }
		if (breakdown == null) { breakdown = PreferencesHelper.COLOR_MENU_GROUP.getValue(); }
		action("Custom...", e -> openView(currentRunnable, null));
		final Menu optionMenu = sub("Options");
		final Menu sortMenu = sub(optionMenu, "Sort by...");
		check(optionMenu, "Breakdown", breakdown, chooseBreak);
		check(optionMenu, "Reverse order", getReverse() == -1, chooseReverse);
		check(sortMenu, SORT_NAMES[0], colorComp == byRGB, chooseSort).setData(byRGB);
		check(sortMenu, SORT_NAMES[1], colorComp == byName, chooseSort).setData(byName);
		check(sortMenu, SORT_NAMES[2], colorComp == byBrightness, chooseSort).setData(byBrightness);
		check(sortMenu, SORT_NAMES[3], colorComp == byLuminescence, chooseSort).setData(byLuminescence);
		sep();
		final List<String> names = new ArrayList(GamaColor.colors.keySet());
		Collections.sort(names, colorComp);
		Menu subMenu = mainMenu;
		for (int i = 0; i < names.size(); i++) {
			final String current = names.get(i);
			if (breakdown && i % 10 == 0) {
				final String following = names.get(Math.min(i + 9, names.size() - 1)).replace("#", "");
				subMenu = sub(current.replace("#", "") + " to " + following);
			}
			final MenuItem item = action(subMenu, "#" + current, defaultListener);
			final GamaColor color = GamaColor.colors.get(current);
			item.setImage(GamaIcon.ofColor(GamaColors.get(color.red(), color.green(), color.blue()), true).image());
		}

	}

	/**
	 * Adds the color submenu to.
	 *
	 * @param menu
	 *            the menu
	 * @param text
	 *            the text
	 * @param selector
	 *            the selector
	 */
	public static void addColorSubmenuTo(final Menu menu, final String text, final Consumer<GamaColor> selector) {
		Menu subMenu = sub(menu, text, text, GamaIcon.named(IGamaIcons.REFERENCE_COLORS).image());
		final List<String> names = new ArrayList(GamaColor.colors.keySet());
		Collections.sort(names, colorComp);
		for (final String current : names) {
			final GamaColor color = GamaColor.colors.get(current);
			final MenuItem item = action(subMenu, "#" + current, t -> selector.accept(GamaColor.colors.get(current)));
			item.setImage(GamaIcon.ofColor(GamaColors.get(color.red(), color.green(), color.blue()), true).image());
		}
	}

	/**
	 * Open.
	 *
	 * @param c
	 *            the c
	 * @param trigger
	 *            the trigger
	 * @param colorInserter
	 *            the color inserter
	 * @param custom
	 *            the custom
	 */
	public void open(final Control c, final SelectionEvent trigger, final SelectionListener colorInserter,
			final IColorRunnable custom) {
		currentListener = colorInserter;
		currentRunnable = custom;
		if (mainMenu == null) {
			mainMenu = new Menu(WorkbenchHelper.getShell(), SWT.POP_UP);
			fillMenu();
		}

		final Point point = c.toDisplay(new Point(trigger.x, trigger.y));
		mainMenu.setLocation(point.x, point.y);
		mainMenu.setVisible(true);
	}

	@Override
	public void reset() {
		super.reset();
		currentListener = null;
		currentRunnable = null;
	}

	/**
	 * Gets the reverse.
	 *
	 * @return the reverse
	 */
	public static Integer getReverse() { return reverse; }

	/**
	 * Sets the reverse.
	 *
	 * @param r
	 *            the new reverse
	 */
	public static void setReverse(final Integer r) { reverse = r; }

	/**
	 * Sets the selection listener.
	 *
	 * @param colorInserter
	 *            the new selection listener
	 */
	public void setSelectionListener(final SelectionListener colorInserter) {
		this.currentListener = colorInserter;

	}

	/**
	 * Sets the current runnable.
	 *
	 * @param runnable
	 *            the new current runnable
	 */
	public void setCurrentRunnable(final IColorRunnable runnable) {
		this.currentRunnable = runnable;

	}

}
