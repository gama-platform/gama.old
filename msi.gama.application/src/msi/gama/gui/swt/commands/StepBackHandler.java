package msi.gama.gui.swt.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import msi.gama.runtime.GAMA;

public class StepBackHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GAMA.stepBackFrontmostExperiment();
		return this;
	}

}
