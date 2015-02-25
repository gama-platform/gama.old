/**
 * Created by drogoul, 24 nov. 2014
 * 
 */
package msi.gama.gui.swt;

import msi.gama.gui.swt.GamaColors.GamaUIColor;
import org.eclipse.swt.SWT;

/**
 * Class IGamaColors.
 * 
 * @author drogoul
 * @since 24 nov. 2014
 * 
 */
public interface IGamaColors {

	public static GamaUIColor BLUE = GamaColors.get(GamaIcons.create("palette.blue2"));
	public static GamaUIColor BROWN = GamaColors.get(GamaIcons.create("palette.brown2"));
	public static GamaUIColor ERROR = GamaColors.get(GamaIcons.create("palette.red2"));
	public static GamaUIColor OK = GamaColors.get(GamaIcons.create("palette.green2"));
	public static GamaUIColor IMPORTED = GamaColors.get(212, 147, 119);
	public static GamaUIColor WARNING = GamaColors.get(GamaIcons.create("palette.orange2"));
	public static GamaUIColor NEUTRAL = GamaColors.get(GamaIcons.create("palette.gray2"));
	public static GamaUIColor TOOLTIP = GamaColors.get(GamaIcons.create("palette.yellow2"));
	public static GamaUIColor GRAY_LABEL = GamaColors.get(0x88, 0x88, 0x88);
	public static GamaUIColor VERY_LIGHT_GRAY = GamaColors.get(245, 245, 245);
	public static GamaUIColor WHITE = new GamaUIColor(GamaColors.system(SWT.COLOR_WHITE),
		GamaColors.system(SWT.COLOR_WHITE));
	public static GamaUIColor BLACK = new GamaUIColor(GamaColors.system(SWT.COLOR_BLACK));
	public static GamaUIColor PARAMETERS_BACKGROUND = GamaColors.get(255, 255, 255);
	public static GamaUIColor PARAMETERS_EDITORS_BACKGROUND = GamaColors.get(240, 240, 240);

}
