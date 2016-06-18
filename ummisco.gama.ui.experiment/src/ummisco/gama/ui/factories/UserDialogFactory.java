package ummisco.gama.ui.factories;

import msi.gama.runtime.IScope;
import msi.gaml.architecture.user.UserPanelStatement;
import ummisco.gama.ui.views.user.UserControlDialog;

public class UserDialogFactory implements ummisco.gama.ui.utils.SwtGui.IUserDialogFactory {

	@Override
	public void openUserDialog(final IScope scope, final UserPanelStatement panel) {
		final UserControlDialog dialog = new UserControlDialog(scope, panel);
		dialog.open();
	}

	@Override
	public void closeUserDialog() {
		final UserControlDialog d = UserControlDialog.current;
		if (d != null) {
			d.close();
		}
	}

}
