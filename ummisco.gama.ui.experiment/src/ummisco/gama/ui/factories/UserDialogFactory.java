/*******************************************************************************************************
 *
 * UserDialogFactory.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.factories;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.runtime.IScope;
import msi.gaml.architecture.user.UserPanelStatement;
import ummisco.gama.ui.views.user.UserControlDialog;

/**
 * A factory for creating UserDialog objects.
 */
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
