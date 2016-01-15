/*********************************************************************************************
 *
 *
 * 'SimulationPerspective.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.perspectives;

import org.eclipse.ui.*;

public class SimulationPerspective implements IPerspectiveFactory {

	/** The Constant ID of the perspective */
	public static final String ID = "msi.gama.application.perspectives.SimulationPerspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		layout.setFixed(false);
		String editorId = layout.getEditorArea();
		layout.addView("msi.gama.gui.view.GamaNavigator", IPageLayout.LEFT, 0.23f, editorId);
		// IViewLayout nav = layout.getViewLayout("msi.gama.gui.view.GamaNavigator");
		// nav.setMoveable(false);
		//
		IPlaceholderFolderLayout displays =
			layout.createPlaceholderFolder("layersFolder", IPageLayout.TOP, 0.7f, editorId);

		displays.addPlaceholder("msi.gama.application.view.LayeredDisplayView:*");

		IPlaceholderFolderLayout inspectorsFolder =
			layout.createPlaceholderFolder("inspectorsFolder", IPageLayout.RIGHT, 0.52f, "layersFolder");

		inspectorsFolder.addPlaceholder("msi.gama.application.view.ParameterView");
		inspectorsFolder.addPlaceholder("msi.gama.application.view.PopulationInspectView:*");
		inspectorsFolder.addPlaceholder("msi.gama.application.view.AgentInspectView");

		IPlaceholderFolderLayout inspectorsFolder2 =
			layout.createPlaceholderFolder("inspectorsFolder2", IPageLayout.BOTTOM, 0.50f, "inspectorsFolder");

		inspectorsFolder2.addPlaceholder("msi.gama.application.view.MonitorView");
		inspectorsFolder.addPlaceholder("msi.gama.application.view.ErrorView");
		layout.addView("msi.gama.application.view.ConsoleView", IPageLayout.BOTTOM, 0.70f,
			"msi.gama.gui.view.GamaNavigator");

		layout.setEditorAreaVisible(false);

	}

}
