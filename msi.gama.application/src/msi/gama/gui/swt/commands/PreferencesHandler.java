package msi.gama.gui.swt.commands;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.GamaPreferencesView;
import org.eclipse.core.commands.*;

public class PreferencesHandler extends AbstractHandler {

	GamaPreferencesView view;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if ( view == null ) {
			view = new GamaPreferencesView(SwtGui.getShell());
		}
		view.open();

		return null;
	}

}
