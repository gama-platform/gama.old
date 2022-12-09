/*******************************************************************************************************
 *
 * IStatusMessage.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import java.awt.Color;

/**
 * Class IStatusMessage.
 *
 * @author drogoul
 * @since 5 nov. 2014
 *
 */
public interface IStatusMessage extends IUpdaterMessage {

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText();

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode();

	/**
	 * Gets the color.
	 *
	 * @return the color
	 */
	public Color getColor();

	/**
	 * Gets the icon.
	 *
	 * @return the icon
	 */
	public String getIcon();
}
