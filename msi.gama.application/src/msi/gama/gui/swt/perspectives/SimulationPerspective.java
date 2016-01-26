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
import msi.gama.common.interfaces.IGui;

public class SimulationPerspective implements IPerspectiveFactory {

	/** The Constant ID of the perspective */
	public static final String ID = "msi.gama.application.perspectives.SimulationPerspective";

	@Override
	public void createInitialLayout(final IPageLayout lay) {

		lay.setFixed(false);
		lay.setEditorAreaVisible(false);
		String editor = lay.getEditorArea();

		IFolderLayout navigAndParam = lay.createFolder("navigAndParam", IPageLayout.LEFT, 0.3f, editor);
		navigAndParam.addView(IGui.PARAMETER_VIEW_ID);
		navigAndParam.addView(IGui.NAVIGATOR_VIEW_ID);
		navigAndParam.addPlaceholder(IGui.ERROR_VIEW_ID);

		lay.addView(IGui.CONSOLE_VIEW_ID, IPageLayout.BOTTOM, 0.70f, "navigAndParam");

		IPlaceholderFolderLayout displays = lay.createPlaceholderFolder("displays", IPageLayout.TOP, 0.7f, editor);
		displays.addPlaceholder(IGui.LAYER_VIEW_ID + ":*");
		displays.addPlaceholder(IGui.GL_LAYER_VIEW_ID + ":*");

		IPlaceholderFolderLayout inspect = lay.createPlaceholderFolder("inspect", IPageLayout.RIGHT, 0.6f, "displays");
		inspect.addPlaceholder(IGui.AGENT_VIEW_ID);
		inspect.addPlaceholder(IGui.TABLE_VIEW_ID + ":*");

		IPlaceholderFolderLayout monitor = lay.createPlaceholderFolder("monitor", IPageLayout.BOTTOM, 0.50f, "inspect");
		monitor.addPlaceholder(IGui.MONITOR_VIEW_ID);

	}

}
