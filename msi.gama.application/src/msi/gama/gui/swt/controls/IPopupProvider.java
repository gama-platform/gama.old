/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.swt.controls;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Shell;

/**
 * The class IPopupProvider.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public interface IPopupProvider {

	public String getPopupText();

	public Shell getControllingShell();

	public Color getPopupBackground();

	public Point getAbsoluteOrigin();

}
