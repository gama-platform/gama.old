/*******************************************************************************************************
 *
 * IEventLayerListener.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.outputs.layers;

/**
 * Class IEventLayerListener.
 *
 * @author drogoul
 * @since 28 avr. 2015
 *
 */
public interface IEventLayerListener {

	// x and y screen coordinates, button = 1 (left button) or 2 (right button)

	/**
	 * Mouse down.
	 *
	 * @param x the x
	 * @param y the y
	 * @param button the button
	 */
	void mouseDown(int x, int y, int button);

	/**
	 * Mouse up.
	 *
	 * @param x the x
	 * @param y the y
	 * @param button the button
	 */
	void mouseUp(int x, int y, int button);

	/**
	 * Mouse clicked.
	 *
	 * @param x the x
	 * @param y the y
	 * @param button the button
	 */
	void mouseClicked(int x, int y, int button);

	/**
	 * Mouse move.
	 *
	 * @param x the x
	 * @param y the y
	 */
	void mouseMove(int x, int y);

	/**
	 * Mouse enter.
	 *
	 * @param x the x
	 * @param y the y
	 */
	void mouseEnter(int x, int y);

	/**
	 * Mouse exit.
	 *
	 * @param x the x
	 * @param y the y
	 */
	void mouseExit(int x, int y);

	/**
	 * Mouse menu.
	 *
	 * @param x the x
	 * @param y the y
	 */
	void mouseMenu(int x, int y);

	/**
	 * @param c
	 */
	void keyPressed(String c);

}
