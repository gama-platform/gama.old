/*******************************************************************************************************
 *
 * PreferencesHelper.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.utils;

import static msi.gama.common.interfaces.IGui.NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID;
import static msi.gama.common.preferences.GamaPreferences.create;
import static msi.gama.common.preferences.GamaPreferences.Interface.APPEARANCE;
import static msi.gama.common.preferences.GamaPreferences.Interface.MENUS;
import static msi.gama.common.preferences.GamaPreferences.Interface.NAME;
import static msi.gama.util.GamaColor.getNamed;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static ummisco.gama.ui.menus.GamaColorMenu.SORT_NAMES;
import static ummisco.gama.ui.menus.GamaColorMenu.byBrightness;
import static ummisco.gama.ui.menus.GamaColorMenu.byLuminescence;
import static ummisco.gama.ui.menus.GamaColorMenu.byName;
import static ummisco.gama.ui.menus.GamaColorMenu.byRGB;
import static ummisco.gama.ui.menus.GamaColorMenu.colorComp;
import static ummisco.gama.ui.resources.GamaColors.toGamaColor;
import static ummisco.gama.ui.resources.IGamaColors.WARNING;

import org.eclipse.core.runtime.CoreException;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gama.runtime.MemoryUtils;
import msi.gama.util.GamaColor;
import msi.gaml.types.IType;
import ummisco.gama.ui.menus.GamaColorMenu;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.GamaPreferencesView;

/**
 * The Class PreferencesHelper.
 */
public class PreferencesHelper {

	/** The Constant CORE_EDITORS_HIGHLIGHT. */
	public static final Pref<Boolean> CORE_EDITORS_HIGHLIGHT =
			create("pref_editors_highligth", "Highlight in yellow the title of value editors when they change", true,
					IType.BOOL, true).in(NAME, APPEARANCE);

	/** The Constant SHAPEFILE_VIEWER_FILL. */
	public static final Pref<GamaColor> SHAPEFILE_VIEWER_FILL = create("pref_shapefile_background_color",
			"Shapefile viewer fill color", () -> getNamed("lightgray"), IType.COLOR, false).in(NAME, APPEARANCE);

	/** The Constant SHAPEFILE_VIEWER_LINE_COLOR. */
	public static final Pref<GamaColor> SHAPEFILE_VIEWER_LINE_COLOR = create("pref_shapefile_line_color",
			"Shapefile viewer line color", () -> getNamed("black"), IType.COLOR, false).in(NAME, APPEARANCE);

	/** The Constant ERROR_TEXT_COLOR. */
	public static final Pref<GamaColor> ERROR_TEXT_COLOR =
			create("pref_error_text_color", "Text color of errors", () -> toGamaColor(IGamaColors.ERROR.inactive()),
					IType.COLOR, true).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.ERRORS);

	/** The Constant WARNING_TEXT_COLOR. */
	public static final Pref<GamaColor> WARNING_TEXT_COLOR =
			create("pref_warning_text_color", "Text color of warnings", () -> toGamaColor(WARNING.inactive()),
					IType.COLOR, true).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.ERRORS);

	/** The Constant IMAGE_VIEWER_BACKGROUND. */
	public static final Pref<GamaColor> IMAGE_VIEWER_BACKGROUND =
			create("pref_image_background_color", "Image viewer background color", () -> GamaColor.getNamed("white"),
					IType.COLOR, false).in(NAME, APPEARANCE);

	// public static final Pref<GamaFont> BASE_BUTTON_FONT = create("pref_button_font", "Font of buttons and dialogs",
	// () -> new GamaFont(getBaseFont(), SWT.BOLD, baseSize), IType.FONT, false).in(NAME, APPEARANCE)
	// .onChange(GamaFonts::setLabelFont);

	/** The color menu sort. */
	public static Pref<String> COLOR_MENU_SORT =
			create("pref_menu_colors_sort", "Sort colors menu by", "RGB value", IType.STRING, false).among(SORT_NAMES)
					.activates("menu.colors.reverse", "menu.colors.group").in(NAME, MENUS).onChange(pref -> {
						if (pref.equals(SORT_NAMES[0])) {
							colorComp = byRGB;
						} else if (pref.equals(SORT_NAMES[1])) {
							colorComp = byName;
						} else if (pref.equals(SORT_NAMES[2])) {
							colorComp = byBrightness;
						} else {
							colorComp = byLuminescence;
						}
					});

	/** The color menu reverse. */
	public static Pref<Boolean> COLOR_MENU_REVERSE =
			create("pref_menu_colors_reverse", "Reverse order", false, IType.BOOL, false).in(NAME, MENUS)
					.onChange(pref -> GamaColorMenu.setReverse(pref ? -1 : 1));

	/** The color menu group. */
	public static Pref<Boolean> COLOR_MENU_GROUP =
			create("pref_menu_colors_group", "Group colors", false, IType.BOOL, false).in(NAME, MENUS)
					.onChange(pref -> GamaColorMenu.breakdown = pref);

	/** The Constant NAVIGATOR_METADATA. */
	public static final Pref<Boolean> NAVIGATOR_METADATA =
			create("pref_navigator_display_metadata", "Display metadata in navigator", true, IType.BOOL, false)
					.in(NAME, APPEARANCE).onChange(newValue -> {
						final var mgr = getWorkbench().getDecoratorManager();
						try {
							mgr.setEnabled(NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID, newValue);
						} catch (final CoreException e) {
							e.printStackTrace();
						}

					});

	/**
	 * Initialize.
	 */
	public static void initialize() {
		final var ini = MemoryUtils.findIniFile();
		Integer writtenMemory = ini == null ? null : MemoryUtils.readMaxMemoryInMegabytes(ini);
		final var text = ini == null || writtenMemory == null
				? "The max. memory allocated. It can be modified in Eclipse (developer version) or in Gama.ini file"
				: "Maximum memory allocated in Mb (requires to restart GAMA)";
		final var maxMemory = writtenMemory == null ? MemoryUtils.maxMemory() : writtenMemory;
		final var p = GamaPreferences.create("pref_memory_max", text, maxMemory, 1, false)
				.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.MEMORY);
		// Force the value to the one contained in the ini file or in the arguments.
		p.set(maxMemory);
		if (writtenMemory == null) { p.disabled(); }
		p.onChange(newValue -> {
			MemoryUtils.changeMaxMemory(ini, newValue);
			GamaPreferencesView.setRestartRequired();
		});

	}

}
