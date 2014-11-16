/**
 * Created by drogoul, 4 nov. 2014
 * 
 */
package msi.gama.common;

import msi.gama.common.interfaces.IGui;

public class StatusMessage implements IStatusMessage {

	String message = "";
	int code = IGui.INFORM;

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
}