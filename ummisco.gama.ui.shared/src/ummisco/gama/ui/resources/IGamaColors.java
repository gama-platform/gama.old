/*******************************************************************************************************
 *
 * IGamaColors.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.resources;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static ummisco.gama.ui.resources.GamaColors.get;
import static ummisco.gama.ui.resources.GamaColors.system;

import org.eclipse.swt.SWT;

import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

/**
 * Class IGamaColors.
 *
 * @author drogoul
 * @since 24 nov. 2014
 *
 */
public interface IGamaColors {

	/** The blue. */
	GamaUIColor BLUE = isDark() ? get(get(92, 118, 161).lighter()) : get(92, 118, 161);
	// isDark() ? get(get(create("palette/palette.blue2")).lighter()) : get(create("palette/palette.blue2"));

	/** The error. */
	GamaUIColor ERROR = isDark() ? get(get(158, 77, 77).lighter()) : get(158, 77, 77);
	// isDark() ? get(get(create("palette/palette.red2")).lighter()) : get(create("palette/palette.red2"));

	/** The ok. */
	GamaUIColor OK = isDark() ? get(get(81, 135, 56).lighter()) : get(81, 135, 56);
	// isDark() ? get(get(create("palette/palette.green2")).lighter()) : get(create("palette/palette.green2"));

	/** The warning. */
	GamaUIColor WARNING = get(207, 119, 56); // get(create("palette/palette.orange2"));

	/** The neutral. */
	GamaUIColor NEUTRAL = get(102, 114, 126); // get(create("palette/palette.gray2"));

	/** The tooltip. */
	GamaUIColor TOOLTIP = get(248, 233, 100); // get(create("palette/palette.yellow2"));

	/** The gray label. */
	GamaUIColor GRAY_LABEL = get(136, 136, 136);

	/** The gray. */
	GamaUIColor GRAY = new GamaUIColor(system(SWT.COLOR_GRAY));

	/** The light gray. */
	GamaUIColor LIGHT_GRAY = get(200, 200, 200);

	/** The very light gray. */
	GamaUIColor VERY_LIGHT_GRAY = get(245, 245, 245);

	/** The dark gray. */
	GamaUIColor DARK_GRAY = get(100, 100, 100);

	/** The very dark gray. */
	GamaUIColor VERY_DARK_GRAY = get(50, 50, 50);

	/** The white. */
	GamaUIColor WHITE = new GamaUIColor(system(SWT.COLOR_WHITE), system(SWT.COLOR_WHITE));

	/** The black. */
	GamaUIColor BLACK = new GamaUIColor(system(SWT.COLOR_BLACK));

	/** The parameters background. */
	GamaUIColor PARAMETERS_BACKGROUND = isDark() ? get(120, 120, 120) : get(255, 255, 255);

	/** The dark orange. */
	GamaUIColor DARK_ORANGE = get(225, 92, 15);

	/** The widget background. */
	GamaUIColor WIDGET_BACKGROUND = get(system(SWT.COLOR_WIDGET_BACKGROUND));

	/** The widget foreground. */
	GamaUIColor WIDGET_FOREGROUND = get(system(SWT.COLOR_WIDGET_FOREGROUND));

	/** The list background. */
	GamaUIColor LIST_BACKGROUND = get(system(SWT.COLOR_LIST_BACKGROUND));

	/** The list foreground. */
	GamaUIColor LIST_FOREGROUND = get(system(SWT.COLOR_LIST_FOREGROUND));

}
