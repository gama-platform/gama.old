/*********************************************************************************************
 *
 * 'IGamaColors.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.resources;

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

	public static GamaUIColor BLUE = GamaColors.get(GamaIcons.create("palette.blue2")).validate();
	public static GamaUIColor BROWN = GamaColors.get(GamaIcons.create("palette.brown2")).validate();
	public static GamaUIColor ERROR = GamaColors.get(GamaIcons.create("palette.red2")).validate();
	public static GamaUIColor OK = GamaColors.get(GamaIcons.create("palette.green2")).validate();
	public static GamaUIColor IMPORTED = GamaColors.get(212, 147, 119).validate();
	public static GamaUIColor WARNING = GamaColors.get(GamaIcons.create("palette.orange2")).validate();
	public static GamaUIColor NEUTRAL = GamaColors.get(GamaIcons.create("palette.gray2")).validate();
	public static GamaUIColor TOOLTIP = GamaColors.get(GamaIcons.create("palette.yellow2")).validate();
	public static GamaUIColor GRAY_LABEL = GamaColors.get(0x88, 0x88, 0x88).validate();
	public static GamaUIColor VERY_LIGHT_GRAY = GamaColors.get(245, 245, 245).validate();
	public static GamaUIColor WHITE =
			new GamaUIColor(GamaColors.system(SWT.COLOR_WHITE), GamaColors.system(SWT.COLOR_WHITE)).validate();
	public static GamaUIColor BLACK = new GamaUIColor(GamaColors.system(SWT.COLOR_BLACK)).validate();
	public static GamaUIColor PARAMETERS_BACKGROUND = GamaColors.get(255, 255, 255).validate();

}
