/**
 * Created by drogoul, 5 nov. 2014
 *
 */
package msi.gama.common;

import msi.gama.common.interfaces.IUpdaterMessage;
import msi.gama.util.GamaColor;

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

	public GamaColor getColor();

	public String getIcon();
}
