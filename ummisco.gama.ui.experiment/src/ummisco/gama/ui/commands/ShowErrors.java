/*********************************************************************************************
 *
 * 'ShowErrors.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import msi.gama.common.GamaPreferences;
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.ErrorView;

public class ShowErrors extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GamaPreferences.CORE_SHOW_ERRORS.set(!GamaPreferences.CORE_SHOW_ERRORS.getValue());
		final ICommandService service = HandlerUtil.getActiveWorkbenchWindowChecked(event)
				.getService(ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		if (GamaPreferences.CORE_SHOW_ERRORS.getValue()) {
			GAMA.getGui().showView(ErrorView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
		} else {
			WorkbenchHelper.hideView(ErrorView.ID);
		}
		return null;
	}

	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		element.setChecked(GamaPreferences.CORE_SHOW_ERRORS.getValue());
	}

}
