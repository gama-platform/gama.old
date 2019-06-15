/*********************************************************************************************
 *
 * 'Messages.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;

import ummisco.gama.ui.utils.WorkbenchHelper;

public class Messages {

	public static void error(final String error) {
		WorkbenchHelper.run(() -> MessageDialog.openError(WorkbenchHelper.getShell(), "Error", error));
	}

	public static void tell(final String error) {
		WorkbenchHelper.run(() -> MessageDialog.openInformation(WorkbenchHelper.getShell(), "Message", error));
	}

	public static boolean question(final String title, final String message) {
		return MessageDialog.openQuestion(WorkbenchHelper.getShell(), title, message);
	}

	public static boolean confirm(final String title, final String message) {
		return MessageDialog.openConfirm(WorkbenchHelper.getShell(), title, message);
	}

}
