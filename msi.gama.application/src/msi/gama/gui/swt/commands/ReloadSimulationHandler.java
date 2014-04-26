/*********************************************************************************************
 * 
 *
 * 'ReloadSimulationHandler.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.commands;

import msi.gama.runtime.FrontEndController;
import msi.gama.runtime.GAMA;
import org.eclipse.core.commands.*;

public class ReloadSimulationHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		//hqnghi
				for ( FrontEndController s : GAMA.getControllers().values() ) {
					s.directPause();
				}
		//end-hqnghi
		GAMA.controller.userReload();
		return null;
	}

}
