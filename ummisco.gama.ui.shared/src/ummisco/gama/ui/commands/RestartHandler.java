package ummisco.gama.ui.commands;

import static ummisco.gama.ui.utils.WorkbenchHelper.getCommand;
import static ummisco.gama.ui.utils.WorkbenchHelper.runCommand;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.internal.AbstractEnabledHandler;

public class RestartHandler extends AbstractEnabledHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		return runCommand(getCommand("org.eclipse.ui.file.restartWorkbench"), event);
	}

}
