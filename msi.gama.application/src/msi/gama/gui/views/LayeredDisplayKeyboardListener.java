/**
 * Created by drogoul, 19 janv. 2016
 *
 */
package msi.gama.gui.views;

import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.outputs.layers.IEventLayerListener;

/**
 * Class LayeredDisplayKeyboardListener.
 *
 * @author drogoul
 * @since 19 janv. 2016
 *
 */
public class LayeredDisplayKeyboardListener implements Listener {

	IDisplaySurface surface;

	/**
	 *
	 */
	public LayeredDisplayKeyboardListener(final IDisplaySurface surface) {
		this.surface = surface;
	}

	/**
	 * Method handleEvent()
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void handleEvent(final Event e) {
		System.out.println("Global key handler : " + e.character);
		e.doit = false;
		new Thread(new Runnable() {

			@Override
			public void run() {
				for ( IEventLayerListener listener : surface.getLayerListeners() ) {
					listener.keyPressed(String.valueOf(e.character));
				}
			}
		}).start();

	}

}
