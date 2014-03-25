/**
 * Created by drogoul, 10 mars 2014
 * 
 */
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