package ummisco.gama.ui.interfaces;

import msi.gama.runtime.IScope;
import msi.gaml.architecture.user.UserPanelStatement;

public interface IUserDialogFactory {

	void openUserDialog(IScope scope, UserPanelStatement panel);

	void closeUserDialog();
}