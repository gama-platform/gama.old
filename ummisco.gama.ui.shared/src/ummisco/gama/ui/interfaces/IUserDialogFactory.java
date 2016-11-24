/*********************************************************************************************
 *
 * 'IUserDialogFactory.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.interfaces;

import msi.gama.runtime.IScope;
import msi.gaml.architecture.user.UserPanelStatement;

public interface IUserDialogFactory {

	void openUserDialog(IScope scope, UserPanelStatement panel);

	void closeUserDialog();
}