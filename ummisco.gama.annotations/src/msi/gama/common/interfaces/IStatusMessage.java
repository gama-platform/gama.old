/**
 * Created by drogoul, 5 nov. 2014
 *
 */
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

	public String getText();

	public int getCode();

	public Color getColor();

	public String getIcon();
}
