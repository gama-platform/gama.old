package msi.gama.gui.swt.commands;

import java.util.Map;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.ErrorView;
import org.eclipse.core.commands.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

public class ShowErrors extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GamaPreferences.CORE_SHOW_ERRORS.set(!GamaPreferences.CORE_SHOW_ERRORS.getValue());
		ICommandService service =
			(ICommandService) HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		if ( GamaPreferences.CORE_SHOW_ERRORS.getValue() ) {
			GuiUtils.showView(ErrorView.ID, null, IWorkbenchPage.VIEW_VISIBLE);
		} else {
			GuiUtils.hideView(ErrorView.ID);
		}
		return null;
	}

	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		element.setChecked(GamaPreferences.CORE_SHOW_ERRORS.getValue());
	}

}
