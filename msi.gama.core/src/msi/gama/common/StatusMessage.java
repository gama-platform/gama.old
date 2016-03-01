/**
 * Created by drogoul, 4 nov. 2014
 *
 */
package msi.gama.common;

import msi.gama.common.interfaces.IGui;
import msi.gama.util.GamaColor;

public class StatusMessage implements IStatusMessage {

	String message = "";
	protected int code = IGui.INFORM;

	public StatusMessage(final String msg, final int s) {
		message = msg;
		code = s;
	}

	@Override
	public boolean isEmpty() {
		return message == null;
	}

	@Override
	public String getText() {
		return message;
	}

	@Override
	public int getCode() {
		return code;
	}

	/**
	 * Method getColor()
	 * @see msi.gama.common.IStatusMessage#getColor()
	 */
	@Override
	public GamaColor getColor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.IStatusMessage#getIcon()
	 */
	@Override
	public String getIcon() {
		return null;
	}

}