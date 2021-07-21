/*********************************************************************************************
 *
 * 'IGamaColors.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.resources;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static ummisco.gama.ui.resources.GamaColors.get;
import static ummisco.gama.ui.resources.GamaColors.system;
import static ummisco.gama.ui.resources.GamaIcons.create;

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

	GamaUIColor BLUE = isDark() ? GamaColors.get(get(create("palette/palette.blue2")).lighter())
			: get(create("palette/palette.blue2"));
	GamaUIColor ERROR = isDark() ? GamaColors.get(get(create("palette/palette.red2")).lighter())
			: get(create("palette/palette.red2"));
	GamaUIColor OK = isDark() ? GamaColors.get(get(create("palette/palette.green2")).lighter())
			: get(create("palette/palette.green2"));
	GamaUIColor WARNING = get(create("palette/palette.orange2"));
	GamaUIColor NEUTRAL = get(create("palette/palette.gray2"));
	GamaUIColor TOOLTIP = get(create("palette/palette.yellow2"));
	GamaUIColor GRAY_LABEL = get(136, 136, 136);
	GamaUIColor GRAY = new GamaUIColor(system(SWT.COLOR_GRAY));
	GamaUIColor LIGHT_GRAY = get(200, 200, 200);
	GamaUIColor VERY_LIGHT_GRAY = get(245, 245, 245);
	GamaUIColor DARK_GRAY = get(100, 100, 100);
	GamaUIColor VERY_DARK_GRAY = get(50, 50, 50);
	GamaUIColor WHITE = new GamaUIColor(system(SWT.COLOR_WHITE), system(SWT.COLOR_WHITE));
	GamaUIColor BLACK = new GamaUIColor(system(SWT.COLOR_BLACK));
	GamaUIColor PARAMETERS_BACKGROUND = isDark() ? get(120, 120, 120) : get(255, 255, 255);
	GamaUIColor DARK_ORANGE = get(225, 92, 15);

}
