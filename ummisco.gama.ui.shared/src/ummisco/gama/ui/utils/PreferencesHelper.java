package ummisco.gama.ui.utils;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;

import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.Entry;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.common.interfaces.IGui;
import msi.gama.util.GamaFont;
import msi.gaml.types.IType;
import ummisco.gama.ui.menus.GamaColorMenu;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.GamaPreferencesView;

public class PreferencesHelper {

	public static final Entry<Color> SHAPEFILE_VIEWER_FILL = GamaPreferences
			.create("shapefile.viewer.background", "Default shapefile viewer fill color", Color.LIGHT_GRAY, IType.COLOR)
			.in(GamaPreferences.UI).group("Viewers (settings effective for new viewers)");

	public static final Entry<Color> SHAPEFILE_VIEWER_LINE_COLOR = GamaPreferences
			.create("shapefile.viewer.line.color", "Default shapefile viewer line color", Color.black, IType.COLOR)
			.in(GamaPreferences.UI).group("Viewers (settings effective for new viewers)");

	public static final Entry<Color> ERROR_TEXT_COLOR = GamaPreferences
			.create("error.text.color", "Text color of errors in error view",
					GamaColors.toAwtColor(IGamaColors.ERROR.inactive()), IType.COLOR)
			.in(GamaPreferences.EXPERIMENTS).group("Errors");

	public static final Entry<Color> WARNING_TEXT_COLOR = GamaPreferences
			.create("warning.text.color", "Text color of warnings in error view",
					GamaColors.toAwtColor(IGamaColors.WARNING.inactive()), IType.COLOR)
			.in(GamaPreferences.EXPERIMENTS).group("Errors");

	public static final Entry<Color> IMAGE_VIEWER_BACKGROUND = GamaPreferences
			.create("image.viewer.background", "Default image viewer background color", Color.white, IType.COLOR)
			.in(GamaPreferences.UI).group("Viewers (settings effective for new viewers)");

	public static final Entry<GamaFont> BASE_BUTTON_FONT = GamaPreferences
			.create("base_button_font", "Font of buttons (applies to new buttons)",
					new GamaFont(GamaFonts.baseFont, SWT.BOLD, GamaFonts.baseSize), IType.FONT)
			.in(GamaPreferences.UI).group("Fonts")
			.addChangeListener(new GamaPreferences.IPreferenceChangeListener<GamaFont>() {

				@Override
				public boolean beforeValueChange(final GamaFont newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final GamaFont newValue) {
					GamaFonts.setLabelFont(newValue);
				}
			});

	public static GamaPreferences.Entry<String> COLOR_MENU_SORT = GamaPreferences
			.create("menu.colors.sort", "Sort colors menu by", "RGB value", IType.STRING)
			.among(GamaColorMenu.SORT_NAMES).activates("menu.colors.reverse", "menu.colors.group")
			.in(GamaPreferences.UI).group("Menus").addChangeListener(new IPreferenceChangeListener<String>() {

				@Override
				public boolean beforeValueChange(final String newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final String pref) {
					if (pref.equals(GamaColorMenu.SORT_NAMES[0])) {
						GamaColorMenu.colorComp = GamaColorMenu.byRGB;
					} else if (pref.equals(GamaColorMenu.SORT_NAMES[1])) {
						GamaColorMenu.colorComp = GamaColorMenu.byName;
					} else if (pref.equals(GamaColorMenu.SORT_NAMES[2])) {
						GamaColorMenu.colorComp = GamaColorMenu.byBrightness;
					} else {
						GamaColorMenu.colorComp = GamaColorMenu.byLuminescence;
					}
					GamaColorMenu.instance.reset();
				}
			});
	public static GamaPreferences.Entry<Boolean> COLOR_MENU_REVERSE = GamaPreferences
			.create("menu.colors.reverse", "Reverse order", false, IType.BOOL).in(GamaPreferences.UI).group("Menus")
			.addChangeListener(new IPreferenceChangeListener<Boolean>() {

				@Override
				public boolean beforeValueChange(final Boolean newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Boolean pref) {
					GamaColorMenu.setReverse(pref ? -1 : 1);
					GamaColorMenu.instance.reset();
				}
			});
	public static GamaPreferences.Entry<Boolean> COLOR_MENU_GROUP = GamaPreferences
			.create("menu.colors.group", "Group colors", false, IType.BOOL).in(GamaPreferences.UI).group("Menus")
			.addChangeListener(new IPreferenceChangeListener<Boolean>() {

				@Override
				public boolean beforeValueChange(final Boolean newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Boolean pref) {
					GamaColorMenu.breakdown = pref;
					GamaColorMenu.instance.reset();
				}
			});
	public static final Entry<Boolean> NAVIGATOR_METADATA = GamaPreferences
			.create("navigator.metadata", "Display metadata of data and GAML files in navigator", true, IType.BOOL)
			.in(GamaPreferences.UI).group("Navigator").addChangeListener(new IPreferenceChangeListener<Boolean>() {

				@Override
				public boolean beforeValueChange(final Boolean newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Boolean newValue) {
					final IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
					try {
						mgr.setEnabled(IGui.NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID, newValue);
					} catch (final CoreException e) {
						e.printStackTrace();
					}

				}
			});

