/*******************************************************************************************************
 *
 * ShowInteractiveConsole.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import static msi.gama.common.interfaces.IGui.INTERACTIVE_CONSOLE_VIEW_ID;
import static org.eclipse.ui.IWorkbenchPage.VIEW_VISIBLE;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import msi.gama.runtime.GAMA;

/**
 * The Class ShowInteractiveConsole.
 */
public class ShowInteractiveConsole extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		return GAMA.getGui().showView(null, INTERACTIVE_CONSOLE_VIEW_ID, null, VIEW_VISIBLE);
	}

}
