package msi.gama.hpc.gui.perspective;

import msi.gama.common.util.GuiUtils;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.IViewLayout;

public class HPCPerspectiveFactory implements IPerspectiveFactory {


	/** The Constant ID of the perspective */
	public static final String ID = GuiUtils.HPC_PERSPECTIVE_ID;
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		System.out.println("stttaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		String editorId = layout.getEditorArea();
		layout.addView("msi.gama.hpc.gui.navigator.OutputNavigator", IPageLayout.LEFT, 0.23f, editorId);
		layout.addView("msi.gama.hpc.gui.experiment.navigator.ExperimentNavigator", IPageLayout.BOTTOM, 0.23f, editorId);
		//layout.getViewLayout("msi.gama.hpc.gui.experiment.navigator.ExperimentNavigator");
	//	IViewLayout nav = layout.getViewLayout("msi.gama.hpc.chartView");
	//	nav.setMoveable(false);

		layout.addView("msi.gama.hpc.chartView", IPageLayout.TOP, 0.70f,
				editorId);


		/*layout.addView("msi.gama.application.view.ConsoleView", IPageLayout.BOTTOM, 0.70f,
			"msi.gama.gui.view.GamaNavigator");*/
	}

}
