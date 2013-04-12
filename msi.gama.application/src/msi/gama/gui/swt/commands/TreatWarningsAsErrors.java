package msi.gama.gui.swt.commands;

import java.util.Map;
import msi.gama.runtime.GAMA;
import org.eclipse.core.commands.*;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

public class TreatWarningsAsErrors extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GAMA.TREAT_WARNINGS_AS_ERRORS = !GAMA.TREAT_WARNINGS_AS_ERRORS;
		ICommandService service =
			(ICommandService) HandlerUtil.getActiveWorkbenchWindowChecked(event).getService(
				ICommandService.class);
		service.refreshElements(event.getCommand().getId(), null);
		return null;
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked(GAMA.TREAT_WARNINGS_AS_ERRORS);
	}

}
