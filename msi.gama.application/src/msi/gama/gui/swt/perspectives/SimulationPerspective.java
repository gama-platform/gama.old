/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.perspectives;

import org.eclipse.ui.*;

public class SimulationPerspective implements IPerspectiveFactory {

	/** The Constant ID of the perspective */
	public static final String ID = "msi.gama.application.perspectives.SimulationPerspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		String editorId = layout.getEditorArea();
		layout.addView("msi.gama.gui.view.GamaNavigator", IPageLayout.LEFT, 0.23f, editorId);
		IViewLayout nav = layout.getViewLayout("msi.gama.gui.view.GamaNavigator");
		nav.setMoveable(false);

		IPlaceholderFolderLayout layersFolder =
			layout.createPlaceholderFolder("layersFolder", IPageLayout.TOP, 0.62f, editorId);

		layersFolder.addPlaceholder("msi.gama.application.view.LayeredDisplayView:*");

		IPlaceholderFolderLayout inspectorsFolder =
			layout.createPlaceholderFolder("inspectorsFolder", IPageLayout.RIGHT, 0.52f, "layersFolder");

		inspectorsFolder.addPlaceholder("msi.gama.application.view.SpeciesInspectView");
		inspectorsFolder.addPlaceholder("msi.gama.application.view.MonitorView");

		IPlaceholderFolderLayout inspectorsFolder2 =
			layout.createPlaceholderFolder("inspectorsFolder2", IPageLayout.BOTTOM, 0.50f, "inspectorsFolder");

		inspectorsFolder2.addPlaceholder("msi.gama.application.view.AgentInspectView");
		inspectorsFolder.addPlaceholder("msi.gama.application.view.ParameterView");
		inspectorsFolder.addPlaceholder("msi.gama.application.view.ErrorView");
		layout.addView("msi.gama.application.view.ConsoleView", IPageLayout.BOTTOM, 0.70f,
			"msi.gama.gui.view.GamaNavigator");

	}

}
