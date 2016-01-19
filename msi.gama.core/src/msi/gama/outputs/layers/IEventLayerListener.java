/**
 * Created by drogoul, 28 avr. 2015
 *
 */
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

	/**
	 * @param c
	 */
	void keyPressed(String c);

}
