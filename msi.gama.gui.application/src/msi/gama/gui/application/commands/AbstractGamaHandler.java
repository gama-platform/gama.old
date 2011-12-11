/**
 * 
 */
package msi.gama.gui.application.commands;

import org.eclipse.core.commands.*;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Written by drogoul
 * Modified on 9 nov. 2011
 * 
 * @todo Description
 * 
 */
public abstract class AbstractGamaHandler extends AbstractHandler {

	protected IWorkbenchPart getView(final ExecutionEvent event, final Class type) {
		IWorkbenchPart view = HandlerUtil.getActivePart(event);
		if ( type.isAssignableFrom(view.getClass()) ) { return view; }
		return null;
	}
}
