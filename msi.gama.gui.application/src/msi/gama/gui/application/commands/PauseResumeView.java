/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.commands;

import msi.gama.gui.application.views.*;
import msi.gama.interfaces.IOutput;
import org.eclipse.core.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

public class PauseResumeView extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ViewSourceProvider state =
			(ViewSourceProvider) ((ISourceProviderService) HandlerUtil.getActiveWorkbenchWindow(
				event).getService(ISourceProviderService.class))
				.getSourceProvider(ViewSourceProvider.var);
		IGamaView view = (IGamaView) HandlerUtil.getActivePart(event);
		IOutput output = view.getOutput();

		if ( output != null ) {
			if ( output.isPaused() ) {
				output.resume();
				state.changeState(false);
			} else {
				output.pause();
				state.changeState(true);
			}
		}
		return null;

	}

}
