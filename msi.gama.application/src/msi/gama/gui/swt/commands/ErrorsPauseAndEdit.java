package msi.gama.gui.swt.commands;

import java.util.Map;
import msi.gama.runtime.GAMA;
import org.eclipse.core.commands.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

public class ErrorsPauseAndEdit extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GAMA.TREAT_ERRORS_AS_FATAL = !GAMA.TREAT_ERRORS_AS_FATAL;
		ICommandService service =
			(ICommandService) HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(
				ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(GAMA.TREAT_ERRORS_AS_FATAL);
	}

}
