/*********************************************************************************************
 *
 * 'ModelingPerspective.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ModelingPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		// layout.addPerspectiveShortcut(PerspectiveHelper.PERSPECTIVE_SIMULATION_ID);
		layout.setEditorAreaVisible(true);
	}
}
