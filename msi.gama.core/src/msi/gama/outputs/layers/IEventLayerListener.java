/*******************************************************************************************************
 *
 * IEventLayerListener.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/** The Constant MOUSE_PRESS. */
	int MOUSE_PRESS = 0;

	/** The Constant MOUSE_RELEASED. */
	int MOUSE_RELEASED = 1;

	/** The Constant MOUSE_CLICKED. */
	int MOUSE_CLICKED = 2;

	/** The Constant MOUSE_MOVED. */
	int MOUSE_MOVED = 4;

	/** The Constant MOUSE_ENTERED. */
	int MOUSE_ENTERED = 5;

	/** The Constant MOUSE_EXITED. */
	int MOUSE_EXITED = 6;

	/** The Constant MOUSE_MENU. */
	int MOUSE_MENU = 7;

	/** The Constant MOUSE_DRAGGED. */
	int MOUSE_DRAGGED = 8;

	/** The Constant KEY_PRESSED. */
	int KEY_PRESSED = 3;

	/** The arrow left. */
	int ARROW_LEFT = 10;

	/** The arrow right. */
	int ARROW_RIGHT = 11;

	/** The arrow up. */
	int ARROW_UP = 12;

	/** The arrow down. */
	int ARROW_DOWN = 13;

	/** The key tab. */
	int KEY_TAB = 14;

	/** The key esc. */
	int KEY_ESC = 15;

	/** The key page up. */
	int KEY_PAGE_UP = 16;

	/** The key page down. */
	int KEY_PAGE_DOWN = 17;

	/** The key return. */
	int KEY_RETURN = 18;

	// x and y screen coordinates, button = 1 (left button) or 2 (right button)

	/**
	 * Mouse down.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param button
	 *            the button
	 */
	void mouseDown(int x, int y, int button);

	/**
	 * Mouse up.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param button
	 *            the button
	 */
	void mouseUp(int x, int y, int button);

	/**
	 * Mouse clicked.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param button
	 *            the button
	 */
	void mouseClicked(int x, int y, int button);

	/**
	 * Mouse move.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void mouseMove(int x, int y);

	/**
	 * Mouse drag.
	 *
	 * @param x the x
	 * @param y the y
	 */
	void mouseDrag(int x, int y, int button);


	/**
	 * Mouse enter.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void mouseEnter(int x, int y);

	/**
	 * Mouse exit.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void mouseExit(int x, int y);

	/**
	 * Mouse menu.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	void mouseMenu(int x, int y);

	/**
	 * @param c
	 */
	void keyPressed(String c);

	/**
	 * Special key pressed.
	 *
	 * @param keyCode
	 *            the key code
	 */
	void specialKeyPressed(int keyCode);

}
