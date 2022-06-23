/*******************************************************************************************************
 *
 * UpdateHandler.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

import msi.gama.runtime.GAMA;

/**
 * The Class UpdateHandler.
 */
public class UpdateHandler extends AbstractEnabledHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		runCommand(getCommand("org.eclipse.equinox.p2.ui.sdk.update"), event);
		GAMA.getGui().refreshNavigator();
		return this;
	}

}
