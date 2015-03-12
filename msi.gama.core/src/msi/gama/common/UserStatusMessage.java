/**
 * Created by drogoul, 11 mars 2015
 * 
 */
package msi.gama.common;

import msi.gama.common.interfaces.IGui;
import msi.gama.util.GamaColor;

/**
 * Class UserStatusMessage.
 * 
 * @author drogoul
 * @since 11 mars 2015
 * 
 */
public class UserStatusMessage extends StatusMessage {

	GamaColor color;

	/**
	 * @param msg
	 * @param color
	 */
	public UserStatusMessage(final String msg, final GamaColor color) {
		super(msg, IGui.USER);
		this.color = color;
	}

	@Override
	public GamaColor getColor() {
		return color;
	}

}
