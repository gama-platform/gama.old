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

import org.eclipse.swt.SWT;

import msi.gama.application.workbench.ThemeHelper;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

/**
 * Class IGamaColors.
 *
 * @author drogoul
 * @since 24 nov. 2014
 *
 */
public interface IGamaColors {

	GamaUIColor BLUE = GamaColors.get(GamaIcons.create("palette/palette.blue2")).validate();
	GamaUIColor ERROR = GamaColors.get(GamaIcons.create("palette/palette.red2")).validate();
	GamaUIColor OK = GamaColors.get(GamaIcons.create("palette/palette.green2")).validate();
	GamaUIColor WARNING = GamaColors.get(GamaIcons.create("palette/palette.orange2")).validate();
	GamaUIColor NEUTRAL = GamaColors.get(GamaIcons.create("palette/palette.gray2")).validate();
	GamaUIColor TOOLTIP = GamaColors.get(GamaIcons.create("palette/palette.yellow2")).validate();
	GamaUIColor GRAY_LABEL = GamaColors.get(0x88, 0x88, 0x88).validate();
	GamaUIColor VERY_LIGHT_GRAY = GamaColors.get(245, 245, 245).validate();
	GamaUIColor VERY_DARK_GRAY = GamaColors.get(20, 20, 20).validate();
	GamaUIColor WHITE =
			new GamaUIColor(GamaColors.system(SWT.COLOR_WHITE), GamaColors.system(SWT.COLOR_WHITE)).validate();
	GamaUIColor BLACK = new GamaUIColor(GamaColors.system(SWT.COLOR_BLACK)).validate();
	GamaUIColor PARAMETERS_BACKGROUND =
			(ThemeHelper.isDark() ? GamaColors.get(120, 120, 120) : GamaColors.get(255, 255, 255)).validate();
	GamaUIColor DARK_ORANGE = GamaColors.get(225, 92, 15).validate();

}
