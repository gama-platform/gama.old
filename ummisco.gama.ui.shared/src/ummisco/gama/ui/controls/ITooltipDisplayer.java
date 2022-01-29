/*******************************************************************************************************
 *
 * ITooltipDisplayer.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

/**
 * The class ITooltipDisplayer. 
 *
 * @author drogoul
 * @since 8 déc. 2014
 *
 */
public interface ITooltipDisplayer {

	/**
	 * Stop displaying tooltips.
	 */
	public abstract void stopDisplayingTooltips();

	/**
	 * Display tooltip.
	 *
	 * @param text the text
	 * @param color the color
	 */
	public abstract void displayTooltip(String text, GamaUIColor color);

}