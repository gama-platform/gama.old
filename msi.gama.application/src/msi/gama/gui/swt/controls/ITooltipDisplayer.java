/**
 * Created by drogoul, 8 déc. 2014
 * 
 */
package msi.gama.gui.swt.controls;

import msi.gama.gui.swt.GamaColors.GamaUIColor;

/**
 * The class ITooltipDisplayer. 
 *
 * @author drogoul
 * @since 8 déc. 2014
 *
 */
public interface ITooltipDisplayer {

	public abstract void stopDisplayingTooltips();

	public abstract void displayTooltip(String text, GamaUIColor color);

}