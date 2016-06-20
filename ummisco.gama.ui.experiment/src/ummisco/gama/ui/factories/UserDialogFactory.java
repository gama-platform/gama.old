package ummisco.gama.ui.factories;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.runtime.IScope;
import msi.gaml.architecture.user.UserPanelStatement;
import ummisco.gama.ui.views.user.UserControlDialog;

public class UserDialogFactory extends AbstractServiceFactory implements ummisco.gama.ui.interfaces.IUserDialogFactory {

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

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

}
