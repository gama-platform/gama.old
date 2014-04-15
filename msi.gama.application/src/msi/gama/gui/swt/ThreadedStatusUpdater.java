/*********************************************************************************************
 * 
 *
 * 'ThreadedStatusUpdater.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.util.*;
import msi.gama.common.util.ThreadedUpdater.IUpdaterMessage;
import msi.gama.gui.swt.ThreadedStatusUpdater.StatusMessage;

public class ThreadedStatusUpdater extends ThreadedUpdater<StatusMessage> {

	public static class StatusMessage implements IUpdaterMessage {

		String message = "";
		int code = IGui.INFORM;

		StatusMessage(final String msg, final int s) {
			message = msg;
			code = s;
		}

		@Override
		public boolean isEmpty() {
			return message == null;
		}

		public String getText() {
			return message;
		}

		public int getCode() {
			return code;
		}
	}

}