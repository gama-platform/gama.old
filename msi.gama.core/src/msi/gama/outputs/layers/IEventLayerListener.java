/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.IEventLayerListener.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	void mouseDown(int x, int y, int button);

	void mouseUp(int x, int y, int button);

	void mouseClicked(int x, int y, int button);

	void mouseMove(int x, int y);

	void mouseEnter(int x, int y);

	void mouseExit(int x, int y);

	void mouseMenu(int x, int y);

	/**
	 * @param c
	 */
	void keyPressed(String c);

}
