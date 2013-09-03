package msi.gama.gui.swt.commands;

import msi.gama.gui.views.GamaPreferencesView;
import org.eclipse.core.commands.*;

public class PreferencesHandler extends AbstractHandler {

	GamaPreferencesView view;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		GamaPreferencesView.show();

		return null;
	}

}
