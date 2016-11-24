/*********************************************************************************************
 *
 * 'ClipboardTester.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.ui.PlatformUI;

public class ClipboardTester extends PropertyTester {

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		final Clipboard clipBoard = new Clipboard(PlatformUI.getWorkbench().getDisplay());
		final FileTransfer transfer = FileTransfer.getInstance();
		final String[] selection = (String[]) clipBoard.getContents(transfer);
		return selection != null;
	}

}
