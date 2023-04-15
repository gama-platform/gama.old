/*******************************************************************************************************
 *
 * DetailsHandler.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.commands;

import static ummisco.gama.ui.utils.WorkbenchHelper.getCommand;
import static ummisco.gama.ui.utils.WorkbenchHelper.runCommand;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.internal.AbstractEnabledHandler;

/**
 * The Class DetailsHandler.
 */
public class DetailsHandler extends AbstractEnabledHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		return runCommand(getCommand("org.eclipse.equinox.p2.ui.sdk.installationDetails"), event);
	}

}
