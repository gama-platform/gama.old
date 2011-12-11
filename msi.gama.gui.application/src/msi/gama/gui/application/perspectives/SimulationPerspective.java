/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.perspectives;

import org.eclipse.ui.*;

public class SimulationPerspective implements IPerspectiveFactory {

	/** The Constant ID of the perspective */
	public static final String ID = "msi.gama.gui.application.perspectives.SimulationPerspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		String editorId = layout.getEditorArea();
		layout.addView("msi.gama.gui.view.GamaNavigator", IPageLayout.LEFT, 0.23f, editorId);
		IViewLayout nav = layout.getViewLayout("msi.gama.gui.view.GamaNavigator");
		nav.setMoveable(false);

		IPlaceholderFolderLayout layersFolder =
			layout.createPlaceholderFolder("layersFolder", IPageLayout.TOP, 0.62f, editorId);

		layersFolder.addPlaceholder("msi.gama.gui.application.view.LayeredDisplayView:*");

		IPlaceholderFolderLayout inspectorsFolder =
			layout.createPlaceholderFolder("inspectorsFolder", IPageLayout.RIGHT, 0.52f,
				"layersFolder");

		inspectorsFolder.addPlaceholder("msi.gama.gui.application.view.SpeciesInspectView");
		inspectorsFolder.addPlaceholder("msi.gama.gui.application.view.MonitorView");

		IPlaceholderFolderLayout inspectorsFolder2 =
			layout.createPlaceholderFolder("inspectorsFolder2", IPageLayout.BOTTOM, 0.50f,
				"inspectorsFolder");

		inspectorsFolder2.addPlaceholder("msi.gama.gui.application.view.AgentInspectView");
		inspectorsFolder.addPlaceholder("msi.gama.gui.application.view.ParameterView");
		inspectorsFolder.addPlaceholder("msi.gama.gui.application.view.ErrorView");
		layout.addView("msi.gama.gui.application.view.ConsoleView", IPageLayout.BOTTOM, 0.70f,
			"msi.gama.gui.view.GamaNavigator");

	}

}