	public static void initialize() {
		final int memory = readMaxMemoryInMegabytes();
		if (memory > 0) {
			final GamaPreferences.Entry<Integer> p = GamaPreferences
					.create("core_max_memory", "Maximum memory allocated to GAMA in megabytes", memory, 1)
					.in(GamaPreferences.EXPERIMENTAL).group("Memory (restart GAMA for it to take effect)");
			p.addChangeListener(new IPreferenceChangeListener<Integer>() {

				@Override
				public boolean beforeValueChange(final Integer newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Integer newValue) {
					changeMaxMemory(newValue);
					GamaPreferencesView.setRestartRequired();
				}
			});
		}

	}

	public static int readMaxMemoryInMegabytes() {
		String loc;
		try {
			loc = Platform.getConfigurationLocation().getURL().getPath();
			File dir = new File(loc);
			dir = dir.getParentFile();
			final File ini = new File(dir.getAbsolutePath() + "/Gama.ini");
			if (ini.exists()) {
				try (final FileInputStream stream = new FileInputStream(ini);
						final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));) {
					String s = reader.readLine();
					while (s != null) {
						if (s.startsWith("-Xmx")) {
							final char last = s.charAt(s.length() - 1);
							double divider = 1000000;
							boolean unit = false;
							switch (last) {
							case 'k':
							case 'K':
								unit = true;
								divider = 1000;
								break;
							case 'm':
							case 'M':
								unit = true;
								divider = 1;
								break;
							case 'g':
							case 'G':
								unit = true;
								divider = 0.001;
								break;
							}
							String trim = s;
							trim = trim.replace("-Xmx", "");
							if (unit)
								trim = trim.substring(0, trim.length() - 1);
							final int result = Integer.parseInt(trim);
							return (int) (result / divider);

						}
						s = reader.readLine();
					}
				}
			}
		} catch (final IOException e) {
		}
		return 0;

	}

	public static void changeMaxMemory(final int memory) {
		final int mem = memory < 128 ? 128 : memory;
		String loc;
		try {
			loc = Platform.getConfigurationLocation().getURL().getPath();
			File dir = new File(loc);
			dir = dir.getParentFile();
			final File ini = new File(dir.getAbsolutePath() + "/Gama.ini");
			final List<String> contents = new ArrayList();
			if (ini.exists()) {
				try (final FileInputStream stream = new FileInputStream(ini);
						final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));) {
					String s = reader.readLine();
					while (s != null) {
						if (s.startsWith("-Xmx")) {
							s = "-Xmx" + mem + "m";
						}
						contents.add(s);
						s = reader.readLine();
					}
				}
				try (final FileOutputStream os = new FileOutputStream(ini);
						final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));) {
					for (final String line : contents) {
						writer.write(line);
						writer.newLine();
					}
					writer.flush();
				}
			}
		} catch (final IOException e) {
		}

	}

	private static URL HOME_URL;

	public static URL getWelcomePageURL() {
		if (HOME_URL == null)
			try {
				HOME_URL = FileLocator
						.toFileURL(Platform.getBundle("ummisco.gama.ui.shared").getEntry("/welcome/welcome.html"));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		return HOME_URL;
	}

}
