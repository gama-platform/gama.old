/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.swt.controls;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

/**
 * The class IPopupProvider.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public interface IPopupProvider {

	public String getPopupText();

	public Control getPositionControl();

	public Color getPopupBackground();

}
