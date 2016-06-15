/**
 * Created by drogoul, 5 déc. 2014
 *
 */
package msi.gama.gui.swt.commands;

import java.util.*;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import msi.gama.gui.swt.*;
import msi.gama.util.GamaColor;
import msi.gaml.operators.fastmaths.CmnFastMath;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * The class EditToolbarColorMenu.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
public class GamaColorMenu extends GamaMenu {

	public static final String[] SORT_NAMES = new String[] { "RGB value", "Name", "Brightness", "Luminescence" };

	public static GamaColorMenu instance = new GamaColorMenu();

	public static GamaColorMenu getInstance() {
		return instance;
	}

	public static interface IColorRunnable {

		void run(int r, int g, int b);
	}

	IColorRunnable defaultRunnable = new IColorRunnable() {

		@Override
		public void run(final int r, final int g, final int b) {
			currentRunnable.run(r, g, b);
		}

	};

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

	private GamaColorMenu() {}

	private SelectionListener currentListener;
	private IColorRunnable currentRunnable;

	public static Integer reverse = null;

	public static Comparator byRGB = new Comparator<String>() {

		@Override
		public int compare(final String arg0, final String arg1) {
			return reverse * GamaColor.colors.get(arg0).compareTo(GamaColor.colors.get(arg1));
		}
	};

	public static Comparator byBrightness = new Comparator<String>() {

		@Override
		public int compare(final String arg0, final String arg1) {
			return reverse * GamaColor.colors.get(arg0).compareBrightnessTo(GamaColor.colors.get(arg1));
		}
	};

	public static Comparator byName = new Comparator<String>() {

		@Override
		public int compare(final String arg0, final String arg1) {
			return reverse * arg0.compareTo(arg1);
		}
	};

	public static Comparator byLuminescence = new Comparator<String>() {

		@Override
		public int compare(final String arg0, final String arg1) {
			return reverse * GamaColor.colors.get(arg0).compareTo(GamaColor.colors.get(arg1));
		}
	};
	public static Comparator colorComp = null;

	public static SelectionListener chooseSort = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			GamaMenuItem item = (GamaMenuItem) e.widget;
			colorComp = (Comparator) item.getData();
			item.topLevelMenu.reset();
		}

	};

	public static Boolean breakdown = null;

	static SelectionListener chooseBreak = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			GamaMenuItem item = (GamaMenuItem) e.widget;
			breakdown = !breakdown;
			item.topLevelMenu.reset();
		}

	};

	static SelectionListener chooseReverse = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			GamaMenuItem item = (GamaMenuItem) e.widget;
			reverse = -1 * reverse;
			item.topLevelMenu.reset();
		}

	};

	public static void openView(final IColorRunnable runnable, final RGB initial) {
		Shell shell = new Shell(Display.getDefault(), SWT.MODELESS);
		final ColorDialog dlg = new ColorDialog(shell, SWT.MODELESS);
		dlg.setText("Choose a custom color");
		dlg.setRGB(initial);
		final RGB rgb = dlg.open();
		int a = StringUtils.INDEX_NOT_FOUND;
		if ( rgb != null ) {
			if ( runnable != null ) {
				runnable.run(rgb.red, rgb.green, rgb.blue);
			}
		}
	}

	@Override
	public void fillMenu() {
		if ( colorComp == null ) {
			String pref = SwtGui.COLOR_MENU_SORT.getValue();
			if ( pref.equals(SORT_NAMES[0]) ) {
				colorComp = byRGB;
			} else if ( pref.equals(SORT_NAMES[1]) ) {
				colorComp = byName;
			} else if ( pref.equals(SORT_NAMES[2]) ) {
				colorComp = byBrightness;
			} else {
				colorComp = byLuminescence;
			}
		}
		if ( reverse == null ) {
			reverse = SwtGui.COLOR_MENU_REVERSE.getValue() ? -1 : 1;
		}
		if ( breakdown == null ) {
			breakdown = SwtGui.COLOR_MENU_GROUP.getValue();
		}
		action("Custom...", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				openView(defaultRunnable, null);
			}

		});
		Menu optionMenu = sub("Options");
		Menu sortMenu = sub(optionMenu, "Sort by...");
		check(optionMenu, "Breakdown", breakdown, chooseBreak);
		check(optionMenu, "Reverse order", reverse == -1, chooseReverse);
		check(sortMenu, SORT_NAMES[0], colorComp == byRGB, chooseSort).setData(byRGB);
		check(sortMenu, SORT_NAMES[1], colorComp == byName, chooseSort).setData(byName);
		check(sortMenu, SORT_NAMES[2], colorComp == byBrightness, chooseSort).setData(byBrightness);
		check(sortMenu, SORT_NAMES[3], colorComp == byLuminescence, chooseSort).setData(byLuminescence);
		sep();
		List<String> names = new ArrayList(GamaColor.colors.keySet());
		Collections.sort(names, colorComp);
		Menu subMenu = mainMenu;
		for ( int i = 0; i < names.size(); i++ ) {
			String current = names.get(i);
			if ( breakdown && i % 10 == 0 ) {
				String following = names.get(CmnFastMath.min(i + 9, names.size() - 1)).replace("#", "");
				subMenu = sub(current.replace("#", "") + " to " + following);
			}
			MenuItem item = action(subMenu, "#" + current, defaultListener);
			GamaColor color = GamaColor.colors.get(current);
			item.setImage(GamaIcons
				.createColorIcon(current, GamaColors.get(color.red(), color.green(), color.blue()), 16, 16).image());
		}

	}

	public void open(final Control c, final SelectionEvent trigger, final SelectionListener colorInserter,
		final IColorRunnable custom) {
		currentListener = colorInserter;
		currentRunnable = custom;
		if ( mainMenu == null ) {
			mainMenu = new Menu(SwtGui.getWindow().getShell(), SWT.POP_UP);
			fillMenu();
		}

		Point point = c.toDisplay(new Point(trigger.x, trigger.y));
		mainMenu.setLocation(point.x, point.y);
		mainMenu.setVisible(true);
	}

	@Override
	public void reset() {
		super.reset();
		currentListener = null;
		currentRunnable = null;
	}

}
