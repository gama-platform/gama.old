/*********************************************************************************************
 *
 * 'TemplatesView.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.templates;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

public class TemplatesView extends ViewPart {

	public TemplatesView() {}

	@Override
	public void createPartControl(final Composite parent) {
		PreferenceDialog dialog =
			PreferencesUtil.createPreferenceDialogOn(parent.getShell(), "msi.gama.lang.gaml.Gaml.templates",
				new String[] {}, null);
		PreferencePage selectedPage = (PreferencePage) dialog.getSelectedPage();
		selectedPage.createControl(parent);
	}

	@Override
	public void setFocus() {}

}
