/*********************************************************************************************
 *
 * 'StepByStepHandler.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import org.eclipse.core.commands.*;
import msi.gama.runtime.GAMA;

public class StepByStepHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GAMA.stepFrontmostExperiment();
		return this;
	}

}
