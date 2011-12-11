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

import msi.gama.gui.application.views.IGamaView;
import msi.gama.interfaces.IOutput;
import org.eclipse.core.commands.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

public class ChangeRefreshRate extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IGamaView view = (IGamaView) HandlerUtil.getActivePart(event);
		IOutput output = view.getOutput();
		final InputDialog dlg =
			new InputDialog(Display.getCurrent().getActiveShell(), output.getName() +
				" refresh rate", "Number of steps between each refresh of " + output.getName(),
				String.valueOf(output.getRefreshRate()), null);
		if ( dlg.open() == Window.OK ) {
			output.setRefreshRate(Integer.valueOf(dlg.getValue()));
		}
		return null;
	}

}
