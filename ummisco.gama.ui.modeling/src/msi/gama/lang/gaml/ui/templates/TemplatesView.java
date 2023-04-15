/*******************************************************************************************************
 *
 * TemplatesView.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.templates;

import org.eclipse.jface.preference.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;

/**
 * The Class TemplatesView.
 */
public class TemplatesView extends ViewPart {

	/**
	 * Instantiates a new templates view.
	 */
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
