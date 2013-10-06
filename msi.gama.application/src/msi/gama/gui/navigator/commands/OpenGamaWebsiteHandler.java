package msi.gama.gui.navigator.commands;

import msi.gama.gui.swt.ApplicationWorkbenchWindowAdvisor;
import org.eclipse.core.commands.*;

public class OpenGamaWebsiteHandler extends AbstractHandler {

	/**
	 * Method execute()
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ApplicationWorkbenchWindowAdvisor.openGamaWebPage();
		return null;
	}

}
