package msi.gama.gui.swt.commands;

import java.util.Map;
import msi.gama.common.GamaPreferences;
import org.eclipse.core.commands.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

public class ErrorsPauseAndEdit extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GamaPreferences.CORE_REVEAL_AND_STOP.set(!GamaPreferences.CORE_REVEAL_AND_STOP.getValue());
		ICommandService service =
			(ICommandService) HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		return null;
	}

	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		element.setChecked(GamaPreferences.CORE_REVEAL_AND_STOP.getValue());
	}

}
