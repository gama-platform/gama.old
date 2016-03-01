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
	String icon;

	/**
	 * @param msg
	 * @param color
	 */
	public UserStatusMessage(final String msg, final GamaColor color, final String icon) {
		super(msg, IGui.USER);
		this.color = color;
		this.icon = icon;
	}

	@Override
	public GamaColor getColor() {
		return color;
	}

	@Override
	public String getIcon() {
		return icon;
	}

}
