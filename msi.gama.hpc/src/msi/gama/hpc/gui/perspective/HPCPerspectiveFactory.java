/*********************************************************************************************
 *
 *
 * 'HPCPerspectiveFactory.java', in plugin 'msi.gama.hpc', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.hpc.gui.perspective;

import org.eclipse.ui.*;
import msi.gama.common.interfaces.IGui;

public class HPCPerspectiveFactory implements IPerspectiveFactory {

	/** The Constant ID of the perspective */
	public static final String ID = IGui.HPC_PERSPECTIVE_ID;

	@Override
	public void createInitialLayout(final IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		System.out.println("stttaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		String editorId = layout.getEditorArea();
		layout.addView("msi.gama.hpc.gui.navigator.OutputNavigator", IPageLayout.LEFT, 0.23f, editorId);
		layout.addView("msi.gama.hpc.gui.experiment.navigator.ExperimentNavigator", IPageLayout.BOTTOM, 0.23f,
			editorId);
		// layout.getViewLayout("msi.gama.hpc.gui.experiment.navigator.ExperimentNavigator");
		// IViewLayout nav = layout.getViewLayout("msi.gama.hpc.chartView");
		// nav.setMoveable(false);

		layout.addView("msi.gama.hpc.chartView", IPageLayout.TOP, 0.70f, editorId);

		/*
		 * layout.addView("msi.gama.application.view.ConsoleView", IPageLayout.BOTTOM, 0.70f,
		 * "msi.gama.gui.view.GamaNavigator");
		 */
	}

}
