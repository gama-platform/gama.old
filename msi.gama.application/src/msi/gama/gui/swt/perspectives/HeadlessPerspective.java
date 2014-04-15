/*********************************************************************************************
 * 
 *
 * 'HeadlessPerspective.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.perspectives;

import org.eclipse.ui.*;

public class HeadlessPerspective implements IPerspectiveFactory {

	/** The Constant ID of the perspective */
	public static final String ID = "msi.gama.application.perspectives.HeadlessPerspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView("msi.gama.application.view.HeadlessParam", IPageLayout.RIGHT, 0.23f, layout.getEditorArea());


	}


}
