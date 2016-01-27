/*********************************************************************************************
 *
 *
 * 'ModelingPerspective.java', in plugin 'msi.gama.application', is part of the source code of the
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

public class ModelingPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		layout.addPerspectiveShortcut(IGui.PERSPECTIVE_SIMULATION_ID);

		// layout.setFixed(false);
		// String editorId = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		// String navId = "msi.gama.gui.view.GamaNavigator";
		// Positioning the navigator
		// IPlaceholderFolderLayout navFolder =
		// layout.createPlaceholderFolder("NavFolder", IPageLayout.LEFT, 0.25f, editorId);
		// navFolder.addPlaceholder(navId);
		// layout.addView(navId, IPageLayout.LEFT, 0.3f, editorId);
		// IViewLayout v = layout.getViewLayout(navId);
		// v.setMoveable(false);
		// And the outline below it
		// IPlaceholderFolderLayout outlineFolder =
		// layout.createPlaceholderFolder("OutlineFolder", IPageLayout.BOTTOM, 0.5f, navId);
		// outlineFolder.addPlaceholder("msi.gama.application.outline");
		// outlineFolder.addPlaceholder("msi.gama.application.problems");
		// layout.addView("msi.gama.application.outline", IPageLayout.BOTTOM, 0.5f, navId);
		// Positioning the problem view below
		// IPlaceholderFolderLayout problemFolder =
		// layout.addView("msi.gama.application.problems", IPageLayout.BOTTOM, 0.66f, editorId);
		// problemFolder.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
		// layout.addView
		// And finally the browser
		// IPlaceholderFolderLayout browserFolder =
		// layout.createPlaceholderFolder("BrowserFolder", IPageLayout.RIGHT, 0.66f, editorId);
		// browserFolder.addPlaceholder(BrowserView.ID);

		// layout.addNewWizardShortcut("msi.gama.gui.wizards.NewFileWizard");
		// layout.addNewWizardShortcut("msi.gama.gui.wizards.NewProjectWizard");

		// IDEActionFactory.BUILD;
	}
}
