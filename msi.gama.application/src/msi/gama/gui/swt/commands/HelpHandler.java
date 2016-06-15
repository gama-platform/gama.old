/*********************************************************************************************
 *
 *
 * 'HelpHandler.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.commands;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import msi.gama.gui.swt.SwtGui;

public class HelpHandler extends AbstractHandler {

	public HelpHandler() {}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		try {
			SwtGui.showWeb2Editor(new URL("http://doc.gama-platform.org"));
		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
