/*********************************************************************************************
 *
 *
 * 'ShowErrors.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.commands;

import java.util.Map;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import msi.gama.common.GamaPreferences;
import msi.gama.gui.views.ErrorView;
import msi.gama.runtime.GAMA;

public class ShowErrors extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GamaPreferences.CORE_SHOW_ERRORS.set(!GamaPreferences.CORE_SHOW_ERRORS.getValue());
		ICommandService service = HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		if ( GamaPreferences.CORE_SHOW_ERRORS.getValue() ) {
			GAMA.getGui().showView(ErrorView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
		} else {
			GAMA.getGui().hideView(ErrorView.ID);
		}
		return null;
	}

	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		element.setChecked(GamaPreferences.CORE_SHOW_ERRORS.getValue());
	}

}
