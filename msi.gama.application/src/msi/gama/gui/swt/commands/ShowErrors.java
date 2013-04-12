package msi.gama.gui.swt.commands;

import java.util.Map;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.ErrorView;
import org.eclipse.core.commands.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

public class ShowErrors extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ErrorView.showErrors = !ErrorView.showErrors;
		ICommandService service =
			(ICommandService) HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(
				ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		if ( ErrorView.showErrors ) {
			GuiUtils.showView(ErrorView.ID, null);
		} else {
			GuiUtils.hideView(ErrorView.ID);
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(ErrorView.showErrors);
	}

}
