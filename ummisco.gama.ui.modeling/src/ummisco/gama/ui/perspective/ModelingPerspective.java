/*******************************************************************************************************
 *
 * ModelingPerspective.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import msi.gama.common.interfaces.IGui;

/**
 * The Class ModelingPerspective.
 */
public class ModelingPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		layout.addShowViewShortcut(IGui.INTERACTIVE_CONSOLE_VIEW_ID);
		layout.setEditorAreaVisible(true);
	}
}
