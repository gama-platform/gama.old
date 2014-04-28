/*********************************************************************************************
 * 
 * 
 * 'OpenGamaWebsiteHandler.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.commands;

import msi.gama.gui.swt.ApplicationWorkbenchWindowAdvisor;
import org.eclipse.core.commands.*;

public class OpenGamaWebsiteHandler extends AbstractHandler {

	/**
	 * Method execute()
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ApplicationWorkbenchWindowAdvisor.openWebPage("https://code.google.com/p/gama-platform/", null);
		return null;
	}

}
