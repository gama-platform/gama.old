/**
 * Created by drogoul, 5 nov. 2014
 * 
 */
package msi.gama.common;

import msi.gama.common.util.ThreadedUpdater.IUpdaterMessage;

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
}
