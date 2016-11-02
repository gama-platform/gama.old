/*********************************************************************************************
 *
 * 'IEventLayerListener.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

	public void mouseDown(int x, int y, int button);

	public void mouseUp(int x, int y, int button);

	public void mouseClicked(int x, int y, int button);

	public void mouseMove(int x, int y);

	public void mouseEnter(int x, int y);

	public void mouseExit(int x, int y);

	/**
	 * @param c
	 */
	void keyPressed(String c);

}
